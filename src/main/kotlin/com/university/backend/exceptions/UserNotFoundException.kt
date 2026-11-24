package com.university.backend.exceptions

class UserNotFoundException(
    message: String
) : RuntimeException(message)