package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.dto.UrlAnalyticsResponse
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val urlAnalyticsRepository: UrlAnalyticsRepository,
    private val urlRepository: UrlRepository,
) {
    @Value("\${app.baseUrl:http://localhost:8844}")
    private lateinit var baseUrl: String

    @Async
    @Transactional
    fun trackAnalytics(
        urlEntity: UrlEntity,
        userAgent: String?,
        ipAddress: String?,
    ) {
        val analytics =
            UrlAnalyticsEntity(
                urlEntity = urlEntity,
                userAgent = userAgent,
                ipAddress = ipAddress,
            )
        urlAnalyticsRepository.save(analytics)
    }

    @Transactional(readOnly = true)
    fun getUrlAnalytics(shortCode: String): UrlAnalyticsResponse {
        val url = urlRepository.findByShortCode(shortCode) ?: throw UrlNotFoundException("URL not found for short code: $shortCode")
        val clicks = urlAnalyticsRepository.countByUrlEntityId(url.id)
        return UrlAnalyticsResponse(
            originalUrl = url.originalUrl,
            shortUrl = "$baseUrl/${url.shortCode}",
            clicks = clicks,
        )
    }
}
