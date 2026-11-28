package com.university.backend.security

import com.university.backend.cafeteria.Cafeteria
import com.university.backend.config.JwtProperties
import com.university.backend.person.Person
import com.university.backend.person.PersonRepository
import com.university.backend.roles.Role
import com.university.backend.session.Session
import com.university.backend.session.SessionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant
import java.util.Base64
import java.util.Optional

/** Real JwtService (real signed tokens), database repositories mocked. */
class JwtAuthenticationFilterTest {

    private val jwtService = JwtService(
        JwtProperties(secret = Base64.getEncoder().encodeToString(ByteArray(32) { 7 }))
    )
    private val personRepository = mock(PersonRepository::class.java)
    private val sessionRepository = mock(SessionRepository::class.java)
    private val filter = JwtAuthenticationFilter(jwtService, personRepository, sessionRepository)

    @AfterEach
    fun clearContext() = SecurityContextHolder.clearContext()

    private fun person(id: Long, email: String, roleName: String?, cafeteriaId: Int?) =
        Person().apply {
            this.id = id
            this.email = email
            this.role = roleName?.let { Role(roleName = it) }
            this.cafeteria = cafeteriaId?.let { Cafeteria(cafeteriaId = it, name = "Cafe $it") }
        }

    private fun session(id: Long, owner: Person, revoked: Boolean = false) = Session().apply {
        sessionId = id
        person = owner
        this.revoked = revoked
        expiresAt = Instant.now().plusSeconds(3600)
    }

    /** Stores [p] and an active session for it; returns a real access token. */
    private fun signedInAs(p: Person, sessionId: Long = 100, revoked: Boolean = false): String {
        `when`(personRepository.findByEmail(p.email)).thenReturn(Optional.of(p))
        `when`(sessionRepository.findById(sessionId))
            .thenReturn(Optional.of(session(sessionId, p, revoked)))
        return jwtService.generateAccessToken(p.email, sessionId)
    }

    /** Runs the filter and returns the Authentication seen by the next filter in the chain. */
    private fun runFilter(authorization: String?): Authentication? {
        val request = MockHttpServletRequest("GET", "/api/auth/me")
        authorization?.let { request.addHeader("Authorization", it) }
        val chain = MockFilterChain()

        filter.doFilter(request, MockHttpServletResponse(), chain)

        assertThat(chain.request).describedAs("request must always continue down the chain").isNotNull()
        return SecurityContextHolder.getContext().authentication
    }

    @Test
    fun `employee token - principal has user id, role and cafeteria id`() {
        val token = signedInAs(person(15, "emp1.eng@emp.com", "EMPLOYEE", 2))

        val auth = runFilter("Bearer $token")!!

        assertThat(auth.principal).isEqualTo(
            JwtAuthenticatedUser(userId = 15, email = "emp1.eng@emp.com", role = "EMPLOYEE", cafeteriaId = 2)
        )
        assertThat(auth.name).isEqualTo("emp1.eng@emp.com")
        assertThat(auth.authorities.map { it.authority }).containsExactly("ROLE_EMPLOYEE")
    }

    @Test
    fun `admin token - cafeteria id is null`() {
        val token = signedInAs(person(12, "admin.seed@admin.com", "ADMIN", null))

        val user = runFilter("Bearer $token")!!.principal as AuthenticatedUser

        assertThat(user.userId).isEqualTo(12L)
        assertThat(user.role).isEqualTo("ADMIN")
        assertThat(user.cafeteriaId).isNull()
    }

    @Test
    fun `no Authorization header - not authenticated`() {
        assertThat(runFilter(null)).isNull()
    }

    @Test
    fun `garbage token - not authenticated`() {
        assertThat(runFilter("Bearer not-a-jwt")).isNull()
    }

    @Test
    fun `refresh token used as access token - not authenticated`() {
        val p = person(15, "emp1.eng@emp.com", "EMPLOYEE", 2)
        signedInAs(p)

        assertThat(runFilter("Bearer ${jwtService.generateRefreshToken(p.email)}")).isNull()
    }

    @Test
    fun `revoked session - not authenticated`() {
        val token = signedInAs(person(15, "emp1.eng@emp.com", "EMPLOYEE", 2), revoked = true)

        assertThat(runFilter("Bearer $token")).isNull()
    }

    @Test
    fun `session belonging to another user - not authenticated`() {
        val owner = person(15, "emp1.eng@emp.com", "EMPLOYEE", 2)
        val other = person(99, "other@example.com", "CUSTOMER", null)
        `when`(personRepository.findByEmail(other.email)).thenReturn(Optional.of(other))
        `when`(sessionRepository.findById(100L)).thenReturn(Optional.of(session(100, owner)))

        assertThat(runFilter("Bearer ${jwtService.generateAccessToken(other.email, 100)}")).isNull()
    }

    @Test
    fun `user without a role fails validation - not authenticated`() {
        val token = signedInAs(person(15, "norole@example.com", null, null))

        assertThat(runFilter("Bearer $token")).isNull()
    }
}
