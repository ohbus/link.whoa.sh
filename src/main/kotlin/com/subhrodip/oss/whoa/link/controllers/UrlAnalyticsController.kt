package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsRequest
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.PagedUrlsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.services.AnalyticsService
import com.subhrodip.oss.whoa.link.services.UrlReadService
import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.API_V1_URLS)
@Tag(name = "Analytics", description = "Endpoints for retrieving URL usage statistics")
class UrlAnalyticsController(
    private val analyticsService: AnalyticsService,
    private val urlReadService: UrlReadService,
) {
    @GetMapping("/{shortCode}/analytics")
    @Timed(value = "whoa.analytics.single.time", description = "Time taken to fetch analytics for a single URL")
    @Operation(
        summary = "Get analytics for a short code",
        description = "Returns click counts and original metadata for the provided short link.",
        responses = [
            ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Short code not found"),
        ],
    )
    fun getUrlAnalytics(
        @PathVariable shortCode: String,
    ): ResponseEntity<UrlAnalyticsResponse> {
        val response = analyticsService.getUrlAnalytics(shortCode)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/analytics/bulk")
    @Timed(value = "whoa.analytics.bulk.time", description = "Time taken to fetch bulk analytics with delta sync support")
    @Operation(
        summary = "Get bulk analytics",
        description = "Returns click counts for a list of short codes. Supports delta sync via lastSyncedAt.",
        responses = [
            ApiResponse(responseCode = "200", description = "Bulk analytics retrieved successfully"),
        ],
    )
    fun getBulkAnalytics(
        @RequestBody request: BulkAnalyticsRequest,
    ): ResponseEntity<BulkAnalyticsResponse> {
        val response = analyticsService.getBulkAnalytics(request.currentCounts, request.lastSyncedAt)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Timed(value = "whoa.urls.list.time", description = "Time taken to list paged URLs")
    @Operation(
        summary = "List registered short links",
        description = "Returns a paged list of all short links registered in the global registry.",
        responses = [
            ApiResponse(responseCode = "200", description = "List retrieved successfully"),
        ],
    )
    fun getPagedUrls(
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<PagedUrlsResponse> {
        val response = urlReadService.getPagedUrls(cursor, limit)
        return ResponseEntity.ok(response)
    }
}
