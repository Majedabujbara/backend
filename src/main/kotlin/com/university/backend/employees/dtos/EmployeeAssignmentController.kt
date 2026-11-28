package com.university.backend.employees

import com.university.backend.employees.dtos.AssignCafeteriaRequest
import com.university.backend.employees.dtos.EmployeeResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//@RestController
@RequestMapping("/api/employees")
class EmployeeAssignmentController(
    private val employeeAssignmentService: EmployeeAssignmentService
) {

    @GetMapping
    fun getAllEmployees(): ResponseEntity<List<EmployeeResponse>> {
        return ResponseEntity.ok(employeeAssignmentService.getAllEmployees())
    }

    @PutMapping("/{employeeId}/cafeteria")
    fun assignCafeteria(
        @PathVariable employeeId: Long,
        @Valid @RequestBody request: AssignCafeteriaRequest
    ): ResponseEntity<EmployeeResponse> {
        return ResponseEntity.ok(employeeAssignmentService.assignCafeteria(employeeId, request))
    }
}