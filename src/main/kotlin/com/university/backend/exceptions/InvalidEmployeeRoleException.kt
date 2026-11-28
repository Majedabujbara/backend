package com.university.backend.exceptions

class InvalidEmployeeRoleException(id: Long, role: String) : RuntimeException("User $id has role $role and cannot be assigned to a cafeteria")