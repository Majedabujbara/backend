package com.university.backend.roles

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class RoleDataInitializer(
    private val roleRepository: RoleRepository
) : CommandLineRunner {

    override fun run(args: Array<String>) {

        val roles = listOf("CUSTOMER", "EMPLOYEE", "ADMIN")

        roles.forEach { roleName ->

            if (roleRepository.findByRoleName(roleName) == null) {
                roleRepository.save(
                    Role(roleName = roleName)
                )
            }
        }
    }
}
