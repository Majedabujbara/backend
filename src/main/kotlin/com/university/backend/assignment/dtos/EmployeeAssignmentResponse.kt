package com.university.backend.assignment.dtos

data class EmployeeAssignmentResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val cafeteriaId: Int,
    val cafeteria: CafeteriaSummaryResponse
)

