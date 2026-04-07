package com.subhrodip.oss.whoa.link.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class EntityTest {
    @Test
    fun `test UrlEntity equality`() {
        val entity1 = UrlEntity("https://google.com", "goog")
        entity1.id = 1L

        val entity2 = UrlEntity("https://bing.com", "bing")
        entity2.id = 1L

        val entity3 = UrlEntity("https://google.com", "goog")
        entity3.id = 2L

        assertEquals(entity1, entity2, "Entities with same ID should be equal")
        assertNotEquals(entity1, entity3, "Entities with different IDs should not be equal")
        assertEquals(entity1.hashCode(), entity2.hashCode())
        assertNotEquals(entity1.hashCode(), entity3.hashCode())

        assertEquals(entity1, entity1)
        assertNotEquals(entity1, "string")
        assertNotEquals(entity1, null)

        val entityNoId1 = UrlEntity("a", "b")
        val entityNoId2 = UrlEntity("a", "b")
        assertNotEquals(entityNoId1, entityNoId2, "Entities without ID should not be equal")
    }

    @Test
    fun `test UrlAnalyticsEntity equality`() {
        val url = UrlEntity("a", "b")
        val entity1 = UrlAnalyticsEntity(url, "ua1", "1.1.1.1")
        entity1.id = 1L

        val entity2 = UrlAnalyticsEntity(url, "ua2", "2.2.2.2")
        entity2.id = 1L

        val entity3 = UrlAnalyticsEntity(url, "ua1", "1.1.1.1")
        entity3.id = 2L

        assertEquals(entity1, entity2)
        assertNotEquals(entity1, entity3)
        assertEquals(entity1.hashCode(), entity2.hashCode())

        assertEquals(entity1, entity1)
        assertNotEquals(entity1, null)

        val entityNoId1 = UrlAnalyticsEntity(url, "a", "b")
        val entityNoId2 = UrlAnalyticsEntity(url, "a", "b")
        assertNotEquals(entityNoId1, entityNoId2)
    }

    @Test
    fun `test toString implementations`() {
        val url = UrlEntity("https://google.com", "goog")
        url.id = 123L
        assertTrue(url.toString().contains("id=123"))
        assertTrue(url.toString().contains("shortCode='goog'"))

        val analytics = UrlAnalyticsEntity(url, "ua", "127.0.0.1")
        analytics.id = 456L
        assertTrue(analytics.toString().contains("id=456"))
        assertTrue(analytics.toString().contains("ipAddress='127.0.0.1'"))
    }

    @Test
    fun `test BaseEntity properties`() {
        val now = OffsetDateTime.now()
        val entity = object : BaseEntity(id = 10L, createdAt = now, isNew = false) {}

        assertEquals(10L, entity.id)
        assertEquals(now, entity.createdAt)

        entity.id = 20L
        entity.createdAt = now.plusDays(1)

        assertEquals(20L, entity.id)
        assertEquals(now.plusDays(1), entity.createdAt)
    }
}
