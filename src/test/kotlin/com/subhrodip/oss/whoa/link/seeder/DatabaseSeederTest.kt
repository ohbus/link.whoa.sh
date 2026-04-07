package com.subhrodip.oss.whoa.link.seeder

import com.subhrodip.oss.whoa.link.domain.UrlAnalyticsEntity
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times

@ExtendWith(MockitoExtension::class)
class DatabaseSeederTest {
    @Mock
    private lateinit var urlRepository: UrlRepository

    @Mock
    private lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @InjectMocks
    private lateinit var databaseSeeder: DatabaseSeeder

    @Test
    fun `test run when database is empty`() {
        `when`(urlRepository.count()).thenReturn(0L)
        `when`(urlRepository.save(any())).thenAnswer { it.arguments[0] }

        databaseSeeder.run()

        verify(urlRepository, times(8)).save(any())
        verify(urlAnalyticsRepository, times(8)).saveAll(any<List<UrlAnalyticsEntity>>())
    }

    @Test
    fun `test run when database is not empty`() {
        `when`(urlRepository.count()).thenReturn(10L)

        databaseSeeder.run()

        verifyNoInteractions(urlAnalyticsRepository)
    }
}
