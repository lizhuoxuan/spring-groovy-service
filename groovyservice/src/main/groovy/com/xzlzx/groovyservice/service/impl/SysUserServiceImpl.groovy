package com.xzlzx.groovyservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.xzlzx.groovyservice.config.GroovySQL;
import com.xzlzx.groovyservice.entity.SysUser;
import com.xzlzx.groovyservice.mapper.SysUserMapper;
import com.xzlzx.groovyservice.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.sql.DataSource;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	@Autowired
	SysUserMapper sysUserMapper;

//	@Autowired
//	GroovySQL db

	@Autowired
	DataSource dataSource

	@Override
	public List<SysUser> getSysUsers() {
		return sysUserMapper.getSysUsers();
	}

	@Override
	Integer updateTenantId() {
		return sysUserMapper.updateTenantId();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	def test() {
		try {
			def db = GroovySQL.newInstance(dataSource)
			db.executeUpdate("update sys_user set map_center=1 where user_id = 1")
			int a = 1/0
			def res = db.firstRow("select map_center from sys_user where user_id = 1")
			return res
		} catch(e) {
			throw e
		} finally {
			return "123"
		}


	}
}

