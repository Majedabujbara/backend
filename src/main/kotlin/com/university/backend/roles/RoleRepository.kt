package com.university.backend.roles


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface RoleRepository : JpaRepository<Role, Int> {
    fun findByRoleName(roleName: String): Role?
}