
package com.university.backend.person

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    @EntityGraph(attributePaths = ["role"])
    fun findByEmail(email: String): Optional<Person>


}

