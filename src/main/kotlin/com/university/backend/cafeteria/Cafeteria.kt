package com.university.backend.cafeteria

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cafeterias")
class Cafeteria(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafeteria_id")
    var cafeteriaId: Int? = null,

    @Column(name = "name", nullable = false, length = 100)
    var name: String = "",

    @Column(name = "phone", nullable = false, length = 20)
    var phone: String = "",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)