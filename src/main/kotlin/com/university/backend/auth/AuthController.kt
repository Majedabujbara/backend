package com.university.backend.auth

import com.university.backend.registration.dtos.RegisterRequest
import com.university.backend.registration.dtos.RegisterResponse
import com.university.backend.registration.services.RegistrationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registrationService: RegistrationService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<RegisterResponse> {
        val response = registrationService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}