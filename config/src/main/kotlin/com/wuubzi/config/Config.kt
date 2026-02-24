package com.wuubzi.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class Config

fun main(args: Array<String>) {
	runApplication<Config>(*args)
}
