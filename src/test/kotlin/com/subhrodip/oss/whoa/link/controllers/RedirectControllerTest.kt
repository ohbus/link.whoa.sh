package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.services.UrlReadService
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class RedirectControllerTest {

    @Mock
    private lateinit var urlReadService: UrlReadService

    @Mock
    private lateinit var request: HttpServletRequest

    @InjectMocks
    private lateinit var redirectController: RedirectController

    @Test
    fun `test redirectToOriginalUrl`() {
        val shortCode = "abc"
        val originalUrl = "https://google.com"
        
        `when`(request.getHeader("User-Agent")).thenReturn("test-agent")
        `when`(request.remoteAddr).thenReturn("127.0.0.1")
        `when`(urlReadService.getOriginalUrl(shortCode, "test-agent", "127.0.0.1")).thenReturn(originalUrl)

        val response = redirectController.redirectToOriginalUrl(shortCode, request)

        assertEquals(HttpStatus.FOUND, response.statusCode)
        assertEquals(originalUrl, response.headers.location?.toString())
    }

    @Test
    fun `test redirectToOriginalUrl with missing user agent`() {
        val shortCode = "abc"
        val originalUrl = "https://google.com"
        
        `when`(request.getHeader("User-Agent")).thenReturn(null)
        `when`(request.remoteAddr).thenReturn("127.0.0.1")
        `when`(urlReadService.getOriginalUrl(shortCode, "", "127.0.0.1")).thenReturn(originalUrl)

        val response = redirectController.redirectToOriginalUrl(shortCode, request)

        assertEquals(HttpStatus.FOUND, response.statusCode)
        assertEquals(originalUrl, response.headers.location?.toString())
    }
}
