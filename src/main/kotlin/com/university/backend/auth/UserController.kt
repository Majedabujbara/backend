package com.university.backend.auth

import com.university.backend.person.UserService
import com.university.backend.person.dtos.UserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse {
        return userService.getUserById(id)
    }
}