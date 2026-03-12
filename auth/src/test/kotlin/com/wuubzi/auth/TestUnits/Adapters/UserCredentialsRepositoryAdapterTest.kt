package com.wuubzi.auth.TestUnits.Adapters

import com.wuubzi.auth.domain.models.UserCredentials
import com.wuubzi.auth.infrastructure.Adapters.UserCredentialsRepositoryAdapter
import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.util.UUID


@ExtendWith(MockitoExtension::class)
class UserCredentialsRepositoryAdapterTest {

    @Mock
    lateinit var userCredentialsRepository: UserCredentialsRepository

    @InjectMocks
    lateinit var adapter: UserCredentialsRepositoryAdapter

    @Test
    fun shouldSaveUserCredentials() {
        // GIVEN
        val domainUser = UserCredentials(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            email = "test@wuubzi.com",
            password = "hashedPassword",
            role = "USER",
            isActive = true,
            createdAt = Timestamp(System.currentTimeMillis())
        )

        val entityResponse = UserCredentialsEntity().apply {
            id = domainUser.id
            userId = domainUser.userId
            email = domainUser.email
            password = domainUser.password
            role = domainUser.role
            isActive = domainUser.isActive
            createdAt = domainUser.createdAt
        }

        whenever(userCredentialsRepository.save(any<UserCredentialsEntity>())).thenReturn(entityResponse)

        // WHEN
        val result = adapter.save(domainUser)

        // THEN
        assertEquals(domainUser.email, result.email)
        verify(userCredentialsRepository).save(any<UserCredentialsEntity>())
    }

    @Test
    fun shouldFindByEmailSuccessfully() {
        // GIVEN
        val email = "test@wuubzi.com"
        val entity = UserCredentialsEntity().apply {
            id = UUID.randomUUID()
            userId = UUID.randomUUID()
            this.email = email
            this.password = "pass"
            this.role = "USER"
            this.isActive = true
            this.createdAt = Timestamp(System.currentTimeMillis())
        }

        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(entity)

        // WHEN
        val result = adapter.findByEmail(email)

        // THEN
        assertEquals(email, result?.email)
    }

    @Test
    fun shouldReturnNullWhenEmailNotFound() {
        // GIVEN
        val email = "notfound@wuubzi.com"
        whenever(userCredentialsRepository.findByEmail(email)).thenReturn(null)

        // WHEN
        val result = adapter.findByEmail(email)

        // THEN
        assertNull(result)
    }
}