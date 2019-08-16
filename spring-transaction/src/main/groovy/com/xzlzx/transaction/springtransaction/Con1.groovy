package com.xzlzx.transaction.springtransaction


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Con1 {

    @Autowired
    GroovyService groovyService

    @GetMapping("/test")
    def test() {
        return groovyService.test()
    }
}
