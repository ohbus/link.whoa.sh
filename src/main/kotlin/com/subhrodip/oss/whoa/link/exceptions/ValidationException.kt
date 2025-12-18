package com.subhrodip.oss.whoa.link.exceptions

class ValidationException(
    message: String,
) : RuntimeException(message) {
    companion object {
        const val ERROR_CODE = 300001
    }
}
