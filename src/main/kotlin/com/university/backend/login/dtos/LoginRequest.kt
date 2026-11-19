package com.university.backend.login.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)