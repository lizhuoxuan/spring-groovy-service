package com.xzlzx.groovyservice.controller

import com.xzlzx.groovyservice.config.GroovySQL
import com.xzlzx.groovyservice.service.SysUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController {

    @Autowired
    GroovySQL db

    @Autowired
    SysUserService sysUserService

    @GetMapping("/test/{id}")
    def getDemo(@PathVariable String id) {
        return "get test" + id
    }

    @GetMapping("/test/db")
    def dbDemo() {
//        Stream.generate(Math.&random).limit(100).parallel().forEach({
//            db.withTransaction {
//                db.executeUpdate("update sys_user set map_center=1 where user_id = 1")
//                db.withTransaction {
//                    db.firstRow("select 2")
////                    int a = 1/0
//                }
////                db.firstRow("select (2/0)")
//            }
//        })
//        println sysUserService.getById(1)
//        return db.firstRow("select * from sys_user")
        return sysUserService.test()
//        return "123"
    }


}
