package com.wuubzi.user.Adapters

import com.wuubzi.user.domain.Models.User
import com.wuubzi.user.infrastructure.Adapters.UserRepositoryAdapter
import com.wuubzi.user.infrastructure.Persistence.Entities.UserEntity
import com.wuubzi.user.infrastructure.Repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
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
class UserRepositoryAdapterTest {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var adapter: UserRepositoryAdapter

    private val userId = UUID.randomUUID()
    private val documentNumber = "12345678"

    @Test
    fun shouldSaveUserSuccessfully() {
        // GIVEN
        val domainUser = User(
            idUser = userId,
            firstName = "Junior",
            lastName = "Tu Pa",
            phone = "12312312312",
            documentNumber = documentNumber,
            address = "Calle 123",
            profileUrl = "http://example.com/profile.jpg",
            createdAt = Timestamp(
                System.currentTimeMillis()
            )
        )
        val entityResponse = UserEntity().apply {
            idUser = userId
            firstName = "Junior"
            lastName = "Tu Papa"
        }
        whenever(userRepository.save(any<UserEntity>())).thenReturn(entityResponse)

        // WHEN
        val result = adapter.save(domainUser)

        // THEN
        assertEquals(domainUser.idUser, result.idUser)
        verify(userRepository).save(any())
    }

    @Test
    fun shouldDeleteUserSuccessfully() {
        // WHEN
        adapter.delete(userId)

        // THEN
        verify(userRepository).deleteByIdUser(userId)
    }

    @Test
    fun shouldFindByIdUserSuccessfully() {
        // GIVEN
        val entity = UserEntity().apply {
            idUser = userId
            firstName = "Test"
        }
        whenever(userRepository.findByIdUser(userId)).thenReturn(entity)

        // WHEN
        val result = adapter.findByIdUser(userId)

        // THEN
        assertEquals(userId, result?.idUser)
        verify(userRepository).findByIdUser(userId)
    }

    @Test
    fun shouldReturnNullWhenIdNotFound() {
        // GIVEN
        whenever(userRepository.findByIdUser(userId)).thenReturn(null)

        // WHEN
        val result = adapter.findByIdUser(userId)

        // THEN
        assertNull(result)
        verify(userRepository).findByIdUser(userId)
    }

    @Test
    fun shouldFindByDocumentNumberSuccessfully() {
        // GIVEN
        val expectedDoc = "12345678"
        val entity = UserEntity().apply {
            this.idUser = userId
            this.documentNumber = expectedDoc
            this.firstName = "Junior"
            this.lastName = "Tu Pap"
            this.address = "Calle 72"
            this.phone = "3000000"
            this.profileUrl = "http://image.com"
            this.createdAt = java.sql.Timestamp(System.currentTimeMillis())
        }

        // Configuramos el mock para que cuando busquen "12345678", devuelva nuestra entidad
        whenever(userRepository.findByDocumentNumber(expectedDoc)).thenReturn(entity)

        // WHEN
        val result = adapter.findByDocumentNumber(expectedDoc)

        // THEN
        assertNotNull(result)
        assertEquals(expectedDoc, result?.documentNumber) // Aquí ya no debería dar "00000000"
        verify(userRepository).findByDocumentNumber(expectedDoc)
    }

    @Test
    fun shouldReturnNullWhenDocumentNumberNotFound() {
        // GIVEN
        whenever(userRepository.findByDocumentNumber(documentNumber)).thenReturn(null)

        // WHEN
        val result = adapter.findByDocumentNumber(documentNumber)

        // THEN
        assertNull(result)
        verify(userRepository).findByDocumentNumber(documentNumber)
    }
}