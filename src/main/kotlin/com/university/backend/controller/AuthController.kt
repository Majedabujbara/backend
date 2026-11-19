package com.university.backend.controller

import com.university.backend.dto.LoginRequest
import com.university.backend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {

        val person = authService.authenticateUser(request)

        val response = mapOf(
            "message" to "Login successful",
            "personId" to person.personId,
            "fullName" to person.fullName,
            "email" to person.email,
            "role" to person.role?.roleName
        )

        return ResponseEntity.ok(response)
    }
}
