package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus

class InternalServerErrorException(
    message: String,
    override val errorCode: String = "WHOA-9001",
) : RuntimeException(message),
    WhoaException {
    override val statusCode: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
}
