package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus

class ShortCodeAlreadyExistsException(
    message: String,
    override val errorCode: String = "WHOA-2002",
) : RuntimeException(message),
    WhoaException {
    override val statusCode: HttpStatus = HttpStatus.CONFLICT
}
