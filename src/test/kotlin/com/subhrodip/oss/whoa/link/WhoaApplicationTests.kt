package com.subhrodip.oss.whoa.link

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WhoaApplicationTests {
    @Test
    fun contextLoads() {
        assertDoesNotThrow {
            WhoaApplication()
        }
    }
}
