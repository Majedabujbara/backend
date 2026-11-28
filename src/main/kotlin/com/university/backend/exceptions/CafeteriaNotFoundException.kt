package com.university.backend.exceptions

class CafeteriaNotFoundException(id: Long) : RuntimeException("Cafeteria not found with id: $id")