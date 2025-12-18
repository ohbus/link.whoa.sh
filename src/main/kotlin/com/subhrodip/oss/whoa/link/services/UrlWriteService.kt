package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.Url
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom

@Service
@Transactional
class UrlWriteService(
    private val urlRepository: UrlRepository,
) {
    @Value("\${app.baseUrl:http://localhost:8844}")
    private lateinit var baseUrl: String

    private val random = SecureRandom()
    private val shortCodeChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

    fun createShortUrl(request: CreateShortUrlRequest): CreateShortUrlResponse {
        val shortCode = generateUniqueShortCode()
        val url =
            Url(
                originalUrl = request.url,
                shortCode = shortCode,
            )
        urlRepository.save(url)
        return CreateShortUrlResponse(
            originalUrl = url.originalUrl,
            shortUrl = "$baseUrl/$shortCode",
        )
    }

    private fun generateUniqueShortCode(): String {
        var shortCode: String
        do {
            shortCode =
                (1..6)
                    .map { shortCodeChars[random.nextInt(shortCodeChars.size)] }
                    .joinToString("")
        } while (urlRepository.findByShortCode(shortCode) != null)
        return shortCode
    }
}
