package com.subhrodip.oss.whoa.link.dto

data class BulkAnalyticsRequest(
    /**
     * Map of shortCode to current local click count.
     * This allows future "delta" optimizations where the server only responds
     * if the count has actually changed.
     */
    val currentCounts: Map<String, Long>
)

data class BulkAnalyticsResponse(
    /**
     * Map of shortCode to the latest authoritative click count.
     */
    val clicks: Map<String, Long>
)
