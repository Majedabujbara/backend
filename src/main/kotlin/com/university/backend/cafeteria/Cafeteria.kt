package com.university.backend.cafeteria

import jakarta.persistence.*

@Entity
@Table(name = "cafeteria")
class Cafeteria(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafeteria_id")
    var cafeteriaId: Int? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "phone", nullable = false)
    var phone: String = ""
)