package com.subhrodip.oss.whoa.link.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DtoTest {
    @Test
    fun `test UrlAnalyticsResponse default values`() {
        val response = UrlAnalyticsResponse("orig", "short", 10L)
        assertEquals("orig", response.originalUrl)
        assertEquals("short", response.shortUrl)
        assertEquals(10L, response.clicks)
        assertNull(response.createdAt)
    }

    @Test
    fun `test CreateShortUrlRequest default values`() {
        val request = CreateShortUrlRequest("https://google.com")
        assertEquals("https://google.com", request.url)
        assertNull(request.shortCode)
    }

    @Test
    fun `test CreateShortUrlResponse`() {
        val response = CreateShortUrlResponse("orig", "short")
        assertEquals("orig", response.originalUrl)
        assertEquals("short", response.shortUrl)
    }

    @Test
    fun `test GlobalClicksResponse`() {
        val response = GlobalClicksResponse(100L, 12345L)
        assertEquals(100L, response.totalClicks)
        assertEquals(12345L, response.serverTimestamp)
    }

    @Test
    fun `test PagedUrlsResponse`() {
        val links = listOf(UrlAnalyticsResponse("a", "b", 1L))
        val response = PagedUrlsResponse(links, 123L, true)
        assertEquals(links, response.links)
        assertEquals(123L, response.nextCursor)
        assertEquals(true, response.hasMore)
    }

    @Test
    fun `test BulkAnalyticsDto`() {
        val counts = mapOf("a" to 1L)
        val request = BulkAnalyticsRequest(counts, 123L)
        assertEquals(counts, request.currentCounts)
        assertEquals(123L, request.lastSyncedAt)

        val clicks = mapOf("a" to 2L)
        val response = BulkAnalyticsResponse(clicks, 456L)
        assertEquals(clicks, response.clicks)
        assertEquals(456L, response.serverTimestamp)
    }
}
