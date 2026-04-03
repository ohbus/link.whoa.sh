package com.subhrodip.oss.whoa.link.seeder

import com.subhrodip.oss.whoa.link.domain.UrlEntity
import com.subhrodip.oss.whoa.link.repositories.UrlAnalyticsRepository
import com.subhrodip.oss.whoa.link.repositories.UrlRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.ArgumentMatchers.anyList

@ExtendWith(MockitoExtension::class)
class DatabaseSeederTest {

    @Mock
    lateinit var urlRepository: UrlRepository

    @Mock
    lateinit var urlAnalyticsRepository: UrlAnalyticsRepository

    @InjectMocks
    lateinit var databaseSeeder: DatabaseSeeder

    @Test
    fun `should skip seeding if database is already seeded`() {
        `when`(urlRepository.count()).thenReturn(1L)
        
        databaseSeeder.run()
        
        verify(urlRepository, never()).save(any())
        verify(urlAnalyticsRepository, never()).saveAll(anyList())
    }

    @Test
    fun `should seed data if database is empty`() {
        `when`(urlRepository.count()).thenReturn(0L)
        `when`(urlRepository.save(any(UrlEntity::class.java))).thenAnswer { it.getArgument(0) }
        
        databaseSeeder.run()
        
        verify(urlRepository, times(8)).save(any(UrlEntity::class.java))
        verify(urlAnalyticsRepository, times(8)).saveAll(anyList())
    }
}
