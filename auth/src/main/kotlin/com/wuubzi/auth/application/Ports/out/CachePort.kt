package com.wuubzi.auth.application.Ports.out

import java.time.Duration

interface CachePort {
    fun save(key: String, value: String, duration: Duration)
    fun get(key: String): String?
    fun delete(key: String)
}