package com.xzlzx.hzb.controller

import groovy.sql.Sql
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
@ResponseBody
class DemoController {

    @Autowired
    Sql db


    @GetMapping("/test/{id}")
    def getDemo(@PathVariable String id) {
        return "get test" + id
    }

    @GetMapping("/test/db")
    def dbDemo() {
        def a = db.firstRow("select 1 as haha")
        return a
    }


}
