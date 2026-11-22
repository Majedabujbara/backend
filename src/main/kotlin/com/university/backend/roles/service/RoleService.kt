package com.university.backend.roles.services

import com.university.backend.roles.Role
import com.university.backend.roles.RoleRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
open class RoleService(
    private val roleRepository: RoleRepository
) {

    open fun getAllRoles(): List<Role> {
        return roleRepository.findAll()
    }

    open fun getRoleByName(roleName: String): Role {
        return roleRepository.findByRoleName(roleName)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: $roleName")
    }

    open fun getRoleById(id: Int): Role {
        return roleRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: $id") }
    }
}