package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import com.subhrodip.oss.whoa.link.services.GlobalCounterService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class TestingControllerTest {
    @Mock
    lateinit var urlRepository: UrlRepository

    @Mock
    lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @Mock
    lateinit var globalCounterService: GlobalCounterService

    @Mock
    lateinit var cacheManager: CacheManager

    @InjectMocks
    lateinit var testingController: TestingController

    @Test
    fun `resetState should wipe databases, clear caches and refresh counter`() {
        val cache = mock(Cache::class.java)
        `when`(cacheManager.cacheNames).thenReturn(listOf("urls", "analytics"))
        `when`(cacheManager.getCache(anyString())).thenReturn(cache)

        val response = testingController.resetState()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body?.get("status"))

        verify(urlAnalyticsRepository).deleteAllInBatch()
        verify(urlRepository).deleteAllInBatch()
        verify(cacheManager, times(2)).getCache(anyString())
        verify(cache, times(2)).clear()
        verify(globalCounterService).refreshFromDatabase()
    }
}
