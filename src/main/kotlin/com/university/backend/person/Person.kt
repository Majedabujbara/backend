package com.university.backend.person

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "people")
class Person(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "person_id")
    var personId: UUID? = null,

    @Column(nullable = false)
    var fullName: String,

    @Column(nullable = true)
    var phone: String? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "role_id",
        nullable = false
    )
    var role: Role? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "cafeteria_id"
    )
      var cafeteria: Cafeteria? = null
)