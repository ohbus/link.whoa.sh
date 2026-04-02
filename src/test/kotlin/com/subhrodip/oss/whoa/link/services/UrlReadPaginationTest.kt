package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.test.util.ReflectionTestUtils
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class UrlReadPaginationTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var analyticsService: AnalyticsService

    @Mock
    private lateinit var urlCacheService: UrlCacheService

    @Mock
    private lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @InjectMocks
    private lateinit var urlReadService: UrlReadService

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(urlReadService, "baseUrl", "http://localhost:8844")
    }

    @Test
    fun `getPagedUrls returns first page correctly`() {
        val urls =
            listOf(
                UrlEntity("url1", "code1").apply {
                    id = 1L
                    createdAt = OffsetDateTime.now().minusMinutes(1)
                },
                UrlEntity("url2", "code2").apply {
                    id = 2L
                    createdAt = OffsetDateTime.now().minusMinutes(2)
                },
            )

        `when`(urlRepository.findLatest(any())).thenReturn(urls)
        `when`(urlAnalyticsRepository.countByUrlIds(any())).thenReturn(emptyList())

        val response = urlReadService.getPagedUrls(null, 10)

        assertEquals(2, response.links.size)
        assertFalse(response.hasMore)
        assertEquals(null, response.nextCursor)
    }

    @Test
    fun `getPagedUrls identifies hasMore correctly`() {
        val urls =
            listOf(
                UrlEntity("url1", "code1").apply {
                    id = 1L
                    createdAt = OffsetDateTime.now().minusMinutes(1)
                },
                UrlEntity("url2", "code2").apply {
                    id = 2L
                    createdAt = OffsetDateTime.now().minusMinutes(2)
                },
                UrlEntity("url3", "code3").apply {
                    id = 3L
                    createdAt = OffsetDateTime.now().minusMinutes(3)
                },
            )

        `when`(urlRepository.findLatest(any())).thenReturn(urls)
        `when`(urlAnalyticsRepository.countByUrlIds(any())).thenReturn(emptyList())

        val response = urlReadService.getPagedUrls(null, 2)

        assertEquals(2, response.links.size)
        assertTrue(response.hasMore)
        assertTrue(response.nextCursor != null)
    }
}
