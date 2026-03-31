package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.UrlDto
import com.subhrodip.oss.whoa.link.exceptions.UrlNotFoundException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UrlCacheServiceTest {

    @Mock
    private lateinit var urlRepository: UrlRepository

    @InjectMocks
    private lateinit var urlCacheService: UrlCacheService

    @Test
    fun `test getCachedUrl returns DTO when found`() {
        val shortCode = "test"
        val entity = UrlEntity(originalUrl = "https://example.com", shortCode = shortCode)
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(entity)

        val result = urlCacheService.getCachedUrl(shortCode)

        assertEquals("https://example.com", result.originalUrl)
        assertEquals(shortCode, result.shortCode)
    }

    @Test
    fun `test getCachedUrl throws exception when not found`() {
        val shortCode = "missing"
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(null)

        assertThrows<UrlNotFoundException> {
            urlCacheService.getCachedUrl(shortCode)
        }
    }

    @Test
    fun `test putInCache converts entity to DTO`() {
        val entity = UrlEntity(originalUrl = "https://example.com", shortCode = "test")
        
        val result = urlCacheService.putInCache(entity)

        assertEquals(entity.originalUrl, result.originalUrl)
        assertEquals(entity.shortCode, result.shortCode)
    }
}
