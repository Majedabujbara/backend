package com.university.backend.employees.dtos

data class EmployeeResponse(
    val id: Long,
    val fullName: String,
    val email: String,
    val phone: String,
    val cafeteriaId: Int?,
    val cafeteriaName: String?
)