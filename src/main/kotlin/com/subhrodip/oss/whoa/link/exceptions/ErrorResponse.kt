package com.subhrodip.oss.whoa.link.exceptions

import java.time.OffsetDateTime

data class ErrorResponse(
    val statusCode: Int,
    val errorCode: String,
    val message: String?,
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
)
