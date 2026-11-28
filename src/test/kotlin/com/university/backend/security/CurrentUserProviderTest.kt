package com.university.backend.security

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class CurrentUserProviderTest {

    private val provider = SecurityContextCurrentUserProvider()

    private val employee = JwtAuthenticatedUser(
        userId = 15, email = "emp1.eng@emp.com", role = "EMPLOYEE", cafeteriaId = 2
    )
    private val customer = JwtAuthenticatedUser(
        userId = 20, email = "customer@example.com", role = "CUSTOMER", cafeteriaId = null
    )

    private fun signIn(principal: Any) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(principal, null, emptyList())
    }

    @AfterEach
    fun clearContext() = SecurityContextHolder.clearContext()

    // ---------- provider ----------

    @Test
    fun `returns user id, role and cafeteria id of the signed-in user`() {
        signIn(employee)

        assertThat(provider.currentUser()).isEqualTo(employee)
        assertThat(provider.userId).isEqualTo(15L)
        assertThat(provider.role).isEqualTo("EMPLOYEE")
        assertThat(provider.cafeteriaId).isEqualTo(2)
        assertThat(provider.requireCafeteriaId()).isEqualTo(2)
    }

    @Test
    fun `cafeteria id is null for a user without a cafeteria`() {
        signIn(customer)

        assertThat(provider.cafeteriaId).isNull()
        assertThatThrownBy { provider.requireCafeteriaId() }
            .isInstanceOf(AccessDeniedException::class.java)
    }

    @Test
    fun `no authentication - currentUserOrNull is null and currentUser throws`() {
        assertThat(provider.currentUserOrNull()).isNull()
        assertThatThrownBy { provider.currentUser() }
            .isInstanceOf(AuthenticationCredentialsNotFoundException::class.java)
        assertThatThrownBy { provider.userId }
            .isInstanceOf(AuthenticationCredentialsNotFoundException::class.java)
    }

    @Test
    fun `principal of another type (old style email string) is not treated as a user`() {
        signIn("someone@example.com")

        assertThat(provider.currentUserOrNull()).isNull()
    }

    // ---------- JwtAuthenticatedUser ----------

    @Test
    fun `authentication name is still the email`() {
        signIn(employee)

        assertThat(employee.name).isEqualTo("emp1.eng@emp.com")
        assertThat(SecurityContextHolder.getContext().authentication!!.name)
            .isEqualTo("emp1.eng@emp.com")
    }

    @Test
    fun `hasRole ignores case`() {
        assertThat(employee.hasRole("EMPLOYEE")).isTrue()
        assertThat(employee.hasRole("employee")).isTrue()
        assertThat(employee.hasRole("ADMIN")).isFalse()
    }

    @Test
    fun `rejects invalid values`() {
        assertThatThrownBy { JwtAuthenticatedUser(0, "a@b.com", "ADMIN", null) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("userId")
        assertThatThrownBy { JwtAuthenticatedUser(1, " ", "ADMIN", null) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("email")
        assertThatThrownBy { JwtAuthenticatedUser(1, "a@b.com", "", null) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("role")
        assertThatThrownBy { JwtAuthenticatedUser(1, "a@b.com", "EMPLOYEE", 0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("cafeteriaId")
    }
}
