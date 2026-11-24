package com.university.backend.person

import com.university.backend.exceptions.UserNotFoundException
import com.university.backend.person.dtos.UserResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val personRepository: PersonRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun edit(
        personId: Long,
        fullName: String,
        phone: String,
        email: String
    ): Person {

        val person = personRepository.findById(personId)
            .orElseThrow {
                NoSuchElementException("User not found")
            }

        val normalizedEmail = email.trim().lowercase()

        val existingPerson = personRepository.findByEmail(normalizedEmail)

        if (existingPerson.isPresent && existingPerson.get().id != personId) {
            throw IllegalArgumentException("Email already exists")
        }

        person.fullName = fullName.trim()
        person.phone = phone.trim()
        person.email = normalizedEmail

        return personRepository.save(person)
    }

    @Transactional
    fun changePassword(
        email: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        val person = personRepository.findByEmail(email.trim().lowercase())
            .orElseThrow {
                NoSuchElementException("User not found")
            }

        if (!passwordEncoder.matches(currentPassword, person.hashPassword)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        if (newPassword != confirmPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }

        if (currentPassword == newPassword) {
            throw IllegalArgumentException("New password must be different")
        }

        person.hashPassword = passwordEncoder.encode(newPassword)
            ?: throw IllegalStateException("Password encoding failed")

        personRepository.save(person)
    }

    @Transactional(readOnly = true)
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