package com.university.backend.refresh.services

import com.university.backend.config.JwtProperties
import com.university.backend.exceptions.InvalidCredentialsException
import com.university.backend.login.services.TokenResult
import com.university.backend.security.JwtService
import com.university.backend.session.SessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class RefreshService(
    private val jwtService: JwtService,
    private val sessionRepository: SessionRepository,
    private val jwtProperties: JwtProperties
) {

    @Transactional
    fun refresh(refreshToken: String): TokenResult {

        val claims = jwtService.parseToken(refreshToken)

        if (claims.get("type", String::class.java) != "refresh") {
            throw InvalidCredentialsException("Invalid or expired refresh token")
        }

        val email = claims.subject

        val session = sessionRepository.findByToken(jwtService.hashToken(refreshToken))
            .orElseThrow {
                InvalidCredentialsException("Invalid or expired refresh token")
            }
        sessionRepository.save(session)

        if (
            session.revoked ||
            !session.expiresAt.isAfter(Instant.now()) ||
            session.person.email != email
        ) {
            throw InvalidCredentialsException("Invalid or expired refresh token")
        }

        val roleName = session.person.role?.roleName
            ?: throw InvalidCredentialsException("Invalid or expired refresh token")

        val newRefreshToken = jwtService.generateRefreshToken(email)

        session.token = jwtService.hashToken(newRefreshToken)
        session.expiresAt = Instant.now()
            .plusMillis(jwtProperties.refreshTokenExpiration)


        println("Updated session ID: ${session.sessionId}")
        println("New stored hash: ${session.token.take(12)}")
        sessionRepository.save(session)

        val newAccessToken = jwtService.generateAccessToken(
            email,
            session.sessionId!!,
            roleName
        )

        return TokenResult(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}
