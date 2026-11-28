package com.university.backend.refresh.services

import com.university.backend.config.JwtProperties
import com.university.backend.exceptions.InvalidCredentialsException
import com.university.backend.refresh.dtos.RefreshRequest
import com.university.backend.refresh.dtos.RefreshResponse
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
    fun refresh(request: RefreshRequest): RefreshResponse {
        val refreshToken = request.refreshToken

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw InvalidCredentialsException("Invalid or expired refresh token")
        }

        val email = jwtService.extractUsername(refreshToken)
        val tokenHash = jwtService.hashToken(refreshToken)

        val session = sessionRepository.findByToken(tokenHash)
            .orElseThrow {
                InvalidCredentialsException("Invalid or expired refresh token")
            }

        if (
            session.revoked ||
            !session.expiresAt.isAfter(Instant.now()) ||
            session.person.email != email
        ) {
            throw InvalidCredentialsException("Invalid or expired refresh token")
        }

        val newRefreshToken = jwtService.generateRefreshToken(email)

        session.token = jwtService.hashToken(newRefreshToken)
        session.expiresAt = Instant.now()
            .plusMillis(jwtProperties.refreshTokenExpiration)

        sessionRepository.save(session)

        val newAccessToken = jwtService.generateAccessToken(
            email,
            session.sessionId!!
        )

        return RefreshResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}
