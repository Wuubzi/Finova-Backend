package com.wuubzi.auth.IntegrationTests.Adapters

import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Adapters.UserCredentialsRepositoryAdapter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.sql.Timestamp
import java.util.UUID

@DataJpaTest()
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserCredentialsRepositoryAdapter::class)
class UserCredentialsRepositoryAdapterIT {

    @Autowired
    lateinit var adapter: UserCredentialsRepositoryAdapter

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:15-alpine").apply {
            withDatabaseName("auth_db")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }

        }
    }

    @Test
    fun shouldPersistCredentialsInARealPostgresContainer() {
        val domainUser = UserCredentials(
            email = "test@wuubzi.com",
            password = "123",
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            role = "USER",
            isActive = true,
            createdAt = Timestamp(System.currentTimeMillis())
        )

        val result = adapter.save(domainUser)

        assertThat(result.email).isEqualTo("test@wuubzi.com")
        assertThat(result.id).isNotNull()
    }
}