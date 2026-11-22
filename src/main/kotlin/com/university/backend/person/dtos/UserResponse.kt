package com.university.backend.person.dtos

data class UserResponse(
    val personId: Long,
    val fullName: String,
    val email: String,
    val roleName: String
)