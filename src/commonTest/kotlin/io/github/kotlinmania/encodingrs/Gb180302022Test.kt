// port-lint: source gb18030_2022.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals

class Gb180302022Test {
    @Test
    fun overrideTablesStayPaired() {
        assertEquals(18, GB18030_2022_OVERRIDE_PUA.size)
        assertEquals(18, GB18030_2022_OVERRIDE_BYTES.size)
        assertEquals(0xE78Du.toUShort(), GB18030_2022_OVERRIDE_PUA.first())
        assertEquals(
            listOf(0xA6u.toUByte(), 0xD9u.toUByte()),
            GB18030_2022_OVERRIDE_BYTES.first().toList(),
        )
        assertEquals(0xE864u.toUShort(), GB18030_2022_OVERRIDE_PUA.last())
        assertEquals(
            listOf(0xFEu.toUByte(), 0xA0u.toUByte()),
            GB18030_2022_OVERRIDE_BYTES.last().toList(),
        )
    }
}
