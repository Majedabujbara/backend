package com.university.backend.person

import com.university.backend.cafeteria.Cafeteria
import com.university.backend.roles.Role

import jakarta.persistence.*

@Entity
@Table(
    name = "people",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_people_email",
            columnNames = ["email"]
        )
    ]
)
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    var id: Long? = null

    @Column(
        name = "full_name",
        nullable = false
    )
    var fullName: String = ""

    @Column(
        name = "phone",
        nullable = false
    )
    var phone: String = ""

    @Column(
        name = "email",
        nullable = false,
        unique = true
    )
    var email: String = ""

    @Column(
        name = "password",
        nullable = false
    )
    var passwordHash: String = ""

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "role_id",
        nullable = false
    )
    var role: Role? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "cafeteria_id"
    )
    var cafeteria: Cafeteria? = null
}