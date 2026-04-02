package com.subhrodip.oss.whoa.link.dto

data class BulkAnalyticsRequest(
    val shortCodes: List<String>
)

data class BulkAnalyticsResponse(
    val clicks: Map<String, Long>
)
