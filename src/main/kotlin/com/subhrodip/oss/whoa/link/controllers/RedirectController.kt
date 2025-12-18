package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.services.UrlReadService
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
class RedirectController(
    private val urlReadService: UrlReadService,
) {
    @GetMapping("/{shortCode}")
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
