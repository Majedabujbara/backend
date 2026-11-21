package com.university.backend.login.services

import com.university.backend.exceptions.InvalidCredentialsException
import com.university.backend.login.dtos.LoginRequest
import com.university.backend.login.dtos.LoginResponse
import com.university.backend.person.PersonRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.university.backend.security.JwtService

@Service
class LoginService(
    private val personRepository: PersonRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService

) {

    fun login(request: LoginRequest): LoginResponse {
        val normalizedEmail = request.email.trim().lowercase()

        val person = personRepository.findByEmail(normalizedEmail)
            .orElseThrow { InvalidCredentialsException("Invalid email or password") }

        if (!passwordEncoder.matches(request.password, person.passwordHash)) {
            throw InvalidCredentialsException("Invalid email or password")
        }

        val roleName = person.role?.roleName ?: ""
        val personId = person.id?.toInt()
            ?: throw IllegalStateException("Person ID is missing")


        val token = jwtService.generateToken(normalizedEmail)

        return LoginResponse(
            accessToken = token,
            personId = personId,
            fullName = person.fullName,
            email = person.email,
            roleName = roleName
        )
    }
}