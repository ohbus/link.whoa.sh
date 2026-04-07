package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.services.UrlWriteService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class UrlWriteControllerTest {
    @Mock
    private lateinit var urlWriteService: UrlWriteService

    @InjectMocks
    private lateinit var controller: UrlWriteController

    @Test
    fun `test createShortUrl`() {
        val request = CreateShortUrlRequest("https://google.com")
        val response = CreateShortUrlResponse("https://google.com", "http://localhost/abc")

        `when`(urlWriteService.createShortUrl(request)).thenReturn(response)

        val result = controller.createShortUrl(request)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(response, result.body)
    }
}
