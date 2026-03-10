package com.wuubzi.gateway

import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test


@SpringBootTest
@ActiveProfiles("test")
class testTests {

    @Value($$"${profile.property.value}")
    private val propertyString: String? = null

    @Test
    fun whenTestIsActive_thenValueShouldBeKeptFromApplicationTestYaml() {
        Assertions.assertEquals("This the the application-test.yaml file", propertyString)
    }
}