
package com.university.backend.security

import com.university.backend.person.PersonRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val personRepository: PersonRepository
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
            if (jwtService.isTokenValid(token)) {

                val email = jwtService.extractUsername(token)

                if (SecurityContextHolder.getContext().authentication == null) {

                    val person = personRepository.findByEmail(email)
                        .orElse(null)

                    if (person != null) {

                        val roleName = person.role?.roleName

                        val authorities = if (roleName != null) {
                            listOf(
                                SimpleGrantedAuthority("ROLE_$roleName")
                            )
                        } else {
                            emptyList()
                        }

                        val authentication =
                            UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                            )

                        SecurityContextHolder.getContext()
                            .authentication = authentication
                    }
                }
            }
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}
