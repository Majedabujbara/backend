package com.university.backend.person

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    @EntityGraph(attributePaths = ["role"])
    fun findByEmail(email: String): Optional<Person>

    @Query(
        """
        select distinct p from Person p
        join fetch p.role r
        left join fetch p.cafeteria c
        where upper(r.roleName) = upper(:roleName)
        """
    )
    fun findAllByRoleName(@Param("roleName") roleName: String): List<Person>
}