package com.subhrodip.oss.whoa.link.exceptions

class UrlNotFoundException(
    message: String,
) : RuntimeException(message) {
    companion object {
        const val ERROR_CODE = 200001
    }
}
