package com.subhrodip.oss.whoa.link.exceptions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `test handleUrlNotFoundException`() {
        val ex = UrlNotFoundException("Not found")
        val response = handler.handleUrlNotFoundException(ex)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Not found", response.body?.message)
    }

    @Test
    fun `test handleValidationException`() {
        val ex = ValidationException("Invalid")
        val response = handler.handleValidationException(ex)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid", response.body?.message)
    }

    @Test
    fun `test handleMethodNotSupportedException`() {
        val ex = MethodNotSupportedException("Not supported")
        val response = handler.handleMethodNotSupportedException(ex)
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.statusCode)
        assertEquals("Not supported", response.body?.message)
    }

    @Test
    fun `test handleShortCodeAlreadyExistsException`() {
        val ex = ShortCodeAlreadyExistsException("Exists")
        val response = handler.handleShortCodeAlreadyExistsException(ex)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Exists", response.body?.message)
    }

    @Test
    fun `test handleDataIntegrityViolationException`() {
        val ex = DataIntegrityViolationException("Conflict")
        val response = handler.handleDataIntegrityViolationException(ex)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `test handleInternalServerErrorException`() {
        val ex = InternalServerErrorException("Internal error")
        val response = handler.handleInternalServerErrorException(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Internal error", response.body?.message)
    }

    @Test
    fun `test handleGenericException`() {
        val ex = Exception("Unknown")
        val response = handler.handleGenericException(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("An unexpected error occurred", response.body?.message)
    }
}
