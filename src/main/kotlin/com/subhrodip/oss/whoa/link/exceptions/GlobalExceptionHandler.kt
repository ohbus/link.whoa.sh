package com.subhrodip.oss.whoa.link.exceptions

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    /**
     * Handles all custom exceptions that implement [WhoaException].
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleWhoaException(ex: Exception): ResponseEntity<ErrorResponse> {
        val whoaEx = ex as? WhoaException
        if (whoaEx != null) {
            val status = whoaEx.statusCode
            if (status.is5xxServerError) {
                log.error(ex) { "Server Error [${whoaEx.errorCode}]: ${ex.message}" }
            } else {
                log.warn { "Client Error [${whoaEx.errorCode}]: ${ex.message}" }
            }

            val response =
                ErrorResponse(
                    statusCode = status.value(),
                    errorCode = whoaEx.errorCode,
                    message = if (status.is5xxServerError) "Internal System Error" else ex.message,
                )
            return ResponseEntity(response, status)
        }
        return handleGenericException(ex)
    }

    /**
     * Specifically handles Bean Validation errors with field-level details.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors =
            ex.bindingResult.fieldErrors.associate {
                it.field to (it.defaultMessage ?: "Invalid value")
            }

        log.warn { "Validation failed: $fieldErrors" }

        val response =
            ErrorResponse(
                statusCode = HttpStatus.BAD_REQUEST.value(),
                errorCode = "WHOA-1001",
                message = "Validation failed for one or more fields",
                errors = fieldErrors,
            )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles database constraint violations.
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Database integrity violation" }
        val response =
            ErrorResponse(
                statusCode = HttpStatus.CONFLICT.value(),
                errorCode = "WHOA-4001",
                message = "A resource conflict occurred (possible duplicate)",
            )
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    /**
     * Final catch-all for unhandled exceptions.
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unhandled exception occurred: ${ex.message}" }
        val response =
            ErrorResponse(
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorCode = "WHOA-9999",
                message = "An unexpected error occurred",
            )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
