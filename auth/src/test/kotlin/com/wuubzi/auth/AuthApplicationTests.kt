package com.wuubzi.auth

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
	properties = [
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
		"spring.cloud.config.enabled=false",
		"spring.config.import=optional:",
		"eureka.client.enabled=false"
	]
)
class AuthApplicationTests {

	@Test
	fun contextLoads() {
	}
}