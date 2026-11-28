package com.university.backend.roles

import com.university.backend.roles.services.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/roles")
open class RoleController(
    private val roleService: RoleService
) {

    @GetMapping
    open fun getAllRoles(): ResponseEntity<List<Role>> {
        return ResponseEntity.ok(roleService.getAllRoles())
    }

    @GetMapping("/name/{roleName}")
    open fun getRoleByName(@PathVariable roleName: String): ResponseEntity<Role> {
        return ResponseEntity.ok(roleService.getRoleByName(roleName))
    }

    @GetMapping("/{id}")
    open fun getRoleById(@PathVariable id: Int): ResponseEntity<Role> {
        return ResponseEntity.ok(roleService.getRoleById(id))
    }
}