package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import com.subhrodip.oss.whoa.link.services.GlobalCounterService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class TestingControllerTest {

    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @Mock
    private lateinit var globalCounterService: GlobalCounterService

    @Mock
    private lateinit var cacheManager: CacheManager

    @InjectMocks
    private lateinit var controller: TestingController

    @Test
    fun `test resetState`() {
        `when`(cacheManager.cacheNames).thenReturn(listOf("cache1"))
        val mockCache = mock(Cache::class.java)
        `when`(cacheManager.getCache("cache1")).thenReturn(mockCache)

        val result = controller.resetState()

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("success", result.body?.get("status"))
        
        verify(urlAnalyticsRepository).deleteAllInBatch()
        verify(urlRepository).deleteAllInBatch()
        verify(mockCache).clear()
        verify(globalCounterService).refreshFromDatabase()
    }
}
