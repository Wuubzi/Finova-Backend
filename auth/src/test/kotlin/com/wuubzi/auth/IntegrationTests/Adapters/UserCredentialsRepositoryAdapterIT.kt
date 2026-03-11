package com.wuubzi.auth.IntegrationTests.Adapters

import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Adapters.UserCredentialsRepositoryAdapter
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.sql.Timestamp
import java.util.UUID

@DataJpaTest
@Testcontainers
@Import(UserCredentialsRepositoryAdapter::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserCredentialsRepositoryAdapterIT {

    @Autowired
    lateinit var adapter: UserCredentialsRepositoryAdapter

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("auth_db")
            .withUsername("test")
            .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }

        init {
            postgres.start()
        }
    }


    @Test
    fun `should persist credentials in a real postgres container`() {
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