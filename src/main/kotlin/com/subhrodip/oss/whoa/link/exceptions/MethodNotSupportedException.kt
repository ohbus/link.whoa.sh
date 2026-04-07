package com.subhrodip.oss.whoa.link.exceptions

import org.springframework.http.HttpStatus

class MethodNotSupportedException(
    message: String,
    override val errorCode: String = "WHOA-1002",
) : RuntimeException(message), WhoaException {
    override val statusCode: HttpStatus = HttpStatus.METHOD_NOT_ALLOWED
}
