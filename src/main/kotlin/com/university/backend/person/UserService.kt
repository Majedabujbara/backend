
package com.university.backend.person

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder

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

        if (!passwordEncoder.matches(currentPassword, person.passwordHash)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        if (newPassword != confirmPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }

        if (currentPassword == newPassword) {
            throw IllegalArgumentException("New password must be different")
        }

        person.passwordHash = passwordEncoder.encode(newPassword)
            ?: throw IllegalStateException("Password encoding failed")

        personRepository.save(person)
    }

}
