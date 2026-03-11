package com.wuubzi.auth.IntegrationTests.Controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.wuubzi.auth.Utils.DateFormatter
import com.wuubzi.auth.Utils.Jwt
import com.wuubzi.auth.application.DTOS.Request.*
import com.wuubzi.auth.application.DTOS.Response.TokenResponse
import com.wuubzi.auth.application.Ports.`in`.*
import com.wuubzi.auth.infrastructure.Controllers.AuthController
import com.wuubzi.auth.infrastructure.Security.CustomUserDetailsService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {



    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private var jwt = Jwt()

    @MockitoBean
    lateinit var register: CreateUserUseCase

    @MockitoBean
    lateinit var login: LoginUserUseCase

    @MockitoBean
    lateinit var logoutUseCase: LogoutUseCase

    @MockitoBean
    lateinit var recoverPasswordUseCase: RecoverPasswordUseCase

    @MockitoBean
    lateinit var validateOTPUseCase: ValidateOTPUseCase

    @MockitoBean
    lateinit var changePasswordUseCase: ChangePasswordUseCase

    @MockitoBean
    lateinit var customUserDetailsService: CustomUserDetailsService

    @MockitoBean
    lateinit var refreshTokenUseCase: RefreshTokenUseCase

    @MockitoBean
    lateinit var dateFormatter: DateFormatter

    @Test
    fun `should register user`() {

        val request = UserRequest(
            firstName = "Test",
            lastName = "User",
            documentNumber = "123456789",
            phoneNumber = "123456789",
            address = "Test Address",
            email = "test@test.com",
            password = "123456"
        )

        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"))

        verify(register).createUser(request)
    }

    @Test
    fun `should login user`() {

        val request = LoginRequest(
            email = "test@test.com",
            password = "123456"
        )

        val tokenResponse =  TokenResponse(
            accessToken = "access",
            refreshToken = "refresh"
        )

        `when`(login.login(request)).thenReturn(tokenResponse)
        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("access"))

        verify(login).login(request)
    }

    @Test
    fun `should logout`() {

        val request = LogoutRequest("refresh-token")

        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Logout exitoso"))

        verify(logoutUseCase).logout("refresh-token")
    }

    @Test
    fun `should recover password`() {

        val request = RecoverPasswordRequest("test@test.com")

        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/recover-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)

        verify(recoverPasswordUseCase).recoverPassword("test@test.com")
    }

    @Test
    fun `should validate otp`() {

        val request = ValidateOTPRequest(
            email = "test@test.com",
            otp = "123456"
        )

        `when`(validateOTPUseCase.validateOTP(request)).thenReturn("reset-token")
        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/validate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resetToken").value("reset-token"))

        verify(validateOTPUseCase).validateOTP(request)
    }

    @Test
    fun `should change password`() {

        val request = ChangePasswordRequest(
            email = "correo@ejemplo.com",
            resetToken = "reset",
            password = "123456",
            confirmPassword = "123456"
        )

        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)

        verify(changePasswordUseCase).changePassword(request)
    }

    @Test
    fun `should refresh token`() {

        val request = RefreshTokenRequest("refresh")

        `when`(refreshTokenUseCase.refreshToken(request)).thenReturn("new-token")
        `when`(dateFormatter.getDate()).thenReturn("2026-01-01")

        mockMvc.perform(
            post("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("new-token"))

        verify(refreshTokenUseCase).refreshToken(request)
    }
}