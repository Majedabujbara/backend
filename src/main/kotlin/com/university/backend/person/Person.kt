package com.university.backend.person

import com.university.backend.cafeteria.Cafeteria
import com.university.backend.roles.Role
import com.university.backend.roles.RoleEnum
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
open class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    open var id: Long? = null

    @Column(name = "full_name", nullable = false, length = 75)
    open var fullName: String = ""

    @Column(
        name = "phone",
        nullable = false,
        length = 20
    )
    open var phone: String = ""

    @Column(
        name = "email",
        nullable = false,
        unique = true,
        length = 150
    )
    open var email: String = ""

    @Column(
        name = "hash_password",
        nullable = false
    )
    open var hashPassword: String = ""

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "role_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_people_role")
    )
    open var role: Role? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "cafeteria_id",
        nullable = true,
        foreignKey = ForeignKey(name = "fk_people_cafeteria")
    )
    open var cafeteria: Cafeteria? = null

    @PrePersist
    @PreUpdate
    fun validateEmployeeCafeteria() {
        val currentRoleName = role?.roleName?.trim()?.uppercase()
        if (currentRoleName == RoleEnum.EMPLOYEE.name && cafeteria == null) {
            throw IllegalStateException("Employee must be assigned to a cafeteria.")
        }
    }
}