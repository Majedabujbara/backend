package com.university.backend.roles

import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    var id: Long? = null,
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    var roleName: String = ""
)