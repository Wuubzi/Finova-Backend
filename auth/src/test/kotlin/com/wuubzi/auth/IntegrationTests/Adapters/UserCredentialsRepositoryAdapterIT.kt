package com.wuubzi.auth.IntegrationTests.Adapters

import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Adapters.UserCredentialsRepositoryAdapter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.sql.Timestamp
import java.util.UUID

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserCredentialsRepositoryAdapter::class)
class UserCredentialsRepositoryAdapterIT {

    @Autowired
    lateinit var adapter: UserCredentialsRepositoryAdapter

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("auth_db")
            .withUsername("test")
            .withPassword("test")

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
    }
}