package com.university.backend.assignment.port

import com.university.backend.cafeteria.CafeteriaRepository
import com.university.backend.exceptions.UserNotFoundException
import com.university.backend.person.Person
import com.university.backend.person.PersonRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class JpaEmployeeAssignmentStore(
    private val personRepository: PersonRepository,
    private val cafeteriaRepository: CafeteriaRepository
) : EmployeeAssignmentStore {

    @Transactional(readOnly = true)
    override fun findAllUsers(): List<EmployeeRecord> =
        personRepository.findAll().map { it.toRecord() }

    @Transactional(readOnly = true)
    override fun findUserById(userId: Long): EmployeeRecord? =
        personRepository.findById(userId).map { it.toRecord() }.orElse(null)

    @Transactional(readOnly = true)
    override fun findCafeteriaById(cafeteriaId: Int): CafeteriaRecord? =
        cafeteriaRepository.findById(cafeteriaId)
            .map { CafeteriaRecord(requireNotNull(it.cafeteriaId), it.name, it.phone) }
            .orElse(null)

    @Transactional
    override fun updateEmployeeCafeteria(employeeId: Long, newCafeteriaId: Int): EmployeeRecord {
        val person = personRepository.findById(employeeId).orElseThrow {
            UserNotFoundException("Employee with id $employeeId not found")
        }
        val cafeteria = cafeteriaRepository.findById(newCafeteriaId).orElseThrow {
            UserNotFoundException("Cafeteria with id $newCafeteriaId not found")
        }
        person.cafeteria = cafeteria
        return personRepository.save(person).toRecord()
    }

    private fun Person.toRecord() = EmployeeRecord(
        id = requireNotNull(id),
        name = fullName,
        email = email,
        role = role?.roleName ?: "UNKNOWN",
        cafeteriaId = cafeteria?.cafeteriaId
    )
}