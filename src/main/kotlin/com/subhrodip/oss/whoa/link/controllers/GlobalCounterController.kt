package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.GlobalClicksResponse
import com.subhrodip.oss.whoa.link.services.GlobalCounterService
import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${UrlConstants.API_V1_URLS}/analytics")
@Tag(name = "Analytics", description = "Endpoints for retrieving authoritative global statistics")
class GlobalCounterController(
    private val globalCounterService: GlobalCounterService
) {
    @GetMapping("/global")
    @Timed(value = "whoa.analytics.global.time", description = "Time taken to fetch in-memory global clicks")
    @Operation(summary = "Get authoritative global click count")
    fun getGlobalClicks(): ResponseEntity<GlobalClicksResponse> {
        return ResponseEntity.ok(
            GlobalClicksResponse(
                totalClicks = globalCounterService.getTotalClicks(),
                serverTimestamp = System.currentTimeMillis()
            )
        )
    }
}
