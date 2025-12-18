package com.subhrodip.oss.whoa.link.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Converter(autoApply = true)
class OffsetDateTimeConverter : AttributeConverter<OffsetDateTime, Long> {
    override fun convertToDatabaseColumn(attribute: OffsetDateTime?): Long? = attribute?.toInstant()?.toEpochMilli()

    override fun convertToEntityAttribute(dbData: Long?): OffsetDateTime? =
        dbData?.let {
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }
}
