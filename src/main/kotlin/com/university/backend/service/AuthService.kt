package com.university.backend.service

import com.university.backend.dto.LoginRequest
import com.university.backend.person.Person
import com.university.backend.person.repository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val personRepository: repository,
    private val passwordEncoder: PasswordEncoder
) {

    fun authenticateUser(request: LoginRequest): Person {

        // 1. Find user by email
        val person = personRepository.findByEmail(request.email)
            ?: throw RuntimeException("Invalid email or password")

        // 2. Verify password
        val isPasswordCorrect = passwordEncoder.matches(
            request.password,
            person.password
        )

        if (!isPasswordCorrect) {
            throw RuntimeException("Invalid email or password")
        }

        // 3. Return authenticated user
        return person
    }
}
