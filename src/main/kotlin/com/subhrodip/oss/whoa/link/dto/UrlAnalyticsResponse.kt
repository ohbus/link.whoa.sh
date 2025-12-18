package com.subhrodip.oss.whoa.link.dto

data class UrlAnalyticsResponse(
    val originalUrl: String,
    val shortUrl: String,
    val clicks: Long,
)
