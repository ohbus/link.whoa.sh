package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class UrlReadServiceTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var analyticsService: AnalyticsService

    @Mock
    private lateinit var urlCacheService: UrlCacheService

    @Mock
    private lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @InjectMocks
    private lateinit var urlReadService: UrlReadService

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(urlReadService, "baseUrl", "http://localhost:8844")
    }

    @Test
    fun `test getOriginalUrl`() {
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)
        `when`(urlRepository.getReferenceById(urlDto.id)).thenReturn(urlEntity)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals(originalUrl, result)
        verify(analyticsService).trackAnalytics(urlEntity, "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl with no protocol`() {
        val shortCode = "abcdef"
        val originalUrl = "example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)
        `when`(urlRepository.getReferenceById(urlDto.id)).thenReturn(urlEntity)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals("https://example.com", result)
        verify(analyticsService).trackAnalytics(urlEntity, "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl case sensitivity`() {
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)
        `when`(urlRepository.getReferenceById(urlDto.id)).thenReturn(urlEntity)
        `when`(urlCacheService.getCachedUrl("Abcdef")).thenThrow(UrlNotFoundException("Not found"))

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals(originalUrl, result)
        assertThrows<UrlNotFoundException> {
            urlReadService.getOriginalUrl("Abcdef", "user-agent", "127.0.0.1")
        }
    }

    @Test
    fun `test getOriginalUrl not found`() {
        val shortCode = "abcdef"
        `when`(urlCacheService.getCachedUrl(shortCode)).thenThrow(UrlNotFoundException("Not found"))

        assertThrows<UrlNotFoundException> {
            urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")
        }
    }
}
