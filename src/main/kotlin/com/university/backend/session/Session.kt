package com.university.backend.session

import jakarta.persistence.*
import com.university.backend.person.Person
import java.time.Instant

@Entity
@Table(name = "sessions")
class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    var sessionId: Long? = null

    @Column(name = "token", nullable = false, unique = true, length = 512)
    var token: String = ""

    @Column(name = "revoked", nullable = false)
    var revoked: Boolean = false

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant = Instant.now()

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "person_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_sessions_person")
    )
    lateinit var person: Person
}
