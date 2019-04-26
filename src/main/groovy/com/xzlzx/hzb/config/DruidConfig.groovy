package com.xzlzx.hzb.config

import com.alibaba.druid.pool.DruidDataSource
import groovy.sql.Sql
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.sql.DataSource

@Configuration
class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource druidDataSource() {
        return new DruidDataSource()
    }

    @Bean
    Sql db() {
        return new Sql(druidDataSource())
    }
}
