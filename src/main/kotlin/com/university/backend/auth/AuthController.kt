
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
import com.university.backend.security.CurrentUserProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.CookieValue
import com.university.backend.refresh.services.RefreshService
import com.university.backend.config.JwtProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
open class AuthController(
    private val registrationService: RegistrationService,
    private val loginService: LoginService,
    private val refreshService: RefreshService,
    private val jwtProperties: JwtProperties,
    private val currentUserProvider: CurrentUserProvider
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
        val result = loginService.login(request)

        val refreshCookie = ResponseCookie.from(
            "refreshToken",
            result.refreshToken
        )
            .httpOnly(true)
            .secure(false)
            .path("/api/auth")
            .sameSite("Lax")
            .maxAge(Duration.ofMillis(jwtProperties.refreshTokenExpiration))
            .build()

        val response = LoginResponse(
            accessToken = result.accessToken,
            personId = result.personId,
            fullName = result.fullName,
            email = result.email,
            roleName = result.roleName
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response)
    }

    @GetMapping("/me")
    open fun getCurrentUser(): ResponseEntity<Map<String, Any?>> {
        val user = currentUserProvider.currentUser()

        return ResponseEntity.ok(
            mapOf(
                "userId" to user.userId,
                "email" to user.email,
                "role" to user.role,
                "cafeteriaId" to user.cafeteriaId
            )
        )
    }

    @PostMapping("/refresh")
    open fun refresh(
        @CookieValue("refreshToken") refreshToken: String
    ): ResponseEntity<Map<String, String>> {

        val result = refreshService.refresh(refreshToken)

        val refreshCookie = ResponseCookie.from(
            "refreshToken",
            result.refreshToken
        )
            .httpOnly(true)
            .secure(false)
            .path("/api/auth")
            .sameSite("Lax")
            .maxAge(Duration.ofMillis(jwtProperties.refreshTokenExpiration))
            .build()

        val response = mapOf(
            "accessToken" to result.accessToken,
            "tokenType" to "Bearer"
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response)
    }
}