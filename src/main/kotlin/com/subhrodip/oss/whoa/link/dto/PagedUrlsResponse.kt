package com.subhrodip.oss.whoa.link.dto

data class PagedUrlsResponse(
    val links: List<UrlAnalyticsResponse>,
    val nextCursor: Long?, // Timestamp of the last item for next Keyset query
    val hasMore: Boolean,
)
