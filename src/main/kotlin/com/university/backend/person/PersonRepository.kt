package com.university.backend.person

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    fun findByEmail(email: String): Optional<Person>
}