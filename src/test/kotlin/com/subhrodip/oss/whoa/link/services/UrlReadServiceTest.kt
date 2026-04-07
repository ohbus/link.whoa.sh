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
import org.mockito.kotlin.any
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

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals(originalUrl, result)
        verify(analyticsService).trackAnalytics(1L, "abcdef", "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl with no protocol`() {
        val shortCode = "abcdef"
        val originalUrl = "example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals("https://example.com", result)
        verify(analyticsService).trackAnalytics(1L, "abcdef", "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl with http protocol`() {
        val shortCode = "abcdef"
        val originalUrl = "http://example.com"
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals("http://example.com", result)
        verify(analyticsService).trackAnalytics(1L, "abcdef", "user-agent", "127.0.0.1")
    }

    @Test
    fun `test getOriginalUrl case sensitivity`() {
        val shortCode = "abcdef"
        val originalUrl = "https://example.com"
        val urlEntity = UrlEntity(originalUrl = originalUrl, shortCode = shortCode)
        val urlDto = UrlDto(id = 1L, originalUrl = originalUrl, shortCode = shortCode)

        `when`(urlCacheService.getCachedUrl(shortCode)).thenReturn(urlDto)
        `when`(urlCacheService.getCachedUrl("Abcdef")).thenThrow(UrlNotFoundException("Not found"))

        val result = urlReadService.getOriginalUrl(shortCode, "user-agent", "127.0.0.1")

        assertEquals(originalUrl, result)
        verify(analyticsService).trackAnalytics(1L, "abcdef", "user-agent", "127.0.0.1")
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

    @Test
    fun `test getPagedUrls initial load`() {
        val urlEntity = UrlEntity(originalUrl = "https://example.com", shortCode = "abc")
        urlEntity.id = 1L
        val entities = listOf(urlEntity)
        `when`(urlRepository.findLatest(any())).thenReturn(entities)

        val result = urlReadService.getPagedUrls(null, 10)

        assertEquals(1, result.links.size)
        assertEquals("https://example.com", result.links[0].originalUrl)
        assertEquals(false, result.hasMore)
    }

    @Test
    fun `test getPagedUrls empty`() {
        `when`(urlRepository.findLatest(any())).thenReturn(emptyList())

        val result = urlReadService.getPagedUrls(null, 10)

        assertEquals(0, result.links.size)
        assertEquals(false, result.hasMore)
        assertEquals(null, result.nextCursor)
    }

    @Test
    fun `test getPagedUrls with cursor and hasMore`() {
        val url1 = UrlEntity(originalUrl = "https://url1.com", shortCode = "abc")
        url1.id = 1L
        val url2 = UrlEntity(originalUrl = "https://url2.com", shortCode = "def")
        url2.id = 2L

        // Return 2 entities when limit is 1 to trigger hasMore
        `when`(urlRepository.findByCreatedAtBefore(any(), any())).thenReturn(listOf(url1, url2))

        val result = urlReadService.getPagedUrls(123456789L, 1)

        assertEquals(1, result.links.size)
        assertEquals(true, result.hasMore)
        assertEquals(true, result.nextCursor != null)
    }
}
