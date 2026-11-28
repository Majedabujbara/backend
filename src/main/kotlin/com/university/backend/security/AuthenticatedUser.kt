package com.university.backend.security

import java.security.Principal

/**
 * Shared contract for "who is calling this request".
 * Services and controllers depend on this instead of on JWT or the Person entity.
 */
interface AuthenticatedUser {
    val userId: Long
    val email: String
    val role: String          // "ADMIN", "EMPLOYEE" or "CUSTOMER"
    val cafeteriaId: Int?     // set for employees, null for admins and customers

    fun hasRole(roleName: String): Boolean = role.equals(roleName, ignoreCase = true)
}

/**
 * Implementation built by JwtAuthenticationFilter from the token and the database,
 * and stored as the principal in Spring Security's SecurityContext.
 *
 * Implements Principal so `authentication.name` still returns the email.
 */
data class JwtAuthenticatedUser(
    override val userId: Long,
    override val email: String,
    override val role: String,
    override val cafeteriaId: Int?
) : AuthenticatedUser, Principal {

    // Never put a half-filled user in the SecurityContext. If this throws inside
    // JwtAuthenticationFilter, the request simply stays unauthenticated (-> 401).
    init {
        require(userId > 0) { "userId must be positive" }
        require(email.isNotBlank()) { "email must not be blank" }
        require(role.isNotBlank()) { "role must not be blank" }
        require(cafeteriaId == null || cafeteriaId > 0) { "cafeteriaId must be positive" }
    }

    override fun getName(): String = email
}
