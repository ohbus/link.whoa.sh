package com.subhrodip.oss.whoa.link.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockStatic
import org.springframework.transaction.support.TransactionSynchronizationManager

class DataSourceConfigTest {

    @Test
    fun `test RoutingDataSource determineCurrentLookupKey`() {
        val routingDataSource = RoutingDataSource()

        mockStatic(TransactionSynchronizationManager::class.java).use { mockedStatic ->
            mockedStatic.`when`<Boolean> { TransactionSynchronizationManager.isCurrentTransactionReadOnly() }.thenReturn(true)
            assertEquals(DataSourceType.READER, routingDataSource.determineCurrentLookupKey())

            mockedStatic.`when`<Boolean> { TransactionSynchronizationManager.isCurrentTransactionReadOnly() }.thenReturn(false)
            assertEquals(DataSourceType.WRITER, routingDataSource.determineCurrentLookupKey())
        }
    }
}
