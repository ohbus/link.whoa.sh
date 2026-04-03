package com.subhrodip.oss.whoa.link.config

import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class DataSourceConfigTest {

    @InjectMocks
    lateinit var dataSourceConfig: DataSourceConfig

    @Test
    fun `writerDataSource should configure hikari with writer props`() {
        ReflectionTestUtils.setField(dataSourceConfig, "writerUrl", "jdbc:h2:mem:writer")
        ReflectionTestUtils.setField(dataSourceConfig, "writerUsername", "user")
        ReflectionTestUtils.setField(dataSourceConfig, "writerPassword", "pass")
        ReflectionTestUtils.setField(dataSourceConfig, "writerDriver", "org.h2.Driver")

        val ds = dataSourceConfig.writerDataSource() as HikariDataSource

        assertEquals("jdbc:h2:mem:writer", ds.jdbcUrl)
        assertEquals("user", ds.username)
        assertEquals("pass", ds.password)
        assertEquals("WriterPool", ds.poolName)
    }

    @Test
    fun `readerDataSource should fallback to writer if reader props missing`() {
        ReflectionTestUtils.setField(dataSourceConfig, "writerUrl", "jdbc:h2:mem:writer")
        ReflectionTestUtils.setField(dataSourceConfig, "writerUsername", "user")
        ReflectionTestUtils.setField(dataSourceConfig, "writerPassword", "pass")
        ReflectionTestUtils.setField(dataSourceConfig, "writerDriver", "org.h2.Driver")
        ReflectionTestUtils.setField(dataSourceConfig, "readerUrl", "")
        ReflectionTestUtils.setField(dataSourceConfig, "readerUsername", "")
        ReflectionTestUtils.setField(dataSourceConfig, "readerPassword", "")
        ReflectionTestUtils.setField(dataSourceConfig, "readerDriver", "")

        val ds = dataSourceConfig.readerDataSource() as HikariDataSource

        assertEquals("jdbc:h2:mem:writer", ds.jdbcUrl)
        assertTrue(ds.isReadOnly)
        assertEquals("ReaderPool", ds.poolName)
    }

    @Test
    fun `readerDataSource should use reader props if present`() {
        ReflectionTestUtils.setField(dataSourceConfig, "writerUrl", "jdbc:h2:mem:writer")
        ReflectionTestUtils.setField(dataSourceConfig, "writerUsername", "user")
        ReflectionTestUtils.setField(dataSourceConfig, "writerPassword", "pass")
        ReflectionTestUtils.setField(dataSourceConfig, "writerDriver", "org.h2.Driver")
        ReflectionTestUtils.setField(dataSourceConfig, "readerUrl", "jdbc:h2:mem:reader")
        ReflectionTestUtils.setField(dataSourceConfig, "readerUsername", "reader_user")
        ReflectionTestUtils.setField(dataSourceConfig, "readerPassword", "reader_pass")
        ReflectionTestUtils.setField(dataSourceConfig, "readerDriver", "org.h2.Driver")

        val ds = dataSourceConfig.readerDataSource() as HikariDataSource

        assertEquals("jdbc:h2:mem:reader", ds.jdbcUrl)
        assertEquals("reader_user", ds.username)
        assertTrue(ds.isReadOnly)
    }

    @Test
    fun `dataSource bean builds routing proxy`() {
        val writer = HikariDataSource()
        val reader = HikariDataSource()
        
        val ds = dataSourceConfig.dataSource(writer, reader)
        assertNotNull(ds)
    }
}
