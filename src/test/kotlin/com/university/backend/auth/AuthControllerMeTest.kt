package com.university.backend.auth

import com.university.backend.config.JwtProperties
import com.university.backend.login.services.LoginService
import com.university.backend.refresh.services.RefreshService
import com.university.backend.registration.services.RegistrationService
import com.university.backend.security.AuthenticatedUser
import com.university.backend.security.CurrentUserProvider
import com.university.backend.security.JwtAuthenticatedUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class AuthControllerMeTest {

    private fun controllerFor(user: AuthenticatedUser?) = AuthController(
        registrationService = mock(RegistrationService::class.java),
        loginService = mock(LoginService::class.java),
        refreshService = mock(RefreshService::class.java),
        jwtProperties = JwtProperties(secret = "unused"),
        currentUserProvider = object : CurrentUserProvider {
            override fun currentUserOrNull() = user
        }
    )

    @Test
    fun `me returns user id, email, role and cafeteria id`() {
        val response = controllerFor(
            JwtAuthenticatedUser(userId = 15, email = "emp1.eng@emp.com", role = "EMPLOYEE", cafeteriaId = 2)
        ).getCurrentUser()

        assertThat(response.statusCode.value()).isEqualTo(200)
        assertThat(response.body).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                "userId" to 15L,
                "email" to "emp1.eng@emp.com",
                "role" to "EMPLOYEE",
                "cafeteriaId" to 2
            )
        )
    }

    @Test
    fun `me returns null cafeteria id for an admin`() {
        val body = controllerFor(
            JwtAuthenticatedUser(userId = 12, email = "admin.seed@admin.com", role = "ADMIN", cafeteriaId = null)
        ).getCurrentUser().body!!

        assertThat(body).containsEntry("cafeteriaId", null)
        assertThat(body).containsEntry("role", "ADMIN")
    }
}
