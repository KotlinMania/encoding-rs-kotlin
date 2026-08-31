// port-lint: tests encoding_rs/src/gb18030_2022.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals

class Gb180302022Test {
    @Test
    fun overrideTablesStayPaired() {
        assertEquals(18, gb180302022OverrideCount())
        assertEquals(18, GB18030_2022_OVERRIDE_BYTES.size)
        assertEquals(0xE78D, gb180302022OverridePuaAt(0))
        assertEquals(0xA6, gb180302022OverrideByteAt(0, 0))
        assertEquals(0xD9, gb180302022OverrideByteAt(0, 1))
        assertEquals(0xE864, gb180302022OverridePuaAt(17))
        assertEquals(0xFE, gb180302022OverrideByteAt(17, 0))
        assertEquals(0xA0, gb180302022OverrideByteAt(17, 1))
    }
}
