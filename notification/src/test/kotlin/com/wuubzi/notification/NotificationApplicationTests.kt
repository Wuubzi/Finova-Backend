package com.wuubzi.notification

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	properties = [
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration",
		"spring.cloud.config.enabled=false",
		"spring.config.import=optional:",
		"eureka.client.enabled=false"
	]
)
class NotificationApplicationTests {

	@Test
	fun contextLoads() {
	}

}
