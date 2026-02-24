package com.wuubzi.auth

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class Hello {


    @GetMapping("/hello")
    fun hello(): String { 
        return "Hello, World!"
    }
}