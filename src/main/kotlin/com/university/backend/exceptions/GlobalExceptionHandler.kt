package com.university.backend.exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiErrorResponse(
    val message: String,
    val errors: Map<String, String>? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {

    // Handle duplicate email
    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleDuplicateEmail(
        exception: EmailAlreadyExistsException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = exception.message ?: "Email already exists"
        )
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response)
    }

    // Handle invalid request fields (@Valid on @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ApiErrorResponse> {
        val errors = exception.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        val response = ApiErrorResponse(
            message = "Validation failed",
            errors = errors
        )
        return ResponseEntity
            .badRequest()
            .body(response)
    }

    // Handle malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(
        exception: HttpMessageNotReadableException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = "Invalid or malformed request body"
        )
        return ResponseEntity
            .badRequest()
            .body(response)
    }

    // Handle database constraint violations
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDatabaseConstraintViolation(
        exception: DataIntegrityViolationException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = "The request conflicts with existing database data"
        )
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response)
    }

    // Handle invalid login credentials
    @ExceptionHandler(value = [InvalidCredentialsException::class])
    open fun handleInvalidCredentials(
        exception: InvalidCredentialsException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = exception.message ?: "Invalid email or password"
        )
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response)
    }

    // Handle resources that do not exist (404)
    @ExceptionHandler(value = [EmployeeNotFoundException::class, CafeteriaNotFoundException::class])
    fun handleNotFound(
        exception: RuntimeException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = exception.message ?: "Resource not found"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }

    // Handle assigning a non-employee user to a cafeteria (400)
    @ExceptionHandler(InvalidEmployeeRoleException::class)
    fun handleInvalidEmployeeRole(
        exception: InvalidEmployeeRoleException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = exception.message ?: "Invalid employee role"
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }

    // Handle @Positive / @Min on path variables and request params (400)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        exception: ConstraintViolationException
    ): ResponseEntity<ApiErrorResponse> {
        val errors = exception.constraintViolations.associate {
            it.propertyPath.toString() to it.message
        }
        val response = ApiErrorResponse(
            message = "Validation failed",
            errors = errors
        )
        return ResponseEntity
            .badRequest()
            .body(response)
    }

    // Handle user not found (404)
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(
        exception: UserNotFoundException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = exception.message ?: "User not found"
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }

    // Handle forbidden access (403)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        exception: AccessDeniedException
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            message = exception.message ?: "Access denied"
        )
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response)
    }
}