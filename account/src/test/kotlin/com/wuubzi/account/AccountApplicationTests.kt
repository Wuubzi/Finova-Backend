package com.wuubzi.account

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	properties = [
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
		"spring.cloud.config.enabled=false",
		"spring.config.import=optional:",
		"eureka.client.enabled=false",
		"jwt.secret=dGVzdHNlY3JldGtleWZvcnRlc3RpbmdwdXJwb3Nlc29ubHltaW4yNTZiaXRzbG9uZ2Vub3VnaGZvcmhtYWNzaGEyNTY=",
		"jwt.expiration=3600000"
	]
)
class AccountApplicationTests {

	@Test
	fun contextLoads() {
		//this functions is empty
	}

}
