package com.university.backend.security

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/** Gives any bean the user ID, role and cafeteria ID of the current request's user. */
interface CurrentUserProvider {

    /** The signed-in user, or null when the request has no valid token. */
    fun currentUserOrNull(): AuthenticatedUser?

    /** The signed-in user; throws (-> 401) when the request has no valid token. */
    fun currentUser(): AuthenticatedUser =
        currentUserOrNull()
            ?: throw AuthenticationCredentialsNotFoundException("No authenticated user")

    val userId: Long get() = currentUser().userId
    val role: String get() = currentUser().role
    val cafeteriaId: Int? get() = currentUser().cafeteriaId

    /** Cafeteria of the current user; throws (-> 403) if they have none (e.g. a customer). */
    fun requireCafeteriaId(): Int =
        cafeteriaId ?: throw AccessDeniedException("User is not assigned to a cafeteria")
}

@Component
class SecurityContextCurrentUserProvider : CurrentUserProvider {
    override fun currentUserOrNull(): AuthenticatedUser? =
        SecurityContextHolder.getContext().authentication?.principal as? AuthenticatedUser
}
