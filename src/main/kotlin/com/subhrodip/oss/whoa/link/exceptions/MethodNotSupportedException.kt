package com.subhrodip.oss.whoa.link.exceptions

class MethodNotSupportedException(
    message: String,
) : RuntimeException(message) {
    companion object {
        const val ERROR_CODE = 400001
    }
}
