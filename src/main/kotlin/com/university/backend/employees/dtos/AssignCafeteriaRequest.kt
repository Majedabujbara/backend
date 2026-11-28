package com.university.backend.employees.dtos

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class AssignCafeteriaRequest(
    @field:NotNull(message = "cafeteriaId is required")
    @field:Positive(message = "cafeteriaId must be positive")
    val cafeteriaId: Int?
)