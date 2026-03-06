package com.wuubzi.auth.infrastructure.Controllers

import com.wuubzi.auth.application.Ports.`in`.CreateUserUseCase
import com.wuubzi.auth.application.Ports.`in`.LoginUserUseCase
import com.wuubzi.auth.infrastructure.Persistence.DTOS.Request.UserRequest
import com.wuubzi.auth.infrastructure.Persistence.DTOS.Response.Response
import com.wuubzi.auth.infrastructure.Persistence.Mappers.toDomain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("api/v1/auth/")
class AuthController(
    private val register: CreateUserUseCase,
){

    @PostMapping("register")
    fun register(@Valid @RequestBody userRequest: UserRequest, request: HttpServletRequest ): Response {
        register.createUser(userRequest.toDomain())
        return Response (
            message = "Usuario registrado exitosamente",
            url = request.requestURL.toString(),
            code = HttpServletResponse.SC_CREATED,
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    @PostMapping("login")
    fun login(): String {
        return "Login"
    }


}