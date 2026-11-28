package com.university.backend.refresh.dtos

data class RefreshResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)
