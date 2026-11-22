package com.university.backend.person

import com.university.backend.person.dtos.UserResponse
import com.university.backend.exceptions.UserNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val personRepository: PersonRepository
) {
    fun getUserById(id: Long): UserResponse {
        val person = personRepository.findById(id).orElseThrow {
            UserNotFoundException("User with ID $id not found")
        }

        return UserResponse(
            personId = requireNotNull(person.id),
            fullName = person.fullName,
            email = person.email,
            roleName = person.role?.roleName ?: "UNKNOWN"
        )
    }
}