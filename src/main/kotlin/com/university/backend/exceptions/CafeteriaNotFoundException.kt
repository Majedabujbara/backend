package com.university.backend.exceptions

class CafeteriaNotFoundException(message: String) : RuntimeException(message) {
    constructor(id: Long) : this("Cafeteria not found with id: $id")
    constructor(id: Int) : this("Cafeteria not found with id: $id")
}