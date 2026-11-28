package com.university.backend.assignment.dtos

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ReassignEmployeeRequest(
    @field:NotNull(message = "cafeteriaId is required")
    @field:Positive(message = "cafeteriaId must be a positive number")
    val cafeteriaId: Int?
)
