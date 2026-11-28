package com.university.backend.employees.dtos

import jakarta.validation.constraints.NotNull

data class AssignCafeteriaRequest(
    @field:NotNull(message = "cafeteriaId is required")
    val cafeteriaId: Int?
)