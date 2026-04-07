package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus

class UrlNotFoundException(
    message: String,
    override val errorCode: String = "WHOA-2001",
) : RuntimeException(message),
    WhoaException {
    override val statusCode: HttpStatus = HttpStatus.NOT_FOUND
}
