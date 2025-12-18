package com.subhrodip.oss.whoa.link.exceptions

class InternalServerErrorException(
    message: String,
) : RuntimeException(message) {
    companion object {
        const val ERROR_CODE = 100001
    }
}
