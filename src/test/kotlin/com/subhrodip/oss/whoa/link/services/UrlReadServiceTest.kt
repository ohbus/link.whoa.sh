package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class UrlReadServiceTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var analyticsService: AnalyticsService

    @InjectMocks
    private lateinit var urlReadService: UrlReadService

    @Test
    fun `test getOriginalUrl`() {
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(urlEntity)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals(originalUrl, result)
        verify(analyticsService).trackAnalytics(urlEntity, "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl with no protocol`() {
        val shortCode = "abcdef"
        val originalUrl = "example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(urlEntity)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals("https://example.com", result)
        verify(analyticsService).trackAnalytics(urlEntity, "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl case sensitivity`() {
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(urlEntity)
        `when`(urlRepository.findByShortCode("Abcdef")).thenReturn(null)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals(originalUrl, result)
        assertThrows<UrlNotFoundException> {
            urlReadService.getOriginalUrl("Abcdef", "user-agent", "127.0.0.1")
        }
    }

    @Test
    fun `test getOriginalUrl not found`() {
        val shortCode = "abcdef"
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(null)

        assertThrows<UrlNotFoundException> {
            urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")
        }
    }
}
