package com.subhrodip.oss.whoa.link.converters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeConverterTest {

    private val converter = OffsetDateTimeConverter()

    @Test
    fun `test convertToDatabaseColumn`() {
        assertNull(converter.convertToDatabaseColumn(null))
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        assertEquals(now.toInstant().toEpochMilli(), converter.convertToDatabaseColumn(now))
    }

    @Test
    fun `test convertToEntityAttribute`() {
        assertNull(converter.convertToEntityAttribute(null))
        val millis = 1712476800000L // 2024-04-07
        val result = converter.convertToEntityAttribute(millis)
        assertEquals(millis, result?.toInstant()?.toEpochMilli())
    }
}
