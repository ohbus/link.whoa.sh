package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlResponse
import com.subhrodip.oss.whoa.link.exceptions.ShortCodeAlreadyExistsException
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
    private val shortCodeChars = "abcdefghijklmnopqrstuvwxyz".toCharArray()

    fun createShortUrl(request: CreateShortUrlRequest): CreateShortUrlResponse {
        val shortCode =
            if (request.shortCode.isNullOrBlank()) {
                generateUniqueShortCode()
            } else {
                if (urlRepository.findByShortCode(request.shortCode) != null) {
                    throw ShortCodeAlreadyExistsException("Short code '${request.shortCode}' already exists")
                }
                request.shortCode
            }

        val urlEntity =
            UrlEntity(
                originalUrl = request.url,
                shortCode = shortCode,
            )
        urlRepository.save(urlEntity)
        return CreateShortUrlResponse(
            originalUrl = urlEntity.originalUrl,
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
