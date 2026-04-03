package com.subhrodip.oss.whoa.link.dto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GlobalClicksResponseTest {

    @Test
    fun `GlobalClicksResponse should implement equals and hashCode correctly`() {
        val res1 = GlobalClicksResponse(100L, 1000L)
        val res2 = GlobalClicksResponse(100L, 1000L)
        val res3 = GlobalClicksResponse(200L, 2000L)

        assertEquals(res1, res2)
        assertEquals(res1.hashCode(), res2.hashCode())
        assertNotEquals(res1, res3)
        assertNotEquals(res1.hashCode(), res3.hashCode())
        
        val resStr = res1.toString()
        assert(resStr.contains("100"))
        assert(resStr.contains("1000"))
    }
}
