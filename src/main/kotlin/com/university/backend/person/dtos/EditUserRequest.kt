
package com.university.backend.person.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EditUserRequest(

    @field:NotBlank(message = "Full name is required")
    val fullName: String,

    @field:NotBlank(message = "Phone is required")
    val phone: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String
)
