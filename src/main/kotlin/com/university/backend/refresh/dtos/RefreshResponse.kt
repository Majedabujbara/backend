package com.university.backend.refresh.dtos

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)
