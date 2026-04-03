package com.subhrodip.oss.whoa.link.dto

data class BulkAnalyticsRequest(
    /**
     * Map of shortCode to current local click count.
     */
    val currentCounts: Map<String, Long>,
    /**
     * The timestamp (epoch millis) of the last successful sync.
     * If provided, the server will only return data for links with activity since this time.
     */
    val lastSyncedAt: Long? = null,
)

data class BulkAnalyticsResponse(
    /**
     * Map of shortCode to the latest authoritative click count.
     * This may only contain a subset of requested codes (the "delta").
     */
    val clicks: Map<String, Long>,
    /**
     * The authoritative server time at which this snapshot was taken.
     * The client should use this for the next 'lastSyncedAt' request.
     */
    val serverTimestamp: Long,
)
