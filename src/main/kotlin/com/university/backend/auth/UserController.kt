
package com.university.backend.auth

import com.university.backend.person.UserService
import com.university.backend.person.dtos.EditUserRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.university.backend.person.dtos.ChangePasswordRequest
import org.springframework.security.core.Authentication


@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PutMapping("/{id}")
    fun edit(
        @PathVariable id: Long,
        @Valid @RequestBody request: EditUserRequest
    ): ResponseEntity<Any> {

        val response = userService.edit(
            personId = id,
            fullName = request.fullName,
            phone = request.phone,
            email = request.email
        )

        return ResponseEntity.ok(
            mapOf("message" to "User updated successfully")
        )
    }


    @PutMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {

        userService.changePassword(
            email = authentication.name,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword,
            confirmPassword = request.confirmPassword
        )

        return ResponseEntity.ok(
            mapOf("message" to "Password changed successfully")
        )
    }

}
