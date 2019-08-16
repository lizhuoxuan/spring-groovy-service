package com.xzlzx.transaction.springtransaction

import com.alibaba.druid.pool.DruidDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.sql.DataSource

@Configuration
class DruidConfig {

    @ConfigurationProperties("spring.datasource.druid")
    @Bean
    DataSource druidDataSource() {
        return new DruidDataSource()
    }

    @Bean
    GroovySQL db() {
        return new GroovySQL(druidDataSource())
    }
}
