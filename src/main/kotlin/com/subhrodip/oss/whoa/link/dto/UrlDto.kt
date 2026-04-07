package com.subhrodip.oss.whoa.link.dto

import java.io.Serializable
import java.time.OffsetDateTime

data class UrlDto(
    val id: Long,
    val originalUrl: String,
    val shortCode: String,
    val totalClicks: Long = 0,
    val createdAt: OffsetDateTime? = null,
) : Serializable
