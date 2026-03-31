package com.subhrodip.oss.whoa.link.dto

import java.io.Serializable

data class UrlDto(
    val originalUrl: String,
    val shortCode: String,
) : Serializable
