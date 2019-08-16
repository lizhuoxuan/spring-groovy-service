package com.xzlzx.groovyservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.xzlzx.groovyservice.entity.SysUser
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface SysUserMapper extends BaseMapper<SysUser> {

	@Select("select * from sys_user")
	List<SysUser> getSysUsers();

	@Update("update sys_user set test_id=(not test_id) where user_id = 1")
	Integer updateTenantId();
}
