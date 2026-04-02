package com.subhrodip.oss.whoa.link.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

private val log = KotlinLogging.logger {}

enum class DataSourceType { WRITER, READER }

class RoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        val isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        val type = if (isReadOnly) DataSourceType.READER else DataSourceType.WRITER
        log.trace { "Routing database call to: $type" }
        return type
    }
}

@Configuration
class DataSourceConfig {

    @Value("\${spring.datasource.url}")
    private lateinit var writerUrl: String

    @Value("\${spring.datasource.username}")
    private lateinit var writerUsername: String

    @Value("\${spring.datasource.password}")
    private lateinit var writerPassword: String

    @Value("\${spring.datasource.driver-class-name}")
    private lateinit var writerDriver: String

    @Value("\${app.datasource.reader.url:}")
    private lateinit var readerUrl: String

    @Value("\${app.datasource.reader.username:}")
    private lateinit var readerUsername: String

    @Value("\${app.datasource.reader.password:}")
    private lateinit var readerPassword: String

    @Value("\${app.datasource.reader.driver-class-name:}")
    private lateinit var readerDriver: String

    @Bean
    fun writerDataSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = writerUrl
        config.username = writerUsername
        config.password = writerPassword
        config.driverClassName = writerDriver
        config.poolName = "WriterPool"
        return HikariDataSource(config)
    }

    @Bean
    fun readerDataSource(): DataSource {
        val url = if (readerUrl.isBlank()) writerUrl else readerUrl
        val username = if (readerUsername.isBlank()) writerUsername else readerUsername
        val password = if (readerPassword.isBlank()) writerPassword else readerPassword
        val driverClassName = if (readerDriver.isBlank()) writerDriver else readerDriver

        if (readerUrl.isBlank()) {
            log.info("Reader database URL is empty or null. Falling back to WRITER instance for read operations.")
        } else {
            log.info("Reader database configured with URL: {}", url)
        }

        val config = HikariConfig()
        config.jdbcUrl = url
        config.username = username
        config.password = password
        config.driverClassName = driverClassName
        config.poolName = "ReaderPool"
        config.isReadOnly = true

        return HikariDataSource(config)
    }

    @Bean
    @Primary
    fun dataSource(
        @Qualifier("writerDataSource") writerDataSource: DataSource,
        @Qualifier("readerDataSource") readerDataSource: DataSource,
    ): DataSource {
        val routingDataSource = RoutingDataSource()

        val dataSources =
            mapOf<Any, Any>(
                DataSourceType.WRITER to writerDataSource,
                DataSourceType.READER to readerDataSource,
            )

        routingDataSource.setTargetDataSources(dataSources)
        routingDataSource.setDefaultTargetDataSource(writerDataSource)
        routingDataSource.afterPropertiesSet()

        // LazyConnectionDataSourceProxy ensures the connection is not fetched until
        // a statement is created, allowing the @Transactional(readOnly) flag to be set
        // properly before the routing decision is made.
        return LazyConnectionDataSourceProxy(routingDataSource)
    }
}
