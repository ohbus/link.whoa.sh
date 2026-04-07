package com.subhrodip.oss.whoa.link.exceptions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ExceptionCoverageTest {

    @Test
    fun `test ValidationException`() {
        val message = "Validation error"
        val ex = ValidationException(message)
        assertEquals(message, ex.message)
        assertEquals("WHOA-1001", ex.errorCode)
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
        
        val customEx = ValidationException(message, "CUSTOM-001")
        assertEquals("CUSTOM-001", customEx.errorCode)
    }

    @Test
    fun `test MethodNotSupportedException`() {
        val message = "Method not supported"
        val ex = MethodNotSupportedException(message)
        assertEquals(message, ex.message)
        assertEquals("WHOA-1002", ex.errorCode)
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, ex.statusCode)
        
        val customEx = MethodNotSupportedException(message, "CUSTOM-002")
        assertEquals("CUSTOM-002", customEx.errorCode)
    }
}
