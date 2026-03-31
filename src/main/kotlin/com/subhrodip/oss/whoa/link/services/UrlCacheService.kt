package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UrlCacheService(
    private val urlRepository: UrlRepository,
) {

    @Cacheable(value = ["urls"], key = "'url:' + #shortCode", unless = "#result == null")
    fun getCachedUrl(shortCode: String): UrlDto {
        val entity =
            urlRepository.findByShortCode(shortCode)
                ?: throw UrlNotFoundException("URL not found for short code: $shortCode")

        return UrlDto(
            originalUrl = entity.originalUrl,
            shortCode = entity.shortCode,
        )
    }

    @CachePut(value = ["urls"], key = "'url:' + #urlEntity.shortCode")
    fun putInCache(urlEntity: UrlEntity): UrlDto {
        return UrlDto(
            originalUrl = urlEntity.originalUrl,
            shortCode = urlEntity.shortCode,
        )
    }
}
