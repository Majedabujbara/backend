package com.university.backend.logout.filters

import com.university.backend.token.BlacklistedTokenRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class BlacklistedTokenFilter(
    private val blacklistedTokenRepository: BlacklistedTokenRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7).trim()

            if (token.isNotEmpty() && blacklistedTokenRepository.existsByToken(token)) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json"
                response.writer.write("""{"error":"Token has been revoked"}""")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}

@Configuration
class BlacklistedTokenFilterConfig {

    @Bean
    fun blacklistedTokenFilterRegistration(
        filter: BlacklistedTokenFilter
    ): FilterRegistrationBean<BlacklistedTokenFilter> {
        return FilterRegistrationBean(filter).apply {
            order = Ordered.HIGHEST_PRECEDENCE
        }
    }
}