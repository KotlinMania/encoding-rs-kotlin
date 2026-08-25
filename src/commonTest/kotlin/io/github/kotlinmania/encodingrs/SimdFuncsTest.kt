// port-lint: tests simd_funcs.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SimdFuncsTest {
    @Test
    fun testUnpack() {
        val ascii = byteArrayOf(
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
            0x69, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76,
        )
        val basicLatin = charArrayOf(
            0x61.toChar(), 0x62.toChar(), 0x63.toChar(), 0x64.toChar(),
            0x65.toChar(), 0x66.toChar(), 0x67.toChar(), 0x68.toChar(),
            0x69.toChar(), 0x70.toChar(), 0x71.toChar(), 0x72.toChar(),
            0x73.toChar(), 0x74.toChar(), 0x75.toChar(), 0x76.toChar(),
        )
        val unpacked = CharArray(16) { ascii[it].toInt().toChar() }
        assertContentEquals(basicLatin, unpacked)
    }

    @Test
    fun testSimdIsBasicLatinSuccess() {
        val basicLatin = charArrayOf(
            0x61.toChar(), 0x62.toChar(), 0x63.toChar(), 0x64.toChar(),
            0x65.toChar(), 0x66.toChar(), 0x67.toChar(), 0x68.toChar(),
            0x69.toChar(), 0x70.toChar(), 0x71.toChar(), 0x72.toChar(),
            0x73.toChar(), 0x74.toChar(), 0x75.toChar(), 0x76.toChar(),
        )
        val isBasicLatin = basicLatin.all { it.code < 0x80 }
        assertTrue(isBasicLatin)
    }

    @Test
    fun testSimdIsBasicLatinC0() {
        val input = charArrayOf(
            0x61.toChar(), 0x62.toChar(), 0x63.toChar(), 0x81.toChar(),
            0x65.toChar(), 0x66.toChar(), 0x67.toChar(), 0x68.toChar(),
            0x69.toChar(), 0x70.toChar(), 0x71.toChar(), 0x72.toChar(),
            0x73.toChar(), 0x74.toChar(), 0x75.toChar(), 0x76.toChar(),
        )
        val isBasicLatin = input.all { it.code < 0x80 }
        assertFalse(isBasicLatin)
    }

    @Test
    fun testSimdIsBasicLatin0fff() {
        val input = charArrayOf(
            0x61.toChar(), 0x62.toChar(), 0x63.toChar(), 0x0FFF.toChar(),
            0x65.toChar(), 0x66.toChar(), 0x67.toChar(), 0x68.toChar(),
            0x69.toChar(), 0x70.toChar(), 0x71.toChar(), 0x72.toChar(),
            0x73.toChar(), 0x74.toChar(), 0x75.toChar(), 0x76.toChar(),
        )
        val isBasicLatin = input.all { it.code < 0x80 }
        assertFalse(isBasicLatin)
    }

    @Test
    fun testSimdIsBasicLatinFfff() {
        val input = charArrayOf(
            0x61.toChar(), 0x62.toChar(), 0x63.toChar(), 0xFFFF.toChar(),
            0x65.toChar(), 0x66.toChar(), 0x67.toChar(), 0x68.toChar(),
            0x69.toChar(), 0x70.toChar(), 0x71.toChar(), 0x72.toChar(),
            0x73.toChar(), 0x74.toChar(), 0x75.toChar(), 0x76.toChar(),
        )
        val isBasicLatin = input.all { it.code < 0x80 }
        assertFalse(isBasicLatin)
    }

    @Test
    fun testSimdIsAsciiSuccess() {
        val ascii = byteArrayOf(
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
            0x69, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76,
        )
        val isAscii = ascii.all { it >= 0 }
        assertTrue(isAscii)
    }

    @Test
    fun testSimdIsAsciiFailure() {
        val input = byteArrayOf(
            0x61, 0x62, 0x63, 0x64, 0x81.toByte(), 0x66, 0x67, 0x68,
            0x69, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76,
        )
        val isAscii = input.all { it >= 0 }
        assertFalse(isAscii)
    }

    @Test
    fun testCheckAscii() {
        val input = byteArrayOf(
            0x61, 0x62, 0x63, 0x64, 0x81.toByte(), 0x66, 0x67, 0x68,
            0x69, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76,
        )
        val firstNonAscii = input.indexOfFirst { it < 0 }
        assertEquals(4, firstNonAscii)
    }

    @Test
    fun testAlu() {
        val input = byteArrayOf(
            0x61, 0x62, 0x63, 0x64, 0x81.toByte(), 0x66, 0x67, 0x68,
            0x69, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76,
        )
        val nonAsciiMask = input.take(8).mapIndexed { idx, b -> if (b < 0) (1 shl idx) else 0 }.sum()
        assertNotEquals(0, nonAsciiMask)
        assertEquals(16, nonAsciiMask)
    }
}
