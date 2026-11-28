
package com.university.backend.auth
import com.university.backend.registration.dtos.RegisterRequest
import com.university.backend.registration.dtos.RegisterResponse
import com.university.backend.registration.services.RegistrationService
import org.springframework.http.HttpStatus
import com.university.backend.login.dtos.LoginRequest
import com.university.backend.login.dtos.LoginResponse
import com.university.backend.login.services.LoginService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import com.university.backend.refresh.dtos.RefreshRequest
import com.university.backend.refresh.dtos.RefreshResponse
import com.university.backend.refresh.services.RefreshService

@RestController
@RequestMapping("/api/auth")
open class AuthController(
    private val registrationService: RegistrationService,
    private val loginService: LoginService,
    private val refreshService: RefreshService
) {
    @PostMapping("/register")
    open fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<RegisterResponse> {
        val response = registrationService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    open fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val response = loginService.login(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    open fun getCurrentUser(
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {

        return ResponseEntity.ok(
            mapOf("email" to authentication.name)
        )
    }

    @PostMapping("/refresh")
    open fun refresh(
        @Valid @RequestBody request: RefreshRequest
    ): ResponseEntity<RefreshResponse> {
        val response = refreshService.refresh(request)
        return ResponseEntity.ok(response)
    }
}