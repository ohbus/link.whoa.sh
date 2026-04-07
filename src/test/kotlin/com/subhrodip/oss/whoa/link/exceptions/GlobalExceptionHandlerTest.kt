package com.subhrodip.oss.whoa.link.exceptions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

@ExtendWith(MockitoExtension::class)
class GlobalExceptionHandlerTest {
    @InjectMocks
    private lateinit var handler: GlobalExceptionHandler

    @Test
    fun `test handleWhoaException client error`() {
        val ex = UrlNotFoundException("Not found")
        val response = handler.handleWhoaException(ex)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("WHOA-2001", response.body?.errorCode)
        assertEquals("Not found", response.body?.message)
    }

    @Test
    fun `test handleWhoaException server error`() {
        val ex = InternalServerErrorException("Secret internal detail")
        val response = handler.handleWhoaException(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("WHOA-9001", response.body?.errorCode)
        assertEquals("Internal System Error", response.body?.message) // Sanitized
    }

    @Test
    fun `test handleValidationException`() {
        val bindingResult = org.mockito.Mockito.mock(BindingResult::class.java)
        val fieldError = FieldError("object", "url", "must be valid")
        org.mockito.Mockito
            .`when`(bindingResult.fieldErrors)
            .thenReturn(listOf(fieldError))

        val ex =
            MethodArgumentNotValidException(org.mockito.Mockito.mock(org.springframework.core.MethodParameter::class.java), bindingResult)

        val response = handler.handleValidationException(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("WHOA-1001", response.body?.errorCode)
        assertEquals("must be valid", response.body?.errors?.get("url"))
    }

    @Test
    fun `test handleDataIntegrityViolationException`() {
        val ex = DataIntegrityViolationException("Conflict")
        val response = handler.handleDataIntegrityViolationException(ex)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("WHOA-4001", response.body?.errorCode)
    }

    @Test
    fun `test handleWhoaException with non-WhoaException`() {
        val ex = RuntimeException("Standard runtime error")
        val response = handler.handleWhoaException(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("WHOA-9999", response.body?.errorCode)
    }

    @Test
    fun `test handleGenericException`() {
        val ex = Exception("Unknown")
        val response = handler.handleGenericException(ex)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("WHOA-9999", response.body?.errorCode)
        assertEquals("An unexpected error occurred", response.body?.message)
    }
}
