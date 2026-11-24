package com.university.backend.token

import org.springframework.data.jpa.repository.JpaRepository

interface BlacklistedTokenRepository : JpaRepository<BlacklistedToken, Long> {
    fun existsByToken(token: String): Boolean
}