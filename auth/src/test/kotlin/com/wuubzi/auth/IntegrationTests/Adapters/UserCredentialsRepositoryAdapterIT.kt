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
import org.testcontainers.containers.PostgreSQLContainer  // ← Importación correcta
import java.sql.Timestamp
import java.util.UUID

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserCredentialsRepositoryAdapter::class)
class UserCredentialsRepositoryAdapterIT {

    @Autowired
    lateinit var adapter: UserCredentialsRepositoryAdapter

    companion object {
        // Detectar si estamos en Jenkins
        private val isJenkins: Boolean =
            System.getenv("JENKINS_HOME") != null ||
                    System.getenv("JENKINS_URL") != null

        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<Nothing>? =  // ← Tipo correcto: PostgreSQLContainer<Nothing>
            if (!isJenkins) {
                PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
                    withDatabaseName("auth_db")
                    withUsername("test")
                    withPassword("test")
                }
            } else {
                null
            }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // Usar estructura if-else simple en lugar de when
            if (isJenkins) {
                // Jenkins: usar postgres-test del docker-compose
                registry.add("spring.datasource.url") { "jdbc:postgresql://postgres-test:5432/auth_db" }
                registry.add("spring.datasource.username") { "test" }
                registry.add("spring.datasource.password") { "test" }
            } else {
                // Local: usar Testcontainers
                postgres?.apply {  // ← Usar apply en lugar de let
                    registry.add("spring.datasource.url") { jdbcUrl }
                    registry.add("spring.datasource.username") { username }
                    registry.add("spring.datasource.password") { password }
                }
            }
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