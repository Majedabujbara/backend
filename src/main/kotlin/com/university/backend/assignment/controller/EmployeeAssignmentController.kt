package com.university.backend.assignment.controller

import com.university.backend.assignment.dtos.EmployeeAssignmentResponse
import com.university.backend.assignment.dtos.ReassignEmployeeRequest
import com.university.backend.assignment.service.EmployeeAssignmentService
import com.university.backend.exceptions.InvalidEmployeeAssignmentException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('ADMIN')")
class EmployeeAssignmentController(
    private val employeeAssignmentService: EmployeeAssignmentService
) {

    @GetMapping
    fun getEmployees(): ResponseEntity<List<EmployeeAssignmentResponse>> {
        return ResponseEntity.ok(employeeAssignmentService.getAllEmployees())
    }

    @PutMapping("/{employeeId}/cafeteria")
    fun reassignCafeteria(
        @PathVariable employeeId: Long,
        @RequestBody request: ReassignEmployeeRequest
    ): ResponseEntity<EmployeeAssignmentResponse> {
        val targetCafeteriaId = request.cafeteriaId
            ?: throw InvalidEmployeeAssignmentException("cafeteriaId is required")

        if (targetCafeteriaId <= 0) {
            throw InvalidEmployeeAssignmentException("cafeteriaId must be a positive number")
        }

        val updated = employeeAssignmentService.reassignEmployeeCafeteria(employeeId, targetCafeteriaId)
        return ResponseEntity.ok(updated)
    }
}