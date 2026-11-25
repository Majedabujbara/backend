package com.university.backend.login.dtos

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val personId: Int,
    val fullName: String,
    val email: String,
    val roleName: String
)