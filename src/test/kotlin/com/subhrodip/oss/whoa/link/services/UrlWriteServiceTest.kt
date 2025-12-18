package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.exceptions.ShortCodeAlreadyExistsException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class UrlWriteServiceTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @InjectMocks
    private lateinit var urlWriteService: UrlWriteService

    @Test
    fun `test createShortUrl with random short code`() {
        ReflectionTestUtils.setField(urlWriteService, "baseUrl", "http://localhost:8844")
        val request = CreateShortUrlRequest(url = "https://example.com")
        `when`(urlRepository.findByShortCode(any())).thenReturn(null)
        `when`(urlRepository.save(any<UrlEntity>())).thenAnswer { it.arguments[0] }

        val response = urlWriteService.createShortUrl(request)

        assertEquals("https://example.com", response.originalUrl)
        assertEquals(28, response.shortUrl.length) // http://localhost:8844/ + 6 chars
    }

    @Test
    fun `test createShortUrl with custom short code`() {
        ReflectionTestUtils.setField(urlWriteService, "baseUrl", "http://localhost:8844")
        val request = CreateShortUrlRequest(url = "https://example.com", shortCode = "custom")
        `when`(urlRepository.findByShortCode("custom")).thenReturn(null)
        `when`(urlRepository.save(any<UrlEntity>())).thenAnswer { it.arguments[0] }

        val response = urlWriteService.createShortUrl(request)

        assertEquals("https://example.com", response.originalUrl)
        assertEquals("http://localhost:8844/custom", response.shortUrl)
    }

    @Test
    fun `test createShortUrl with collision`() {
        ReflectionTestUtils.setField(urlWriteService, "baseUrl", "http://localhost:8844")
        val request = CreateShortUrlRequest(url = "https://example.com")
        val existingUrl = UrlEntity(originalUrl = "https://another.com", shortCode = "abcdef")

        // Simulate a collision on the first attempt
        `when`(urlRepository.findByShortCode(any())).thenReturn(existingUrl).thenReturn(null)
        `when`(urlRepository.save(any<UrlEntity>())).thenAnswer { it.arguments[0] }

        val response = urlWriteService.createShortUrl(request)

        assertEquals("https://example.com", response.originalUrl)
        assertEquals(28, response.shortUrl.length) // http://localhost:8844/ + 6 chars
    }

    @Test
    fun `test createShortUrl with existing custom short code`() {
        val request = CreateShortUrlRequest(url = "https://example.com", shortCode = "custom")
        val urlEntity = UrlEntity(originalUrl = "https://another.com", shortCode = "custom")
        `when`(urlRepository.findByShortCode("custom")).thenReturn(urlEntity)

        assertThrows<ShortCodeAlreadyExistsException> {
            urlWriteService.createShortUrl(request)
        }
    }
}
