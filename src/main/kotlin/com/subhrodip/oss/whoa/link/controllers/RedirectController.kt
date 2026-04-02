package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.services.UrlReadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping
@Tag(name = "Redirection", description = "Endpoints for following short links")
class RedirectController(
    private val urlReadService: UrlReadService,
) {
    @GetMapping("/{shortCode}")
    @Operation(
        summary = "Redirect to original URL",
        description = "Looks up the original long URL for the given short code and performs a 302 redirect.",
        responses = [
            ApiResponse(responseCode = "302", description = "Redirection successful"),
            ApiResponse(responseCode = "404", description = "Short code not found"),
        ],
    )
    fun redirectToOriginalUrl(
        @PathVariable shortCode: String,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val userAgent = request.getHeader("User-Agent")
        val ipAddress = request.remoteAddr
        val originalUrl = urlReadService.getOriginalUrl(shortCode, userAgent, ipAddress)
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build()
    }
}
