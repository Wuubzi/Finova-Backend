package com.wuubzi.eureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class Eureka

fun main(args: Array<String>) {
	runApplication<Eureka>(*args)
}
