package com.university.backend.login.services

import com.university.backend.exceptions.InvalidCredentialsException
import com.university.backend.login.dtos.LoginRequest
import com.university.backend.person.PersonRepository
import com.university.backend.security.JwtService
import com.university.backend.session.Session
import com.university.backend.session.SessionRepository
import com.university.backend.config.JwtProperties
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val personId: Int,
    val fullName: String,
    val email: String,
    val roleName: String
)

@Service
class LoginService(
    private val personRepository: PersonRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val sessionRepository: SessionRepository,
    private val jwtProperties: JwtProperties
) {

    @Transactional
    fun login(request: LoginRequest): LoginResult {

        val normalizedEmail = request.email.trim().lowercase()

        val person = personRepository.findByEmail(normalizedEmail)
            .orElseThrow {
                InvalidCredentialsException("Invalid email or password")
            }

        println("Email found: ${person.email}")
        println("Password hash exists: ${person.hashPassword.isNotBlank()}")
        println("Password matches: ${passwordEncoder.matches(request.password, person.hashPassword)}")

        if (!passwordEncoder.matches(request.password, person.hashPassword)) {
            throw InvalidCredentialsException("Invalid email or password")
        }

        val roleName = person.role?.roleName ?: ""

        val personId = person.id
            ?: throw IllegalStateException("Person ID is missing")

        // 1. Revoke the previous active session
        sessionRepository.findByPerson_IdAndRevokedFalse(personId)
            .ifPresent { activeSession ->
                activeSession.revoked = true
                sessionRepository.save(activeSession)
            }

        // 2. Generate a new Refresh Token
        val refreshToken = jwtService.generateRefreshToken(normalizedEmail)

        // 3. Create a new Session and store the hashed Refresh Token
        val session = Session().apply {
            token = jwtService.hashToken(refreshToken)
            revoked = false
            createdAt = Instant.now()
            expiresAt = Instant.now().plusMillis(
                jwtProperties.refreshTokenExpiration
            )
            this.person = person
        }

        val savedSession = sessionRepository.save(session)
        
        val accessToken = jwtService.generateAccessToken(
            normalizedEmail,
            savedSession.sessionId!!
        )

        return LoginResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            personId = personId.toInt(),
            fullName = person.fullName,
            email = person.email,
            roleName = roleName
        )
    }
}
