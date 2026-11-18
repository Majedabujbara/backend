package com.university.backend.registration.services

import com.university.backend.registration.dtos.RegisterRequest
import com.university.backend.registration.dtos.RegisterResponse

import com.university.backend.person.PersonRepository
import com.university.backend.person.Person

import com.university.backend.roles.RoleRepository

import com.university.backend.exceptions.EmailAlreadyExistsException

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegistrationService(

    private val personRepository: PersonRepository,

    private val roleRepository: RoleRepository,

    private val passwordEncoder: PasswordEncoder

) {

    @Transactional
    fun register(request: RegisterRequest): RegisterResponse {

        val normalizedEmail = request.email.lowercase()

        if (personRepository.findByEmail(normalizedEmail).isPresent) {
            throw EmailAlreadyExistsException(
                "Email already registered"
            )
        }

        val customerRole = roleRepository.findByRoleName("CUSTOMER")
            ?: throw IllegalStateException(
                "CUSTOMER role is not configured in the database"
            )

        val hashedPassword = passwordEncoder.encode(request.password)!!

        val person = Person().apply {
            fullName = request.fullName.trim()
            phone = request.phone.trim()
            email = normalizedEmail
            passwordHash = hashedPassword
            role = customerRole
            cafeteria = null
        }

        val savedPerson = personRepository.save(person)

        return RegisterResponse(
            personId = requireNotNull(savedPerson.id).toInt(), // تم تعديلها لتتوافق مع معرف الـ Person لديك
            fullName = savedPerson.fullName,
            email = savedPerson.email,
            roleName = savedPerson.role?.roleName ?: "CUSTOMER"
        )
    }
}