package com.university.backend.exceptions

class EmployeeNotFoundException(id: Long) : RuntimeException("Employee not found with id: $id")