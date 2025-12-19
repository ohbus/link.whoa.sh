package com.subhrodip.oss.whoa.link.converters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class OffsetDateTimeConverterTest {
    private val converter = OffsetDateTimeConverter()

    @Test
    fun `convertToDatabaseColumn should convert OffsetDateTime to Long`() {
        val offsetDateTime = OffsetDateTime.of(2023, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC)
        val expectedLong = 1673778600000L // Corresponds to 2023-01-15T10:30:00Z in milliseconds

        val actualLong = converter.convertToDatabaseColumn(offsetDateTime)

        assertEquals(expectedLong, actualLong)
    }

    @Test
    fun `convertToDatabaseColumn should return null for null OffsetDateTime`() {
        val actualLong = converter.convertToDatabaseColumn(null)
        assertNull(actualLong)
    }

    @Test
    fun `convertToEntityAttribute should convert Long to OffsetDateTime`() {
        val dbData = 1673778600000L // Corresponds to 2023-01-15T10:30:00Z in milliseconds
        val expectedOffsetDateTime = OffsetDateTime.of(2023, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC)

        val actualOffsetDateTime = converter.convertToEntityAttribute(dbData)

        assertEquals(expectedOffsetDateTime, actualOffsetDateTime)
    }

    @Test
    fun `convertToEntityAttribute should return null for null Long`() {
        val actualOffsetDateTime = converter.convertToEntityAttribute(null)
        assertNull(actualOffsetDateTime)
    }

    @Test
    fun `conversion round-trip should be consistent`() {
        val originalOffsetDateTime =
            OffsetDateTime.of(2024, 7, 20, 15, 45, 30, 123456789, ZoneOffset.ofHours(-5))

        val longRepresentation = converter.convertToDatabaseColumn(originalOffsetDateTime)
        val convertedBackOffsetDateTime = converter.convertToEntityAttribute(longRepresentation)

        val expectedUtcMillis =
            originalOffsetDateTime
                .withOffsetSameInstant(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS)

        assertEquals(expectedUtcMillis, convertedBackOffsetDateTime)
    }
}
