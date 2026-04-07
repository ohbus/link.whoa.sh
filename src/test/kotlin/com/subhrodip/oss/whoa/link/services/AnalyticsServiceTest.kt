package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.test.util.ReflectionTestUtils
import java.time.OffsetDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class AnalyticsServiceTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @Mock
    private lateinit var urlCacheService: UrlCacheService

    @Mock
    private lateinit var globalCounterService: GlobalCounterService

    @InjectMocks
    private lateinit var analyticsService: AnalyticsService

    @Test
    fun `test trackAnalytics`() {
        val shortCode = "abcdef"
        val urlId = 1L

        `when`(urlRepository.getReferenceById(urlId)).thenReturn(mock(UrlEntity::class.java))

        analyticsService.trackAnalytics(urlId, shortCode, "user-agent", "127.0.0.1")

        verify(urlAnalyticsRepository).save(any<UrlAnalyticsEntity>())
        verify(urlRepository).incrementClickCount(urlId)
        verify(urlCacheService).evictUrlCache(shortCode)
        verify(globalCounterService).incrementRealTime()
    }

    @Test
    fun `test getUrlAnalytics`() {
        ReflectionTestUtils.setField(analyticsService, "baseUrl", "http://localhost:8844")
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val now = OffsetDateTime.now()
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode, totalClicks = 5L, createdAt = now)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)

        val result = analyticsService.getUrlAnalytics(shortCode)

        assertEquals(originalUrl, result.originalUrl)
        assertEquals("http://localhost:8844/abcdef", result.shortUrl)
        assertEquals(5L, result.clicks)
        assertEquals(now, result.createdAt)
    }

    @Test
    fun `test getUrlAnalytics not found`() {
        val shortCode = "abcdef"
        `when`(urlCacheService.getCachedUrl(shortCode)).thenThrow(UrlNotFoundException("Not found"))

        assertThrows<UrlNotFoundException> {
            analyticsService.getUrlAnalytics(shortCode)
        }
    }
}
