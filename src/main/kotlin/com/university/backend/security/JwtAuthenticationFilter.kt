package com.university.backend.security

import com.university.backend.person.PersonRepository
import com.university.backend.session.SessionRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val personRepository: PersonRepository,
    private val sessionRepository: SessionRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        try {
            // 1. Validate that the token is a valid Access Token
            if (!jwtService.isAccessTokenValid(token)) {
                SecurityContextHolder.clearContext()
                filterChain.doFilter(request, response)
                return
            }

            // 2. Extract email and session ID
            val email = jwtService.extractUsername(token)
            val sessionId = jwtService.extractSessionId(token)

            if (SecurityContextHolder.getContext().authentication == null) {

                // 3. Find the Session in the database
                val session = sessionRepository.findById(sessionId)
                    .orElse(null)

                if (session == null ||
                    session.revoked ||
                    !session.expiresAt.isAfter(Instant.now())
                ) {
                    SecurityContextHolder.clearContext()
                    filterChain.doFilter(request, response)
                    return
                }

                // 4. Verify that the Session belongs to this user
                val person = personRepository.findByEmail(email)
                    .orElse(null)

                if (person == null || session.person.id != person.id) {
                    SecurityContextHolder.clearContext()
                    filterChain.doFilter(request, response)
                    return
                }

                // 5. Load the user's role
                val roleName = person.role?.roleName

                val authorities = if (roleName != null) {
                    listOf(
                        SimpleGrantedAuthority("ROLE_$roleName")
                    )
                } else {
                    emptyList()
                }

                // 6. Set authentication with the shared AuthenticatedUser as principal
                val currentUser = JwtAuthenticatedUser(
                    userId = requireNotNull(person.id),
                    email = person.email,
                    role = roleName ?: "",
                    cafeteriaId = person.cafeteria?.cafeteriaId
                )

                val authentication =
                    UsernamePasswordAuthenticationToken(
                        currentUser,
                        null,
                        authorities
                    )

                SecurityContextHolder.getContext().authentication =
                    authentication
            }

        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
            logger.warn("JWT authentication failed", e)
        }

        filterChain.doFilter(request, response)
    }
}
