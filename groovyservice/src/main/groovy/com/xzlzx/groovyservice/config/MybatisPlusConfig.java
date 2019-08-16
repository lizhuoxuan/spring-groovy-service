package com.xzlzx.groovyservice.config;

import com.xzlzx.groovyservice.interceptor.DataScopeInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lengleng
 * @date 2017/10/29
 */
@Configuration
@MapperScan("com.xzlzx.groovyservice.mapper")
public class MybatisPlusConfig {

	@Bean
	@ConditionalOnMissingBean
	public DataScopeInterceptor dataScopeInterceptor(DataSource dataSource) {
		return new DataScopeInterceptor(dataSource);
	}
}
