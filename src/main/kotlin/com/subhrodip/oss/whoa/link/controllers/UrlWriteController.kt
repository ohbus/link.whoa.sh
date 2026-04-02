package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.services.UrlWriteService
import io.micrometer.core.annotation.Counted
import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.API_V1_URLS)
@Tag(name = "URL Writing", description = "Endpoints for creating new short URLs")
class UrlWriteController(
    private val urlWriteService: UrlWriteService,
) {
    @PostMapping
    @Timed(value = "whoa.urls.create.time", description = "Time taken to create a short URL")
    @Counted(value = "whoa.urls.create.count", description = "Number of short URL creation attempts")
    @Operation(
        summary = "Create a short URL",
        description = "Accepts a long URL and an optional custom short code to generate a shortened link.",
        responses = [
            ApiResponse(responseCode = "201", description = "URL successfully shortened"),
            ApiResponse(responseCode = "400", description = "Invalid URL or parameters"),
            ApiResponse(responseCode = "409", description = "Short code already exists"),
        ],
    )
    fun createShortUrl(
        @Valid @RequestBody request: CreateShortUrlRequest,
    ): ResponseEntity<CreateShortUrlResponse> {
        val response = urlWriteService.createShortUrl(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
