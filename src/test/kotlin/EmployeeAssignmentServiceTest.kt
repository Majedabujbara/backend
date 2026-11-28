package com.university.backend.employees

import com.university.backend.cafeteria.Cafeteria
import com.university.backend.cafeteria.CafeteriaRepository
import com.university.backend.employees.dtos.AssignCafeteriaRequest
import com.university.backend.exceptions.CafeteriaNotFoundException
import com.university.backend.exceptions.InvalidEmployeeRoleException
import com.university.backend.exceptions.UserNotFoundException
import com.university.backend.person.Person
import com.university.backend.person.PersonRepository
import com.university.backend.roles.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Optional

class EmployeeAssignmentServiceTest {

    private lateinit var personRepository: PersonRepository
    private lateinit var cafeteriaRepository: CafeteriaRepository
    private lateinit var service: EmployeeAssignmentService

    @BeforeEach
    fun setUp() {
        personRepository = mock(PersonRepository::class.java)
        cafeteriaRepository = mock(CafeteriaRepository::class.java)
        service = EmployeeAssignmentService(personRepository, cafeteriaRepository)
    }

    private fun createRole(name: String): Role {
        return Role().apply {
            roleName = name
        }
    }

    private fun createPerson(
        id: Long = 1L,
        fullName: String = "Omar Ahmad",
        email: String = "omar@university.edu",
        phone: String = "0790000000",
        role: Role? = createRole("EMPLOYEE"),
        cafeteria: Cafeteria? = null
    ): Person {
        return Person().apply {
            this.id = id
            this.fullName = fullName
            this.email = email
            this.phone = phone
            this.role = role
            this.cafeteria = cafeteria
        }
    }

    private fun createCafeteria(
        id: Int = 10,
        name: String = "Main Cafeteria",
        phone: String = "065000000"
    ): Cafeteria {
        return Cafeteria(
            cafeteriaId = id,
            name = name,
            phone = phone
        )
    }

    @Test
    fun `getAllEmployees returns mapped employee responses`() {
        val cafe = createCafeteria(id = 2, name = "Engineering Cafe")
        val emp = createPerson(
            id = 5L,
            fullName = "Sara Ali",
            email = "sara@university.edu",
            cafeteria = cafe
        )
        `when`(personRepository.findAllByRoleName("EMPLOYEE")).thenReturn(listOf(emp))

        val result = service.getAllEmployees()

        assertEquals(1, result.size)
        assertEquals(5L, result[0].id)
        assertEquals("Sara Ali", result[0].fullName)
        assertEquals(2, result[0].cafeteriaId)
        assertEquals("Engineering Cafe", result[0].cafeteriaName)
        verify(personRepository).findAllByRoleName("EMPLOYEE")
    }

    @Test
    fun `assignCafeteria assigns cafeteria when employee and cafeteria exist`() {
        val employee = createPerson(id = 1L, role = createRole("EMPLOYEE"))
        val cafeteria = createCafeteria(id = 3, name = "Library Cafe")

        `when`(personRepository.findById(1L)).thenReturn(Optional.of(employee))
        `when`(cafeteriaRepository.findById(3)).thenReturn(Optional.of(cafeteria))
        `when`(personRepository.save(employee)).thenReturn(employee)

        val response = service.assignCafeteria(1L, AssignCafeteriaRequest(cafeteriaId = 3))

        assertEquals(1L, response.id)
        assertEquals(3, response.cafeteriaId)
        assertEquals("Library Cafe", response.cafeteriaName)
        assertEquals(cafeteria, employee.cafeteria)
        verify(personRepository).save(employee)
    }

    @Test
    fun `assignCafeteria throws UserNotFoundException when employee does not exist`() {
        `when`(personRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(UserNotFoundException::class.java) {
            service.assignCafeteria(99L, AssignCafeteriaRequest(cafeteriaId = 1))
        }

        verify(cafeteriaRepository, never()).findById(1)
    }

    @Test
    fun `assignCafeteria throws InvalidEmployeeRoleException when role is not EMPLOYEE`() {
        val customer = createPerson(id = 2L, role = createRole("CUSTOMER"))
        `when`(personRepository.findById(2L)).thenReturn(Optional.of(customer))

        assertThrows(InvalidEmployeeRoleException::class.java) {
            service.assignCafeteria(2L, AssignCafeteriaRequest(cafeteriaId = 1))
        }

        verify(cafeteriaRepository, never()).findById(1)
        verify(personRepository, never()).save(customer)
    }

    @Test
    fun `assignCafeteria throws CafeteriaNotFoundException when cafeteria does not exist`() {
        val employee = createPerson(id = 1L, role = createRole("EMPLOYEE"))
        `when`(personRepository.findById(1L)).thenReturn(Optional.of(employee))
        `when`(cafeteriaRepository.findById(404)).thenReturn(Optional.empty())

        assertThrows(CafeteriaNotFoundException::class.java) {
            service.assignCafeteria(1L, AssignCafeteriaRequest(cafeteriaId = 404))
        }

        verify(personRepository, never()).save(employee)
    }
}