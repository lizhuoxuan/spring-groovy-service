package com.xzlzx.transaction.springtransaction


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroovyService {

    @Autowired
    GroovySQL db

    def test() {
        db.withTransaction {

            db.executeUpdate("update sys_user set map_center=2 where user_id = 1")
            int a = 1/0
        }
        return db.firstRow("select map_center from sys_user where user_id = 1;")
    }
}
