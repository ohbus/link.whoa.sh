package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
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
import org.mockito.kotlin.verify
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class AnalyticsServiceTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @InjectMocks
    private lateinit var analyticsService: AnalyticsService

    @Test
    fun `test trackAnalytics`() {
        val urlEntity = UrlEntity(originalUrl = "https://example.com", shortCode = "abcdef")

        analyticsService.trackAnalytics(urlEntity, "user-agent", "127.0.0.1")

        verify(urlAnalyticsRepository).save(any<UrlAnalyticsEntity>())
    }

    @Test
    fun `test getUrlAnalytics`() {
        ReflectionTestUtils.setField(analyticsService, "baseUrl", "http://localhost:8844")
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        ReflectionTestUtils.setField(urlEntity, "id", 1L)
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(urlEntity)
        `when`(urlAnalyticsRepository.countByUrlEntityId(1)).thenReturn(5)

        val result = analyticsService.getUrlAnalytics(shortCode)

        assertEquals(originalUrl, result.originalUrl)
        assertEquals("http://localhost:8844/abcdef", result.shortUrl)
        assertEquals(5, result.clicks)
    }

    @Test
    fun `test getUrlAnalytics not found`() {
        val shortCode = "abcdef"
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(null)

        assertThrows<UrlNotFoundException> {
            analyticsService.getUrlAnalytics(shortCode)
        }
    }
}
