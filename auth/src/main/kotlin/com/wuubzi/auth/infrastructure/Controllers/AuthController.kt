package com.wuubzi.auth.infrastructure.Controllers

import com.wuubzi.auth.Utils.DateFormatter
import com.wuubzi.auth.application.DTOS.Request.ChangePasswordRequest
import com.wuubzi.auth.application.DTOS.Request.LoginRequest
import com.wuubzi.auth.application.DTOS.Request.LogoutRequest
import com.wuubzi.auth.application.DTOS.Request.RecoverPasswordRequest
import com.wuubzi.auth.application.DTOS.Request.RefreshTokenRequest
import com.wuubzi.auth.application.Ports.`in`.CreateUserUseCase
import com.wuubzi.auth.application.DTOS.Request.UserRequest
import com.wuubzi.auth.application.DTOS.Request.ValidateOTPRequest
import com.wuubzi.auth.application.DTOS.Response.AuthResponse
import com.wuubzi.auth.application.DTOS.Response.LoginResponse
import com.wuubzi.auth.application.DTOS.Response.Response
import com.wuubzi.auth.application.DTOS.Response.ValidateOtpResponse
import com.wuubzi.auth.application.Ports.`in`.ChangePasswordUseCase
import com.wuubzi.auth.application.Ports.`in`.LoginUserUseCase
import com.wuubzi.auth.application.Ports.`in`.LogoutUseCase
import com.wuubzi.auth.application.Ports.`in`.RecoverPasswordUseCase
import com.wuubzi.auth.application.Ports.`in`.RefreshTokenUseCase
import com.wuubzi.auth.application.Ports.`in`.ValidateOTPUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth/")
class AuthController(
    private val register: CreateUserUseCase,
    private val login: LoginUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val recoverPasswordUseCase: RecoverPasswordUseCase,
    private  val validateOTPUseCase: ValidateOTPUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val dateFormatter: DateFormatter
){

    @PostMapping("register")
    fun register(@Valid @RequestBody userRequest: UserRequest, request: HttpServletRequest ): Response {
        register.createUser(userRequest)
        return Response (
            message = "Usuario registrado exitosamente",
            url = request.requestURL.toString(),
            code = HttpServletResponse.SC_CREATED,
            timestamp = dateFormatter.getDate()
        )
    }

    @PostMapping("login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest, request: HttpServletRequest): LoginResponse {

       val tokenResponse  = login.login(loginRequest)
        return LoginResponse (
            message = "Usuario registrado exitosamente",
            url = request.requestURL.toString(),
            token = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            code = HttpServletResponse.SC_CREATED,
            timestamp = dateFormatter.getDate()
        )
    }

    @PostMapping("/logout")
    fun logout(@Valid @RequestBody refreshToken: LogoutRequest, request: HttpServletRequest): Response {
        logoutUseCase.logout(refreshToken.refreshToken)
        return Response(
            message = "Logout exitoso",
            url = request.requestURL.toString() ,
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )
    }

    @PostMapping("/recover-password")
    fun recoverPassword(@Valid @RequestBody recoverRequest: RecoverPasswordRequest, request: HttpServletRequest): Response {
        recoverPasswordUseCase.recoverPassword(recoverRequest.email)
        return Response(
            message = "A verification code has been sent to your email.",
            url = request.requestURL.toString(),
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )
    }

    @PostMapping("/validate-otp")
    fun validateOtp(@Valid @RequestBody validateOTPRequest: ValidateOTPRequest, request: HttpServletRequest): ValidateOtpResponse {
       val resetToken = validateOTPUseCase.validateOTP(validateOTPRequest)
        return ValidateOtpResponse(
            message = "Code verified successfully. You can now reset your password.",
            url = request.requestURL.toString(),
            resetToken = resetToken,
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )
    }

    @PostMapping("/change-password")
    fun changePassword(@Valid @RequestBody changePasswordRequest: ChangePasswordRequest, request: HttpServletRequest): Response {
        changePasswordUseCase.changePassword(changePasswordRequest)
        return Response(
            message = "Password changed successfully",
            url = request.requestURL.toString(),
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest,request: HttpServletRequest): AuthResponse {
        val token = refreshTokenUseCase.refreshToken(refreshTokenRequest)
        return AuthResponse(
            message = "Token refreshed successfully",
            url = request.requestURL.toString(),
            token = token,
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )
    }



}