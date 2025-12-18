package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFoundException(ex: UrlNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                UrlNotFoundException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ErrorResponse> {
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
        val errorResponse =
            ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                MethodNotSupportedException.ERROR_CODE.toString(),
                ex.message,
            )
        return ResponseEntity(errorResponse, HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(InternalServerErrorException::class)
    fun handleInternalServerErrorException(ex: InternalServerErrorException): ResponseEntity<ErrorResponse> {
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
        val errorResponse =
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "696969",
                "An unexpected error occurred",
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
