package com.subhrodip.oss.whoa.link.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.OffsetDateTime

/**
 * Standard error response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val statusCode: Int,
    val errorCode: String,
    val message: String?,
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val traceId: String? = null,
    val errors: Map<String, String>? = null,
)
