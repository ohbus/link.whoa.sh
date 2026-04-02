package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class UrlCacheIntegrationTest {
    @Autowired
    private lateinit var urlReadService: UrlReadService

    @Autowired
    private lateinit var urlWriteService: UrlWriteService

    @Autowired
    private lateinit var urlCacheService: UrlCacheService

    @MockitoBean
    private lateinit var urlRepository: UrlRepository

    @MockitoBean
    private lateinit var analyticsService: AnalyticsService

    @Autowired
    private lateinit var cacheManager: CacheManager

    @BeforeEach
    fun setup() {
        // Clear cache before each test
        cacheManager.getCache("urls")?.clear()
    }

    @Test
    fun `test findUrlByShortCode is cached`() {
        val shortCode = "testCache"
        val urlEntity = UrlEntity(originalUrl = "https://example.com", shortCode = shortCode)

        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(urlEntity)

        // First call - should hit repository
        val firstResult = urlCacheService.getCachedUrl(shortCode)
        assertNotNull(firstResult)
        assertEquals("https://example.com", firstResult.originalUrl)

        // Second call - should hit cache
        val secondResult = urlCacheService.getCachedUrl(shortCode)
        assertEquals(firstResult, secondResult)

        // Verify repository was only called once for getCachedUrl
        verify(urlRepository, times(1)).findByShortCode(shortCode)

        // Verify cache contains the entry
        val cache = cacheManager.getCache("urls")
        assertNotNull(cache)
        val cachedValue = cache?.get("url:$shortCode")
        assertNotNull(cachedValue)
    }

    @Test
    fun `test createShortUrl populates cache`() {
        val shortCode = "newCode"
        val request = CreateShortUrlRequest(url = "https://new.com", shortCode = shortCode)

        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(null)
        `when`(urlRepository.save(any<UrlEntity>())).thenAnswer { it.arguments[0] }

        // Create the short URL
        urlWriteService.createShortUrl(request)

        // Verify cache is populated
        val cache = cacheManager.getCache("urls")
        val cachedValue = cache?.get("url:$shortCode")
        assertNotNull(cachedValue, "Cache should be populated after creation")

        // Call read service - should NOT hit repository because it's in cache
        val result = urlCacheService.getCachedUrl(shortCode)
        assertEquals("https://new.com", result.originalUrl)

        // Verify repository findByShortCode was NOT called by getCachedUrl
        // (It was called once by createShortUrl for checking existence)
        verify(urlRepository, times(1)).findByShortCode(shortCode)
    }

    @Test
    fun `test analytics tracked on every hit and getCachedUrl uses cache`() {
        val shortCode = "analyticTest"
        val urlEntity = UrlEntity(originalUrl = "https://example.com", shortCode = shortCode)

        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(urlEntity)
        `when`(urlRepository.getReferenceById(any())).thenReturn(urlEntity)

        // First hit
        urlReadService.getOriginalUrl(shortCode, "agent1", "1.1.1.1")
        // Second hit
        urlReadService.getOriginalUrl(shortCode, "agent2", "2.2.2.2")

        // Verify analytics tracked twice
        verify(analyticsService, times(2)).trackAnalytics(any(), any(), any())

        // Verify repository findByShortCode called:
        // 1 time by getCachedUrl (first hit only, cached thereafter)
        verify(urlRepository, times(1)).findByShortCode(shortCode)

        // Verify getReferenceById called:
        // 2 times by getOriginalUrl directly (once per hit)
        verify(urlRepository, times(2)).getReferenceById(any())
    }
}
