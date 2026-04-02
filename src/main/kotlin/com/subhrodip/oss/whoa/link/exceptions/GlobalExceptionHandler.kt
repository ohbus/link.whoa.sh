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
    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFoundException(ex: UrlNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn { "URL not found: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                UrlNotFoundException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(
        ValidationException::class,
        MethodArgumentNotValidException::class,
    )
    fun handleValidationException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.warn { "Validation failed: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ValidationException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodNotSupportedException::class)
    fun handleMethodNotSupportedException(ex: MethodNotSupportedException): ResponseEntity<ErrorResponse> {
        log.warn { "Method not supported: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                MethodNotSupportedException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(ShortCodeAlreadyExistsException::class)
    fun handleShortCodeAlreadyExistsException(ex: ShortCodeAlreadyExistsException): ResponseEntity<ErrorResponse> {
        log.warn { "Short code already exists: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ShortCodeAlreadyExistsException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Data integrity violation: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ShortCodeAlreadyExistsException.ERROR_CODE.toString(),
                "Short code already exists or data integrity violation",
            )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(InternalServerErrorException::class)
    fun handleInternalServerErrorException(ex: InternalServerErrorException): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Internal server error: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                InternalServerErrorException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unexpected system error: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "696969",
                "An unexpected error occurred",
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
