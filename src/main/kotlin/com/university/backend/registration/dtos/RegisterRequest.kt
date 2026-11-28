package com.university.backend.registration.dtos

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
data class RegisterRequest(

    @field:NotBlank(message = "Full name is required") //@field:NotBlank: the same def between !isEmpty and IsNotBlank
    @field:Size(max = 75, message = "Full name must not exceed 75 characters")
    @field:Pattern(
        regexp = "^[A-Za-z\\u0600-\\u06FF]+(?: [A-Za-z\\u0600-\\u06FF]+)*$",
        message = "Full name must contain letters only without numbers or special characters"
    )
    val fullName: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    @field:Pattern(
        regexp = "^07[789]\\d{7}$",
        message = "Phone number must be a valid 10-digit Jordanian number starting with 077, 078, or 079"
    )
    val phone: String,

    @field:NotBlank(message = "Email is required")
    @field:Size(max = 150, message = "Email must not exceed 150 characters")
    @field:Email(message = "Please provide a valid email address")
    @field:Email(message = "Please provide a valid email address")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[#@$%]).{8,}$",
        message = "Password must be at least 8 characters, include at least 1 uppercase letter,1 digit,and 1 special character"
    )
    val password: String
)