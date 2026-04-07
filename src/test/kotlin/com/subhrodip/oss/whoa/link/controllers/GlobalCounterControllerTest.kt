package com.subhrodip.oss.whoa.link.controllers

import com.subhrodip.oss.whoa.link.services.GlobalCounterService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus

@ExtendWith(MockitoExtension::class)
class GlobalCounterControllerTest {
    @Mock
    private lateinit var globalCounterService: GlobalCounterService

    @InjectMocks
    private lateinit var controller: GlobalCounterController

    @Test
    fun `test getGlobalClicks`() {
        `when`(globalCounterService.getTotalClicks()).thenReturn(100L)

        val result = controller.getGlobalClicks()

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(100L, result.body?.totalClicks)
    }
}
