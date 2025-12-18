package com.subhrodip.oss.whoa.link.dto

import jakarta.validation.constraints.NotEmpty

data class CreateShortUrlRequest(
    @field:NotEmpty(message = "URL cannot be empty")
    val url: String,
)
