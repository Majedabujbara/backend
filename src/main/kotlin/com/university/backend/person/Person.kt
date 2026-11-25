package com.university.backend.person


import com.university.backend.cafeteria.Cafeteria
import com.university.backend.roles.Role
import java.time.LocalDateTime
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

    @Column(name = "full_name", nullable = false, length = 75)
    var fullName: String = ""


    @Column(
        name = "phone",
        nullable = false, length = 20
    )
    var phone: String = ""

    @Column(
        name = "email",
        nullable = false,
        unique = true, length = 150
    )
    var email: String = ""

    @Column(
        name = "hash_password",
        nullable = false
    )
    var hashPassword: String = ""

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "role_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_people_role")
    )
    var role: Role? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "cafeteria_id",
        foreignKey = ForeignKey(name = "fk_people_cafeteria")
    )
    var cafeteria: Cafeteria? = null


}