package com.university.backend.exceptions

class InvalidEmployeeRoleException(message: String) : RuntimeException(message) {
    constructor(id: Long, role: String) : this(
        "User $id has role $role and cannot be assigned to a cafeteria"
    )

    constructor(id: String, role: String) : this(
        "User $id has role $role and cannot be assigned to a cafeteria"
    )
}