package com.xzlzx.groovyservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzlzx.groovyservice.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

	List<SysUser> getSysUsers();

	Integer updateTenantId();

	def test()
}

