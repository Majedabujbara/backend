package com.university.backend.exceptions

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
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

    // Handle invalid request fields
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
        return ResponseEntity.badRequest()
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

    @ExceptionHandler(InvalidEmployeeAssignmentException::class)
    fun handleInvalidEmployeeAssignment(
        exception: InvalidEmployeeAssignmentException
    ): ResponseEntity<ApiErrorResponse> {
        val msg = exception.message ?: "Invalid employee assignment"
        val response = ApiErrorResponse(
            message = msg,
            errors = mapOf("employeeAssignment" to msg)
        )
        return ResponseEntity.badRequest().body(response)
    }
}