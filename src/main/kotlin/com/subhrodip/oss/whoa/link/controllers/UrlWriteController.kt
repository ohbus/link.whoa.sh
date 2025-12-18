package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.constants.UrlConstants
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.services.UrlWriteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.API_V1_URLS)
class UrlWriteController(
    private val urlWriteService: UrlWriteService,
) {
    @PostMapping
    fun createShortUrl(
        @Valid @RequestBody request: CreateShortUrlRequest,
    ): ResponseEntity<CreateShortUrlResponse> {
        val response = urlWriteService.createShortUrl(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
