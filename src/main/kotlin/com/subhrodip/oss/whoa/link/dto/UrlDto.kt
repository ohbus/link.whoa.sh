package com.subhrodip.oss.whoa.link.dto

import java.io.Serializable

data class UrlDto(
    val id: Long,
    val originalUrl: String,
    val shortCode: String,
) : Serializable
