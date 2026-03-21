package com.wuubzi.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class User

fun main(args: Array<String>) {
	runApplication<User>(*args)
}
