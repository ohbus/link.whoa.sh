package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UrlReadService(
    private val urlRepository: UrlRepository,
    private val analyticsService: AnalyticsService,
) {
    fun getOriginalUrl(
        shortCode: String,
        userAgent: String?,
        ipAddress: String?,
    ): String {
        val url = urlRepository.findByShortCode(shortCode) ?: throw UrlNotFoundException("URL not found for short code: $shortCode")
        analyticsService.trackAnalytics(url, userAgent, ipAddress)
        // If original URL does not contain a protocol, return http:// by default
        if (!url.originalUrl.startsWith("http")) {
            return "https://${url.originalUrl}"
        }
        return url.originalUrl
    }
}
