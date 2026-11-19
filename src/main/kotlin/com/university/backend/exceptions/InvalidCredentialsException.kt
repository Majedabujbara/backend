package com.university.backend.exceptions

class InvalidCredentialsException(
    message: String = "Invalid email or password"
) : RuntimeException(message)