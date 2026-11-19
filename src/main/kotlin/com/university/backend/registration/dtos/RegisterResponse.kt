package com.university.backend.registration.dtos

data class RegisterResponse(

    val personId: Int,

    val fullName: String,

    val email: String,

    val roleName: String
)