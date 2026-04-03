package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsRequest
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.PagedUrlsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.services.AnalyticsService
import com.subhrodip.oss.whoa.link.services.UrlReadService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class UrlAnalyticsControllerTest {

    @Mock
    lateinit var analyticsService: AnalyticsService

    @Mock
    lateinit var urlReadService: UrlReadService

    @InjectMocks
    lateinit var urlAnalyticsController: UrlAnalyticsController

    @Test
    fun `getUrlAnalytics should return analytics response`() {
        val mockResponse = UrlAnalyticsResponse("original", "shortUrl", 10, OffsetDateTime.now())
        `when`(analyticsService.getUrlAnalytics("code1")).thenReturn(mockResponse)

        val response = urlAnalyticsController.getUrlAnalytics("code1")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockResponse, response.body)
    }

    @Test
    fun `getBulkAnalytics should return bulk response`() {
        val request = BulkAnalyticsRequest(mapOf("code1" to 1L), 1000L)
        val mockResponse = BulkAnalyticsResponse(mapOf("code1" to 2L), 2000L)
        `when`(analyticsService.getBulkAnalytics(request.currentCounts, request.lastSyncedAt)).thenReturn(mockResponse)

        val response = urlAnalyticsController.getBulkAnalytics(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockResponse, response.body)
    }

    @Test
    fun `getPagedUrls should return paged response`() {
        val mockResponse = PagedUrlsResponse(emptyList(), null, false)
        `when`(urlReadService.getPagedUrls(100L, 10)).thenReturn(mockResponse)

        val response = urlAnalyticsController.getPagedUrls(100L, 10)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockResponse, response.body)
    }
}
