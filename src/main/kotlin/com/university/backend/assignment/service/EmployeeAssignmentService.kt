package com.university.backend.assignment.service

import com.university.backend.assignment.dtos.CafeteriaSummaryResponse
import com.university.backend.assignment.dtos.EmployeeAssignmentResponse
import com.university.backend.assignment.port.EmployeeAssignmentStore
import com.university.backend.exceptions.InvalidEmployeeAssignmentException
import com.university.backend.exceptions.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeAssignmentService(
    private val store: EmployeeAssignmentStore
) {
    @Transactional(readOnly = true)
    fun getAllEmployees(): List<EmployeeAssignmentResponse> {
        return store.findAllUsers()
            .filter { it.role.equals("EMPLOYEE", ignoreCase = true) }
            .map { emp ->
                val assignedCafeteriaId = emp.cafeteriaId
                    ?: throw InvalidEmployeeAssignmentException(
                        "Employee ${emp.id} must be assigned to a cafeteria"
                    )
                val cafeteria = store.findCafeteriaById(assignedCafeteriaId)
                    ?: throw UserNotFoundException(
                        "Assigned cafeteria $assignedCafeteriaId not found for employee ${emp.id}"
                    )

                EmployeeAssignmentResponse(
                    id = emp.id,
                    name = emp.name,
                    email = emp.email,
                    role = emp.role.uppercase(),
                    cafeteriaId = cafeteria.cafeteriaId,
                    cafeteria = CafeteriaSummaryResponse(
                        cafeteriaId = cafeteria.cafeteriaId,
                        name = cafeteria.name,
                        phone = cafeteria.phone
                    )
                )
            }
    }

    @Transactional
    fun reassignEmployeeCafeteria(employeeId: Long, cafeteriaId: Int): EmployeeAssignmentResponse {
        val user = store.findUserById(employeeId)
            ?: throw UserNotFoundException("Employee with id $employeeId not found")

        if (!user.role.equals("EMPLOYEE", ignoreCase = true)) {
            throw InvalidEmployeeAssignmentException(
                "Only EMPLOYEE accounts can be assigned to a cafeteria (role: ${user.role})"
            )
        }

        val targetCafeteria = store.findCafeteriaById(cafeteriaId)
            ?: throw UserNotFoundException("Cafeteria with id $cafeteriaId not found")

        val updated = store.updateEmployeeCafeteria(employeeId, targetCafeteria.cafeteriaId)
        val finalCafeteriaId = updated.cafeteriaId ?: targetCafeteria.cafeteriaId

        return EmployeeAssignmentResponse(
            id = updated.id,
            name = updated.name,
            email = updated.email,
            role = updated.role.uppercase(),
            cafeteriaId = finalCafeteriaId,
            cafeteria = CafeteriaSummaryResponse(
                cafeteriaId = targetCafeteria.cafeteriaId,
                name = targetCafeteria.name,
                phone = targetCafeteria.phone
            )
        )
    }

}