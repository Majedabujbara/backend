package com.university.backend.assignment.controller

import com.university.backend.assignment.dtos.EmployeeAssignmentResponse
import com.university.backend.assignment.dtos.ReassignEmployeeRequest
import com.university.backend.assignment.service.EmployeeAssignmentService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('ADMIN')")
@Validated
class EmployeeAssignmentController(
    private val employeeAssignmentService: EmployeeAssignmentService
) {

    @GetMapping
    fun getEmployees(): ResponseEntity<List<EmployeeAssignmentResponse>> {
        return ResponseEntity.ok(employeeAssignmentService.getAllEmployees())
    }

    @PutMapping("/{employeeId}/cafeteria")
    fun reassignCafeteria(
        @PathVariable
        @Positive(message = "employeeId must be a positive number")
        employeeId: Long,
        @Valid
        @RequestBody
        request: ReassignEmployeeRequest
    ): ResponseEntity<EmployeeAssignmentResponse> {
        val updated = employeeAssignmentService.reassignEmployeeCafeteria(
            employeeId = employeeId,
            cafeteriaId = request.cafeteriaId!!
        )
        return ResponseEntity.ok(updated)
    }
}