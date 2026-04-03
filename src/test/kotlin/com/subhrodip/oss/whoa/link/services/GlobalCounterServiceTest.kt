package com.subhrodip.oss.whoa.link.services

import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GlobalCounterServiceTest {

    @Mock
    lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @InjectMocks
    lateinit var globalCounterService: GlobalCounterService

    @Test
    fun `init should refresh from db`() {
        `when`(urlAnalyticsRepository.countAllClicks()).thenReturn(100L)
        globalCounterService.init()
        assertEquals(100L, globalCounterService.getTotalClicks())
    }

    @Test
    fun `refreshFromDatabase should update local state if differs`() {
        `when`(urlAnalyticsRepository.countAllClicks()).thenReturn(150L)
        globalCounterService.refreshFromDatabase()
        assertEquals(150L, globalCounterService.getTotalClicks())
    }

    @Test
    fun `incrementRealTime should increment local state instantly`() {
        `when`(urlAnalyticsRepository.countAllClicks()).thenReturn(0L)
        globalCounterService.init()
        
        globalCounterService.incrementRealTime()
        assertEquals(1L, globalCounterService.getTotalClicks())
    }
}
