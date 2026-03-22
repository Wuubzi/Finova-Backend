package com.wuubzi.user.application.Ports.Out

import java.time.Duration

interface CachePort {
    fun save(key: String, value: String, duration: Duration)
    fun get(key: String): String?
    fun delete(key: String)

    fun <T> saveObject(key: String, value: T, duration: Duration)
    fun <T> getObject(key: String, clazz: Class<T>): T?
}