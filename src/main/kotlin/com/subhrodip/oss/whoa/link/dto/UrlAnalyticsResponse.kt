package com.subhrodip.oss.whoa.link.dto

import java.time.OffsetDateTime

data class UrlAnalyticsResponse(
    val originalUrl: String,
    val shortUrl: String,
    val clicks: Long,
    val createdAt: OffsetDateTime? = null
)
