package com.wuubzi.user.infrastructure.Controllers


import com.wuubzi.user.application.DTOS.Request.UpdateRequestDTO
import com.wuubzi.user.application.DTOS.Response.Response
import com.wuubzi.user.application.Ports.In.DeleteUserUseCase
import com.wuubzi.user.application.Ports.In.GetUserUseCase
import com.wuubzi.user.application.Ports.In.UpdateUserUseCase
import com.wuubzi.user.domain.Models.User
import com.wuubzi.user.utils.DateFormatter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/v1/users/")
class UserController(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val dateFormatter: DateFormatter
) {

    @GetMapping("me")
    fun getUser( @RequestHeader("X-User-Id") userId: UUID): ResponseEntity<User> {
         val user = getUserUseCase.getUser(userId)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PutMapping("me")
    fun updateUser(
        @RequestHeader("X-User-Id") userId: UUID,
        @Valid @RequestBody userRequest: UpdateRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        updateUserUseCase.updateUser(userId, userRequest)
        val responseBody = Response(
            message = "Usuario actualizado exitosamente",
            url = request.requestURL.toString(),
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )
        return ResponseEntity(responseBody, HttpStatus.OK)
    }

    @DeleteMapping("me")
    fun deleteUser(@RequestHeader("X-User-Id") userId: UUID,  request: HttpServletRequest): ResponseEntity<Response> {
        deleteUserUseCase.deleteUser(userId)
        val responseBody = Response(
            message = "Usuario eliminado exitosamente",
            url = request.requestURL.toString(),
            code = HttpServletResponse.SC_OK,
            timestamp = dateFormatter.getDate()
        )

        return ResponseEntity(responseBody, HttpStatus.OK)
    }
}