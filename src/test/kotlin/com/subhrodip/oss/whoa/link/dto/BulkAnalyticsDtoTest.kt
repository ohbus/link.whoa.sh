package com.subhrodip.oss.whoa.link.dto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BulkAnalyticsDtoTest {

    @Test
    fun `BulkAnalyticsRequest should implement equals and hashCode correctly`() {
        val req1 = BulkAnalyticsRequest(mapOf("code1" to 1L), 1000L)
        val req2 = BulkAnalyticsRequest(mapOf("code1" to 1L), 1000L)
        val req3 = BulkAnalyticsRequest(mapOf("code2" to 2L), 2000L)

        assertEquals(req1, req2)
        assertEquals(req1.hashCode(), req2.hashCode())
        assertNotEquals(req1, req3)
        assertNotEquals(req1.hashCode(), req3.hashCode())
        
        val reqStr = req1.toString()
        assert(reqStr.contains("code1"))
        assert(reqStr.contains("1000"))
    }

    @Test
    fun `BulkAnalyticsRequest should support default values`() {
        val req = BulkAnalyticsRequest(mapOf("code1" to 1L))
        assertEquals(null, req.lastSyncedAt)
        assertEquals(mapOf("code1" to 1L), req.currentCounts)
    }

    @Test
    fun `BulkAnalyticsResponse should implement equals and hashCode correctly`() {
        val res1 = BulkAnalyticsResponse(mapOf("code1" to 1L), 1000L)
        val res2 = BulkAnalyticsResponse(mapOf("code1" to 1L), 1000L)
        val res3 = BulkAnalyticsResponse(mapOf("code2" to 2L), 2000L)

        assertEquals(1000L, res1.serverTimestamp)
        assertEquals(res1, res2)
        assertEquals(res1.hashCode(), res2.hashCode())
        assertNotEquals(res1, res3)
        assertNotEquals(res1.hashCode(), res3.hashCode())
        
        val resStr = res1.toString()
        assert(resStr.contains("code1"))
        assert(resStr.contains("1000"))
    }
}
