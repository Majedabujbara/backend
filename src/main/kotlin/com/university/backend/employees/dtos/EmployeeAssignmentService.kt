package com.university.backend.employees

import com.university.backend.cafeteria.CafeteriaRepository
import com.university.backend.employees.dtos.AssignCafeteriaRequest
import com.university.backend.employees.dtos.EmployeeResponse
import com.university.backend.exceptions.CafeteriaNotFoundException
import com.university.backend.exceptions.InvalidEmployeeRoleException
import com.university.backend.exceptions.UserNotFoundException
import com.university.backend.person.Person
import com.university.backend.person.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

//@Service
@Transactional(readOnly = true)
open class EmployeeAssignmentService(
    private val personRepository: PersonRepository,
    private val cafeteriaRepository: CafeteriaRepository
) {

    open fun getAllEmployees(): List<EmployeeResponse> {
        return personRepository.findAllByRoleName("EMPLOYEE")
            .map { it.toEmployeeResponse() }
    }

    @Transactional
    open fun assignCafeteria(employeeId: Long, request: AssignCafeteriaRequest): EmployeeResponse {
        val person = personRepository.findById(employeeId)
            .orElseThrow { UserNotFoundException("Employee not found with id: $employeeId") }

        val roleName = person.role?.roleName
        if (roleName == null || !roleName.equals("EMPLOYEE", ignoreCase = true)) {
            throw InvalidEmployeeRoleException(employeeId, roleName ?: "NONE")
        }

        val targetCafeteriaId = request.cafeteriaId
            ?: throw IllegalArgumentException("cafeteriaId is required")

        val cafeteria = cafeteriaRepository.findById(targetCafeteriaId)
            .orElseThrow { CafeteriaNotFoundException(targetCafeteriaId.toLong()) }

        person.cafeteria = cafeteria
        val updated = personRepository.save(person)
        return updated.toEmployeeResponse()
    }
        }

    private fun Person.toEmployeeResponse(): EmployeeResponse {
        return EmployeeResponse(
            id = this.id ?: 0L,
            fullName = this.fullName,
            email = this.email,
            phone = this.phone,
            cafeteriaId = this.cafeteria?.cafeteriaId,
            cafeteriaName = this.cafeteria?.name
        )
    }