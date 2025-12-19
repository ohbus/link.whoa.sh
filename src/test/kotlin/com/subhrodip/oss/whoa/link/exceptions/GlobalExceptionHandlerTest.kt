package com.subhrodip.oss.whoa.link.exceptions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `should handle UrlNotFoundException`() {
        // Given
        val exception = UrlNotFoundException("URL not found")

        // When
        val response = handler.handleUrlNotFoundException(exception)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(HttpStatus.NOT_FOUND.value(), response.body?.statusCode)
        assertEquals(UrlNotFoundException.ERROR_CODE.toString(), response.body?.errorCode)
        assertEquals("URL not found", response.body?.message)
    }

    @Test
    fun `should handle ValidationException`() {
        // Given
        val exception = ValidationException("Invalid input")

        // When
        val response = handler.handleValidationException(exception)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.body?.statusCode)
        assertEquals(ValidationException.ERROR_CODE.toString(), response.body?.errorCode)
        assertEquals("Invalid input", response.body?.message)
    }

    @Test
    fun `should handle MethodNotSupportedException`() {
        // Given
        val exception = MethodNotSupportedException("Method not supported")

        // When
        val response = handler.handleMethodNotSupportedException(exception)

        // Then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.statusCode)
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), response.body?.statusCode)
        assertEquals(MethodNotSupportedException.ERROR_CODE.toString(), response.body?.errorCode)
        assertEquals("Method not supported", response.body?.message)
    }

    @Test
    fun `should handle ShortCodeAlreadyExistsException`() {
        // Given
        val exception = ShortCodeAlreadyExistsException("Short code exists")

        // When
        val response = handler.handleShortCodeAlreadyExistsException(exception)

        // Then
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(HttpStatus.CONFLICT.value(), response.body?.statusCode)
        assertEquals(ShortCodeAlreadyExistsException.ERROR_CODE.toString(), response.body?.errorCode)
        assertEquals("Short code exists", response.body?.message)
    }

    @Test
    fun `should handle InternalServerErrorException`() {
        // Given
        val exception = InternalServerErrorException("Internal error")

        // When
        val response = handler.handleInternalServerErrorException(exception)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.body?.statusCode)
        assertEquals(InternalServerErrorException.ERROR_CODE.toString(), response.body?.errorCode)
        assertEquals("Internal error", response.body?.message)
    }

    @Test
    fun `should handle generic Exception`() {
        // Given
        val exception = Exception("Some generic error")

        // When
        val response = handler.handleGenericException(exception)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.body?.statusCode)
        assertEquals("696969", response.body?.errorCode)
        assertEquals("An unexpected error occurred", response.body?.message)
    }
}
