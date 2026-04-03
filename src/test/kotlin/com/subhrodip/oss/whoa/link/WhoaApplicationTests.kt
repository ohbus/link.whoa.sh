package com.subhrodip.oss.whoa.link

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.assertDoesNotThrow

@SpringBootTest
class WhoaApplicationTests {
    @Test
    fun contextLoads() {
        assertDoesNotThrow {
            WhoaApplication()
        }
    }
}
