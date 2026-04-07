package com.subhrodip.oss.whoa.link.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Base62EncoderTest {

    @Test
    fun `test encode`() {
        assertEquals("a", Base62Encoder.encode(0))
        assertEquals("b", Base62Encoder.encode(1))
        assertEquals("9", Base62Encoder.encode(61))
        assertEquals("ba", Base62Encoder.encode(62))
        assertEquals("bb", Base62Encoder.encode(63))
    }
}
