package com.wuubzi.auth.Services

import com.wuubzi.auth.application.DTOS.Events.UserCreated
import com.wuubzi.auth.application.DTOS.Request.UserRequest
import com.wuubzi.auth.application.Exceptions.EmailAlreadyExist
import com.wuubzi.auth.application.Ports.out.KafkaPort
import com.wuubzi.auth.application.Ports.out.PasswordEncoderPort
import com.wuubzi.auth.application.Ports.out.BucketPort
import com.wuubzi.auth.application.Ports.out.FileValidationPort
import com.wuubzi.auth.application.Ports.out.UserCredentialsRepositoryPort
import com.wuubzi.auth.application.Services.CreateUserService
import com.wuubzi.auth.domain.models.UserCredentials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockMultipartFile
import java.sql.Timestamp
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class CreateUserServiceTest {

    @Mock
    lateinit var userCredentialsRepository: UserCredentialsRepositoryPort

    @Mock
    lateinit var passwordEncoder: PasswordEncoderPort

    @Mock
    lateinit var kafkaPort: KafkaPort

    @Mock
    lateinit var fileValidation: FileValidationPort  // ← AGREGADO

    @Mock
    lateinit var bucketPort: BucketPort  // ← AGREGADO

    @InjectMocks
    lateinit var createUserService: CreateUserService

    private val userRequest = UserRequest(
        email = "nuevo@wuubzi.com",
        password = "password123",
        firstName = "Carlos",
        lastName = "Perez",
        documentNumber = "123456",
        phoneNumber = "3001234567",
        address = "Calle 123"
    )

    private val file = MockMultipartFile(
        "profilePicture",
        "profile.jpg",
        "image/jpeg",
        "fake-image-content".toByteArray()
    )

    @Test
    fun shouldCreateUserSuccessfully() {
        // GIVEN
        val profileUrl = "https://bucket.example.com/profiles/user-profile.jpg"

        whenever(userCredentialsRepository.findByEmail(userRequest.email)).thenReturn(null)
        whenever(passwordEncoder.encode(userRequest.password)).thenReturn("encodedPassword")
        whenever(bucketPort.saveBucket(file)).thenReturn(profileUrl)  // Mock del bucket

        // Mockeamos el save para que devuelva lo que recibe
        whenever(userCredentialsRepository.save(any())).thenAnswer { it.arguments[0] as UserCredentials }

        // WHEN
        val result = createUserService.createUser(userRequest, file)

        // THEN
        // 1. Verificar que se validó el archivo
        verify(fileValidation).validate(file)

        // 2. Verificar que se guardó en el bucket
        verify(bucketPort).saveBucket(file)

        // 3. Verificar que se codificó la contraseña
        assertEquals("encodedPassword", result.password)
        assertEquals(userRequest.email, result.email)

        // 4. Verificar que se publicó el evento en Kafka con los datos correctos
        val eventCaptor = argumentCaptor<UserCreated>()
        verify(kafkaPort).publishUserCreated(eventCaptor.capture())

        val capturedEvent = eventCaptor.firstValue
        assertEquals(userRequest.firstName, capturedEvent.firstName)
        assertEquals(userRequest.documentNumber, capturedEvent.documentNumber)
        assertEquals(result.userId, capturedEvent.idUser)
        assertEquals(profileUrl, capturedEvent.profileUrl)  // Verificar que se pasó la URL

        // 5. Verificar que se persistió en el repositorio
        verify(userCredentialsRepository).save(any())
    }

    @Test
    fun shouldThrowEmailAlreadyExistException() {
        // GIVEN
        val existingUser = UserCredentials(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            email = userRequest.email,
            password = "hashedPassword",
            role = "USER",
            isActive = true,
            createdAt = Timestamp(System.currentTimeMillis())
        )

        whenever(userCredentialsRepository.findByEmail(userRequest.email)).thenReturn(existingUser)

        // WHEN & THEN
        val exception = assertThrows(EmailAlreadyExist::class.java) {
            createUserService.createUser(userRequest, file)
        }

        assertEquals("User with email ${userRequest.email} already exists", exception.message)

        // Verificar que no se validó archivo, no se guardó en bucket, no se llamó a Kafka ni se guardó nada nuevo
        verify(fileValidation, org.mockito.Mockito.never()).validate(any())
        verify(bucketPort, org.mockito.Mockito.never()).saveBucket(any())
        verify(kafkaPort, org.mockito.Mockito.never()).publishUserCreated(any())
        verify(userCredentialsRepository, org.mockito.Mockito.never()).save(any())
    }
}
