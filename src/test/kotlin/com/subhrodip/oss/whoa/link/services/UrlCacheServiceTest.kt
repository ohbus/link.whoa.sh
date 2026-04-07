package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
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
    fun `test getCachedUrl success`() {
        val shortCode = "abc"
        val entity = UrlEntity("https://url.com", shortCode)
        entity.id = 1L
        
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(entity)
        
        val result = urlCacheService.getCachedUrl(shortCode)
        
        assertEquals(1L, result.id)
        assertEquals(shortCode, result.shortCode)
    }

    @Test
    fun `test getCachedUrl not found`() {
        val shortCode = "abc"
        `when`(urlRepository.findByShortCode(shortCode)).thenReturn(null)
        
        assertThrows<UrlNotFoundException> {
            urlCacheService.getCachedUrl(shortCode)
        }
    }

    @Test
    fun `test putInCache`() {
        val entity = UrlEntity("https://url.com", "abc")
        entity.id = 1L
        
        val result = urlCacheService.putInCache(entity)
        
        assertEquals(1L, result.id)
        assertEquals("abc", result.shortCode)
    }

    @Test
    fun `test evictUrlCache`() {
        // This is mainly for coverage as it just logs
        urlCacheService.evictUrlCache("abc")
    }
}
