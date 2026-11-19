package com.university.backend.com.university.backend.dto

import java.util.UUID

data class LoginResponse(
    val token: String,
    val personId: UUID,
    val fullName: String,
    val email: String,
    val role: String
)
