package com.university.backend.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SessionRepository : JpaRepository<Session, Long> {

    fun findByToken(token: String): Optional<Session>

    fun findAllByPerson_Id(personId: Long): List<Session>

    fun findByPerson_IdAndRevokedFalse(personId: Long): Optional<Session>
}