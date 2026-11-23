package com.university.backend.logout.controllers

import com.university.backend.logout.services.LogoutService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LogoutController(
    private val logoutService: LogoutService
) {

    @PostMapping("/logout")
    fun logout(
        @RequestHeader("Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        return try {
            val message = logoutService.logout(authHeader)
            ResponseEntity.ok(mapOf("message" to message))
        } catch (e: IllegalArgumentException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to (e.message ?: "Invalid Authorization header")))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to (e.message ?: "Logout failed")))
        }
    }
}