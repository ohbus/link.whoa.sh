package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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

    @Test
    fun `getBulkAnalytics returns all requested counts when lastSyncedAt is null`() {
        val currentCounts = mapOf("code1" to 10L, "code2" to 20L)
        val repoCounts = listOf(
            arrayOf<Any>("code1", 15L),
            arrayOf<Any>("code2", 25L)
        )
        
        `when`(urlAnalyticsRepository.countByShortCodes(listOf("code1", "code2"))).thenReturn(repoCounts)

        val response = analyticsService.getBulkAnalytics(currentCounts, null)

        assertEquals(2, response.clicks.size)
        assertEquals(15L, response.clicks["code1"])
        assertEquals(25L, response.clicks["code2"])
    }

    @Test
    fun `getBulkAnalytics returns only changed counts when lastSyncedAt is provided`() {
        val lastSyncedAt = System.currentTimeMillis() - 10000
        val currentCounts = mapOf("code1" to 10L, "code2" to 20L)
        
        // code1 changed, code2 didn't
        `when`(urlAnalyticsRepository.findShortCodesWithActivitySince(eq(listOf("code1", "code2")), any()))
            .thenReturn(listOf("code1"))
        
        `when`(urlAnalyticsRepository.countByShortCodes(listOf("code1")))
            .thenReturn(listOf(arrayOf<Any>("code1", 15L)))

        val response = analyticsService.getBulkAnalytics(currentCounts, lastSyncedAt)

        assertEquals(1, response.clicks.size)
        assertEquals(15L, response.clicks["code1"])
        assertTrue(!response.clicks.containsKey("code2"))
    }

    @Test
    fun `getBulkAnalytics returns empty map when no activity since lastSyncedAt`() {
        val lastSyncedAt = System.currentTimeMillis()
        val currentCounts = mapOf("code1" to 10L)
        
        `when`(urlAnalyticsRepository.findShortCodesWithActivitySince(eq(listOf("code1")), any()))
            .thenReturn(emptyList())

        val response = analyticsService.getBulkAnalytics(currentCounts, lastSyncedAt)

        assertTrue(response.clicks.isEmpty())
    }
}
