package com.university.backend.person.dtos

data class UserResponse(
    val personId: Long,
    val fullName: String,
    val email: String,
    val phone: String,
    val roleName: String
)