package com.subhrodip.oss.whoa.link.util

object Base62Encoder {
    private const val CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private val BASE = CHARACTERS.length.toLong()

    fun encode(value: Long): String {
        if (value == 0L) return CHARACTERS[0].toString()

        var n = value
        val sb = StringBuilder()
        while (n > 0) {
            sb.append(CHARACTERS[(n % BASE).toInt()])
            n /= BASE
        }
        return sb.reverse().toString()
    }
}
