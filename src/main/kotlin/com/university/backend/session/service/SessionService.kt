package com.university.backend.session.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.university.backend.session.SessionRepository

@Service
class SessionService(
    private val sessionRepository: SessionRepository
)  {
    @Transactional(readOnly = true)
    fun getOwnerFullNameByToken(token: String): String {
        val session = sessionRepository.findByToken(token)
            .orElseThrow { IllegalArgumentException("Session not found") }

        return session.person.fullName
    }
}