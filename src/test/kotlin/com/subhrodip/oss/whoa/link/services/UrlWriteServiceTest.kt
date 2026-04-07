package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.dto.CreateShortUrlRequest
import com.subhrodip.oss.whoa.link.exceptions.ShortCodeAlreadyExistsException
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockitoExtension::class)
class UrlWriteServiceTest {

    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var urlCacheService: UrlCacheService

    @InjectMocks
    private lateinit var urlWriteService: UrlWriteService

    @Test
    fun `test createShortUrl with generated code`() {
        ReflectionTestUtils.setField(urlWriteService, "baseUrl", "http://localhost:8844")
        val request = CreateShortUrlRequest("https://google.com")
        `when`(urlRepository.getNextShortCodeId()).thenReturn(12345L)
        `when`(urlRepository.save(any<UrlEntity>())).thenAnswer { it.arguments[0] }

        val result = urlWriteService.createShortUrl(request)

        assertEquals("https://google.com", result.originalUrl)
        verify(urlRepository).save(any<UrlEntity>())
        verify(urlCacheService).putInCache(any<UrlEntity>())
    }

    @Test
    fun `test createShortUrl with custom code success`() {
        ReflectionTestUtils.setField(urlWriteService, "baseUrl", "http://localhost:8844")
        val request = CreateShortUrlRequest("https://google.com", "my-custom-code")
        `when`(urlRepository.findByShortCode("my-custom-code")).thenReturn(null)
        `when`(urlRepository.save(any<UrlEntity>())).thenAnswer { it.arguments[0] }

        val result = urlWriteService.createShortUrl(request)

        assertEquals("http://localhost:8844/my-custom-code", result.shortUrl)
        verify(urlRepository).save(any<UrlEntity>())
    }

    @Test
    fun `test createShortUrl with custom code conflict`() {
        val request = CreateShortUrlRequest("https://google.com", "existing-code")
        `when`(urlRepository.findByShortCode("existing-code")).thenReturn(UrlEntity("http://other.com", "existing-code"))

        assertThrows<ShortCodeAlreadyExistsException> {
            urlWriteService.createShortUrl(request)
        }
    }
}
