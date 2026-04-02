package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsRequest
import com.subhrodip.oss.whoa.link.dto.BulkAnalyticsResponse
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.services.AnalyticsService
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.API_V1_URLS)
@Tag(name = "Analytics", description = "Endpoints for retrieving URL usage statistics")
class UrlAnalyticsController(
    private val analyticsService: AnalyticsService,
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
    @Timed(value = "whoa.analytics.bulk.time", description = "Time taken to fetch bulk analytics")
    @Operation(
        summary = "Get bulk analytics",
        description = "Returns click counts for a list of short codes in a single request.",
        responses = [
            ApiResponse(responseCode = "200", description = "Bulk analytics retrieved successfully"),
        ],
    )
    fun getBulkAnalytics(
        @RequestBody request: BulkAnalyticsRequest,
    ): ResponseEntity<BulkAnalyticsResponse> {
        val response = analyticsService.getBulkAnalytics(request.currentCounts)
        return ResponseEntity.ok(response)
    }
}
