package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus

/**
 * Base interface for all domain-specific exceptions.
 * Ensures every exception has a machine-readable error code and an appropriate HTTP status.
 */
interface WhoaException {
    val errorCode: String
    val statusCode: HttpStatus
}
