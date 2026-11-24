package com.university.backend.token

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "blacklisted_tokens")
class BlacklistedToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 512)
    val token: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant
)