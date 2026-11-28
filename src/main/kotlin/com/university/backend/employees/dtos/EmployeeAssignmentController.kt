package com.university.backend.employees

import com.university.backend.employees.dtos.AssignCafeteriaRequest
import com.university.backend.employees.dtos.EmployeeResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/employees")
open class EmployeeAssignmentController(
    private val employeeAssignmentService: EmployeeAssignmentService
) {

    @GetMapping
    open fun getAllEmployees(): ResponseEntity<List<EmployeeResponse>> {
        return ResponseEntity.ok(employeeAssignmentService.getAllEmployees())
    }

    @PutMapping("/{employeeId}/cafeteria")
    open fun assignCafeteria(
        @PathVariable @Positive(message = "employeeId must be positive") employeeId: Long,
        @Valid @RequestBody request: AssignCafeteriaRequest
    ): ResponseEntity<EmployeeResponse> {
        return ResponseEntity.ok(employeeAssignmentService.assignCafeteria(employeeId, request))
    }
}