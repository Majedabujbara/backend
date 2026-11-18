package com.university.backend.person

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface repository : JpaRepository<Person, UUID> {

    fun findByEmail(email: String): Person?

    fun existsByEmail(email: String): Boolean
}