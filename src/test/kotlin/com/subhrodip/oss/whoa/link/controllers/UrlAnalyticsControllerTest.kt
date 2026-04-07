package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsRequest
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.PagedUrlsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.services.AnalyticsService
import com.subhrodip.oss.whoa.link.services.UrlReadService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class UrlAnalyticsControllerTest {

    @Mock
    private lateinit var analyticsService: AnalyticsService

    @Mock
    private lateinit var urlReadService: UrlReadService

    @InjectMocks
    private lateinit var controller: UrlAnalyticsController

    @Test
    fun `test getUrlAnalytics`() {
        val response = UrlAnalyticsResponse("orig", "short", 5L, OffsetDateTime.now())
        `when`(analyticsService.getUrlAnalytics("abc")).thenReturn(response)
        
        val result = controller.getUrlAnalytics("abc")
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response, result.body)
    }

    @Test
    fun `test getBulkAnalytics`() {
        val request = BulkAnalyticsRequest(mapOf("a" to 1L), 123L)
        val response = BulkAnalyticsResponse(mapOf("a" to 2L), 456L)
        `when`(analyticsService.getBulkAnalytics(request.currentCounts, request.lastSyncedAt)).thenReturn(response)
        
        val result = controller.getBulkAnalytics(request)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response, result.body)
    }

    @Test
    fun `test getPagedUrls`() {
        val response = PagedUrlsResponse(emptyList(), null, false)
        `when`(urlReadService.getPagedUrls(null, 10)).thenReturn(response)
        
        val result = controller.getPagedUrls(null, 10)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response, result.body)
    }
}
