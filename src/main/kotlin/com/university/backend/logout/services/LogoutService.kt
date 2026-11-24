package com.university.backend.logout.services

import com.university.backend.token.BlacklistedToken
import com.university.backend.token.BlacklistedTokenRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class LogoutService(
    private val blacklistedTokenRepository: BlacklistedTokenRepository
) {
    fun logout(authHeader: String?): String {
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            throw IllegalArgumentException("Missing or invalid Authorization header")
        }

        val token = authHeader.substring(7).trim()

        if (!blacklistedTokenRepository.existsByToken(token)) {
            val blacklistedToken = BlacklistedToken(
                token = token,
                expiresAt = Instant.now().plus(24, ChronoUnit.HOURS)
            )
            blacklistedTokenRepository.save(blacklistedToken)
        }

        return "Logout successful"
    }
}