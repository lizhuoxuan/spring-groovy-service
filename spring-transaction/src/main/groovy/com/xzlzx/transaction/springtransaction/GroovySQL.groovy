package com.xzlzx.transaction.springtransaction

import groovy.sql.Sql

import javax.sql.DataSource
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.ConsoleHandler
import java.util.logging.Level

/**
 * Created by liurui on 2017/3/18.
 */
class GroovySQL extends Sql {

    static dbTables = [:]  //缓存数据库表信息

    int dbHash //连接池hashcode

    static {
        LOG.level = Level.FINE
        LOG.addHandler(new ConsoleHandler(level: Level.FINE))
    }

    GroovySQL(DataSource dataSource) {
        super(dataSource)
        dbHash = dataSource.hashCode()
    }

    def saveList(list, String table, String specialType, int batchSize = 100) {
        def sql
        def values
        def keys
        def type

        if (list.size() > 0) {
            String _sql = "insert into $table ( "
            keys = []
            def updateList = []
            list[0].each { it ->
                if (tables[table].contains(it.key)) {
                    keys << it.key
                    _sql += "$it.key,"
                    if (specialType == "update") {
                        if (it.key != "id") {
                            updateList << "${it.key}=VALUES(${it.key})"
                        }
                    }
                }
            }
            _sql = _sql.substring(0, _sql.length() - 1)
            _sql += " ) values "
            def valueList = []
            list.eachWithIndex { item, i ->
                def itemList = []
                item.each { k, v ->
                    itemList << "'${judgeType(k, v)}'"
                }
                valueList << "(${itemList.join(',')})"
                itemList = []
                if ((i + 1) % batchSize == 0 && i != 0 || i == list.size() - 1) {
                    def strSql = ""
                    if (specialType == "update") {
                        strSql = "${_sql} ${valueList.join(',')} ON DUPLICATE KEY UPDATE ${updateList.join(',')}".toString()
                    } else {
                        strSql = "${_sql} ${valueList.join(',')}".toString()
                    }
                    this.execute(strSql)
                    valueList = []
                }
            }
        }
    }

    def saveItem(Map item, String table, String specialType) {
        try {

            if (specialType == 'insert') {//insert
                String _sql = "insert into $table ( "
                def _param = []
                item.each { it ->
                    if (tables[table].contains(it.key)) {
                        _sql += "$it.key,"
//                        it.value = judgeType(it.key, it.value)
                        _param << it.value
                    }
                }
                _sql = _sql.substring(0, _sql.length() - 1)
                _sql += " ) values ("
                _param.size().times {
                    _sql += "?,"
                }
                _sql = _sql.substring(0, _sql.length() - 1)
                _sql += ")"
                return this.executeInsert(_sql, _param)[0][0]
            } else if (specialType == 'update') {//update
                String _sql = "update $table set "
                def _param = []
                item.each { it ->
                    if (it.key != 'id' && tables[table].contains(it.key)) {
                        _sql += "$it.key = :$it.key ,"
//                        it.value = judgeType(it.key, it.value)
                        _param << it.value
                    }
                }
                _sql = _sql.substring(0, _sql.length() - 1)
                _sql += " where id = :id"
                _param << item.id
                return this.executeUpdate(_sql, item)
            }
        } catch (e) {
            e.printStackTrace()
            return false
        }

    }

    /**
     *
     *  ！！！！注意  参数的值  标准类型   String  int  Date等，不可以用GString
     * @param sql select * from table
     * @param condition 数组里装数组  里面的数组  list[0]：列名，list[1]：值，list[2]：操作符 不写 默认是=
     *              [["num", 0, ">="],
     *               ['createtime', '2018-06-04 10:11:11', '>'],
     *               ["name", [1,2], "in"],
     *               ["page", "1"],
     *               ['pageSize', "5"]]
     * @param postfix 排序语句   order by id desc
     * @return
     */
    def select(String sql, List condition = [], String postfix = "") {
        if (condition.size() > 0) {
            String _where = ""
            def param = [:]
            def paramPage = [:]
            def list = []
            condition.eachWithIndex { con, i ->
                def operator = '='
                if (con[0] == 'page' || con[0] == 'pageSize') {
                    paramPage[con[0]] = con[1]
                } else {
                    def colunmName = con[0]
                    if (con.size() == 3) {
                        operator = con[2]
                    }
                    if (operator == 'in') {
                        def paramList = []
                        con[1].eachWithIndex { paramForIn, index ->
                            def paramName = "param${index}_in"
                            param[paramName] = paramForIn
                            paramList << "?.${paramName}"
                        }
                        list << "${colunmName} in (${paramList.join(',')}) "
                    } else {
                        def paramName = "param${i}"
                        param[paramName] = con[1]
                        list << "${colunmName} $operator ?.${paramName} "
                    }
                }
            }
            def pageStr = ''
            if (paramPage) {
                param.page = (paramPage.page.toInteger() - 1) * paramPage.pageSize.toInteger()
                param.pageSize = paramPage.pageSize.toInteger()
                pageStr = " limit ?.page,?.pageSize "
            }
            if (list.size() > 0) {
                _where = " where " + list.join('and ')
            }
            String finalSql = "${sql} ${_where} ${postfix} ${pageStr}".toString()
            return this.rows(finalSql, param)
        } else {
            return this.rows(sql + postfix)
        }
    }

    def judgeType(key, value) {
//        if (key.toString().contains("time") && value instanceof Date) {
//            value = value.format('yyyy-MM-dd HH:mm:ss')
//        }
        if (value instanceof String && (value?.contains("'") || value?.contains("\\"))) {
            value = value.replaceAll("\\\\", "\\\\\\\\")
            value = value.replaceAll("'", "\\\\'")
        }
        return value
    }

    def getTables() {
        // 缓存没有过期，改表需要重启
        if (dbTables.containsKey(dbHash)) {
            return dbTables[dbHash]
        } else {
            def tables = [:]
            this.rows("show tables from ${this.dataSource.dbName}".toString()).each {
                String _table = it["Tables_in_${this.dataSource.dbName}".toString()]
                def cols = []
                this.rows("show columns from $_table".toString()).each { t ->
                    cols << t.Field
                }
                tables[_table] = cols
            }
            dbTables[dbHash] = tables
            return tables
        }
    }

    //转换成驼峰
    def convert(data) {
        if (data instanceof List) {
            def list = []
            data.each { m ->
                list << convert(m)
            }
            return list
        } else if (data instanceof Map) {
            def clone = [:]
            data.each { k, v ->
                if (k.toString().contains('_')) {
                    String m = k.toString().replaceAll('_[A-z]') { ch ->
                        (ch - '_').toUpperCase()
                    }
                    clone[m] = v
                } else {
                    clone[k] = v
                }
            }
            return clone
        }
    }

    private AtomicBoolean isSubTransaction = new AtomicBoolean(false)

    @Override
    void withTransaction(Closure closure) throws SQLException {
        if (isSubTransaction.get()) {
            closure.call()
        } else {
            isSubTransaction.set(true)
            try {
                super.withTransaction(closure)
            } finally {
                isSubTransaction.set(false)
            }
        }
    }

    private AtomicInteger ai = new AtomicInteger(0)
    private int i = 0

    void plus_i() {
       ai.incrementAndGet()
       i++
    }
}
