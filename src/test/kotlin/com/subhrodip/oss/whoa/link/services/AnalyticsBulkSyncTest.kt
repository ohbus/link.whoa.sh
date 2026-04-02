package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.dto.ClickCountByIdProjection
import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class AnalyticsBulkSyncTest {

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

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(analyticsService, "baseUrl", "http://localhost:8844")
    }

    private fun mockProjection(id: Long, clicks: Long): ClickCountByIdProjection {
        val p = mock(ClickCountByIdProjection::class.java)
        whenever(p.urlId).thenReturn(id)
        whenever(p.totalClicks).thenReturn(clicks)
        return p
    }

    private fun mockUrlDto(id: Long, code: String): UrlDto {
        return UrlDto(id, "https://example.com", code)
    }

    @Test
    fun `getBulkAnalytics returns all requested counts when lastSyncedAt is null`() {
        val currentCounts = mapOf("code1" to 10L, "code2" to 20L)
        
        whenever(urlCacheService.getCachedUrl("code1")).thenReturn(mockUrlDto(1L, "code1"))
        whenever(urlCacheService.getCachedUrl("code2")).thenReturn(mockUrlDto(2L, "code2"))
        
        val p1 = mockProjection(1L, 15L)
        val p2 = mockProjection(2L, 25L)
        
        whenever(urlAnalyticsRepository.countByUrlIds(listOf(1L, 2L))).thenReturn(listOf(p1, p2))

        val response = analyticsService.getBulkAnalytics(currentCounts, null)

        assertEquals(2, response.clicks.size)
        assertEquals(15L, response.clicks["code1"])
        assertEquals(25L, response.clicks["code2"])
    }

    @Test
    fun `getBulkAnalytics returns only changed counts when lastSyncedAt is provided`() {
        val lastSyncedAt = System.currentTimeMillis() - 10000
        val currentCounts = mapOf("code1" to 10L, "code2" to 20L)
        
        whenever(urlCacheService.getCachedUrl("code1")).thenReturn(mockUrlDto(1L, "code1"))
        whenever(urlCacheService.getCachedUrl("code2")).thenReturn(mockUrlDto(2L, "code2"))
        
        whenever(urlAnalyticsRepository.findIdsWithActivitySince(eq(listOf(1L, 2L)), any())).thenReturn(listOf(1L))
        
        val p1 = mockProjection(1L, 15L)
        whenever(urlAnalyticsRepository.countByUrlIds(listOf(1L))).thenReturn(listOf(p1))

        val response = analyticsService.getBulkAnalytics(currentCounts, lastSyncedAt)

        assertEquals(1, response.clicks.size)
        assertEquals(15L, response.clicks["code1"])
        assertTrue(!response.clicks.containsKey("code2"))
    }

    @Test
    fun `getBulkAnalytics returns empty map when no activity since lastSyncedAt`() {
        val lastSyncedAt = System.currentTimeMillis()
        val currentCounts = mapOf("code1" to 10L)
        
        whenever(urlCacheService.getCachedUrl("code1")).thenReturn(mockUrlDto(1L, "code1"))
        whenever(urlAnalyticsRepository.findIdsWithActivitySince(eq(listOf(1L)), any())).thenReturn(emptyList())

        val response = analyticsService.getBulkAnalytics(currentCounts, lastSyncedAt)

        assertTrue(response.clicks.isEmpty())
    }
}
