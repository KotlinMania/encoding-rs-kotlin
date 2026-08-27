// port-lint: tests encoding_rs/src/ascii.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals

class AsciiTest {
    @Test
    fun testAsciiToAscii() {
        for (i in 0 until 32) {
            val src = ByteArray(32)
            val dst = ByteArray(32)
            for (j in 0 until 32) {
                val c = if (i == j) 0xAA.toByte() else (j + 0x40).toByte()
                src[j] = c
            }
            val result = Ascii.asciiToAscii(src, dst, 32)
            assertEquals(0xAA.toByte(), result?.first)
            assertEquals(i, result?.second)
            for (j in 0 until i) {
                assertEquals((j + 0x40).toByte(), dst[j])
            }
        }
    }

    @Test
    fun testAsciiToBasicLatin() {
        for (i in 0 until 32) {
            val src = ByteArray(32)
            val dst = CharArray(32)
            for (j in 0 until 32) {
                val c = if (i == j) 0xAA.toByte() else (j + 0x40).toByte()
                src[j] = c
            }
            val result = Ascii.asciiToBasicLatin(src, dst, 32)
            assertEquals(0xAA.toByte(), result?.first)
            assertEquals(i, result?.second)
            for (j in 0 until i) {
                assertEquals((j + 0x40).toChar(), dst[j])
            }
        }
    }

    @Test
    fun testBasicLatinToAscii() {
        for (i in 0 until 32) {
            val src = CharArray(32)
            val dst = ByteArray(32)
            for (j in 0 until 32) {
                val c = if (i == j) 0xAA.toChar() else (j + 0x40).toChar()
                src[j] = c
            }
            val result = Ascii.basicLatinToAscii(src, dst, 32)
            assertEquals(0xAA.toChar(), result?.first)
            assertEquals(i, result?.second)
            for (j in 0 until i) {
                assertEquals((j + 0x40).toByte(), dst[j])
            }
        }
    }

    @Test
    fun testAsciiValidUpTo() {
        assertEquals(3, Ascii.asciiValidUpTo(byteArrayOf(0x61, 0x62, 0x63)))
        assertEquals(2, Ascii.asciiValidUpTo(byteArrayOf(0x61, 0x62, 0x80.toByte(), 0x63)))
        assertEquals(0, Ascii.asciiValidUpTo(byteArrayOf(0xFF.toByte())))
    }

    @Test
    fun testIso2022JpAsciiValidUpTo() {
        assertEquals(3, Ascii.iso2022JpAsciiValidUpTo(byteArrayOf(0x61, 0x62, 0x63)))
        assertEquals(2, Ascii.iso2022JpAsciiValidUpTo(byteArrayOf(0x61, 0x62, 0x1B, 0x63)))
        assertEquals(1, Ascii.iso2022JpAsciiValidUpTo(byteArrayOf(0x61, 0x0E, 0x63)))
        assertEquals(1, Ascii.iso2022JpAsciiValidUpTo(byteArrayOf(0x61, 0x0F, 0x63)))
    }
}
