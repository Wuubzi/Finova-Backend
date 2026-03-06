package com.wuubzi.auth.infrastructure.Security


import com.wuubzi.auth.infrastructure.Persistence.Entities.UserCredentialsEntity
import com.wuubzi.auth.infrastructure.Repositories.UserCredentialsRepository
import io.jsonwebtoken.lang.Collections
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    val userCredentialsRepository: UserCredentialsRepository
): UserDetailsService  {
     override fun loadUserByUsername(username: String): UserDetails {
         val user: UserCredentialsEntity = userCredentialsRepository.findByEmail(username)
             ?: throw RuntimeException("User not found with username: $username")
         return org.springframework.security.core.userdetails.User(
             user.email ?: "",
               user.password ?: "",
             Collections.emptyList()
         )
    }
}