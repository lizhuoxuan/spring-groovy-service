package com.xzlzx.groovyservice.config

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.sql.DataSource

@Configuration
class DruidConfig {

    @Bean
    DataSource druidDataSource() {
        return DruidDataSourceBuilder.create().build()
    }

    @Bean
    GroovySQL db() {
        return new GroovySQL(druidDataSource())
    }
}
