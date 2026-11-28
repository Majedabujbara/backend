package com.university.backend.security

import com.university.backend.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date
import java.util.UUID
import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.SecretKey
import io.jsonwebtoken.Claims

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    private val signingKey: SecretKey by lazy {
        val keyBytes = Base64.getDecoder().decode(jwtProperties.secret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    // 1. Generate Access Token
    fun generateAccessToken(
        email: String,
        sessionId: Long,
        role:String
    ): String {
        val now = Date()
        val expirationDate = Date(
            now.time + jwtProperties.accessTokenExpiration
        )

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(email)
            .claim("type", "access")
            .claim("sessionId", sessionId)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(signingKey)
            .compact()
    }

    // 2. Generate Refresh Token
    fun generateRefreshToken(email: String): String {
        val now = Date()
        val expirationDate = Date(
            now.time + jwtProperties.refreshTokenExpiration
        )

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(email)
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(signingKey)
            .compact()
    }

    // 3. Extract Username (Email)
    fun extractUsername(token: String): String {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    // 4. Extract Session ID
    fun extractSessionId(token: String): Long {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .get("sessionId", Number::class.java)
            .toLong()
    }

    // Extract Role
    fun extractRole(token: String): String {
        return parseToken(token).get("role", String::class.java)
    }

    // 5. Validate Access Token
    fun isAccessTokenValid(token: String): Boolean {

            val claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload

            return claims.get("type", String::class.java) == "access" &&
                    claims.get("sessionId", Number::class.java) != null &&
                    claims.get("role", String::class.java) != null


    }

    // 6. Validate Refresh Token
    fun isRefreshTokenValid(token: String): Boolean {

            val claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload

        return  claims.get("type", String::class.java) == "refresh"

    }

    // 7. Hash Refresh Token
    fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(token.toByteArray(Charsets.UTF_8))

        return HexFormat.of().formatHex(hash)
    }
}
