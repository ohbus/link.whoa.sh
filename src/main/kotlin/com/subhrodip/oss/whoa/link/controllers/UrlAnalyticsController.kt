package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.services.AnalyticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.API_V1_URLS)
class UrlAnalyticsController(
    private val analyticsService: AnalyticsService,
) {
    @GetMapping("/{shortCode}${UrlConstants.ANALYTICS_PATH}")
    fun getUrlAnalytics(
        @PathVariable shortCode: String,
    ): ResponseEntity<UrlAnalyticsResponse> {
        val response = analyticsService.getUrlAnalytics(shortCode)
        return ResponseEntity.ok(response)
    }
}
