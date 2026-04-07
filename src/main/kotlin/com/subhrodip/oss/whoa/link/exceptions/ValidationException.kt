package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus

class ValidationException(
    message: String,
    override val errorCode: String = "WHOA-1001",
) : RuntimeException(message), WhoaException {
    override val statusCode: HttpStatus = HttpStatus.BAD_REQUEST
}
