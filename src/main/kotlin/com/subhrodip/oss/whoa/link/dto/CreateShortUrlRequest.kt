package com.subhrodip.oss.whoa.link.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateShortUrlRequest(
    @field:NotEmpty(message = "URL cannot be empty")
    val url: String,
    @field:Size(max = 10, message = "Short code can be at most 10 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Short code can only contain alphanumeric characters")
    val shortCode: String? = null,
)
