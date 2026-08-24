// port-lint: tests big5.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Big5Test {
    private fun decodeBig5(bytes: ByteArray, expect: String) {
        val (decoded, _, _) = Encoding.BIG5.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encodeBig5(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.BIG5.encode(string)
        assertContentEquals(expect, encoded)
    }

    @Test
    fun testBig5Decode() {
        // Empty
        decodeBig5(byteArrayOf(), "")

        // ASCII
        decodeBig5(byteArrayOf(0x61, 0x62), "\u0061\u0062")

        // Edge cases
        decodeBig5(byteArrayOf(0x87.toByte(), 0x40.toByte()), "\u43F0")
        decodeBig5(byteArrayOf(0xFE.toByte(), 0xFE.toByte()), "\u79D4")
        decodeBig5(byteArrayOf(0xFE.toByte(), 0xFD.toByte()), "\uD864\uDD0D") // U+2910D
        decodeBig5(byteArrayOf(0x88.toByte(), 0x62.toByte()), "\u00CA\u0304")
        decodeBig5(byteArrayOf(0x88.toByte(), 0x64.toByte()), "\u00CA\u030C")
        decodeBig5(byteArrayOf(0x88.toByte(), 0x66.toByte()), "\u00CA")
        decodeBig5(byteArrayOf(0x88.toByte(), 0xA3.toByte()), "\u00EA\u0304")
        decodeBig5(byteArrayOf(0x88.toByte(), 0xA5.toByte()), "\u00EA\u030C")
        decodeBig5(byteArrayOf(0x88.toByte(), 0xA7.toByte()), "\u00EA")
        decodeBig5(byteArrayOf(0x99.toByte(), 0xD4.toByte()), "\u8991")
        decodeBig5(byteArrayOf(0x99.toByte(), 0xD5.toByte()), "\uD85E\uDD67") // U+27967
        decodeBig5(byteArrayOf(0x99.toByte(), 0xD6.toByte()), "\u8A29")

        // Edge cases surrounded with ASCII
        decodeBig5(byteArrayOf(0x61, 0x87.toByte(), 0x40.toByte(), 0x62), "\u0061\u43F0\u0062")
        decodeBig5(byteArrayOf(0x61, 0xFE.toByte(), 0xFE.toByte(), 0x62), "\u0061\u79D4\u0062")
        decodeBig5(byteArrayOf(0x61, 0xFE.toByte(), 0xFD.toByte(), 0x62), "\u0061\uD864\uDD0D\u0062")
        decodeBig5(byteArrayOf(0x61, 0x88.toByte(), 0x62.toByte(), 0x62), "\u0061\u00CA\u0304\u0062")
        decodeBig5(byteArrayOf(0x61, 0x88.toByte(), 0x64.toByte(), 0x62), "\u0061\u00CA\u030C\u0062")
        decodeBig5(byteArrayOf(0x61, 0x88.toByte(), 0x66.toByte(), 0x62), "\u0061\u00CA\u0062")
        decodeBig5(byteArrayOf(0x61, 0x88.toByte(), 0xA3.toByte(), 0x62), "\u0061\u00EA\u0304\u0062")
        decodeBig5(byteArrayOf(0x61, 0x88.toByte(), 0xA5.toByte(), 0x62), "\u0061\u00EA\u030C\u0062")
        decodeBig5(byteArrayOf(0x61, 0x88.toByte(), 0xA7.toByte(), 0x62), "\u0061\u00EA\u0062")
        decodeBig5(byteArrayOf(0x61, 0x99.toByte(), 0xD4.toByte(), 0x62), "\u0061\u8991\u0062")
        decodeBig5(byteArrayOf(0x61, 0x99.toByte(), 0xD5.toByte(), 0x62), "\u0061\uD85E\uDD67\u0062")
        decodeBig5(byteArrayOf(0x61, 0x99.toByte(), 0xD6.toByte(), 0x62), "\u0061\u8A29\u0062")

        // Bad sequences
        decodeBig5(byteArrayOf(0x80.toByte(), 0x61), "\uFFFD\u0061")
        decodeBig5(byteArrayOf(0xFF.toByte(), 0x61), "\uFFFD\u0061")
        decodeBig5(byteArrayOf(0xFE.toByte(), 0x39), "\uFFFD\u0039")
        decodeBig5(byteArrayOf(0x87.toByte(), 0x66), "\uFFFD\u0066")
        decodeBig5(byteArrayOf(0x81.toByte(), 0x40), "\uFFFD\u0040")
        decodeBig5(byteArrayOf(0x61, 0x81.toByte()), "\u0061\uFFFD")
    }

    @Test
    fun testBig5Encode() {
        // Empty
        encodeBig5("", byteArrayOf())

        // ASCII
        encodeBig5("\u0061\u0062", byteArrayOf(0x61, 0x62))

        // Edge cases
        encodeBig5("\u9EA6\u0061", "&#40614;a".encodeToByteArray())
        encodeBig5("\uD858\uDE6B\u0061", "&#156267;a".encodeToByteArray()) // U+2626B
        encodeBig5("\u3000", byteArrayOf(0xA1.toByte(), 0x40.toByte()))
        encodeBig5("\u20AC", byteArrayOf(0xA3.toByte(), 0xE1.toByte()))
        encodeBig5("\u4E00", byteArrayOf(0xA4.toByte(), 0x40.toByte()))
        encodeBig5("\uD85D\uDE07", byteArrayOf(0xC8.toByte(), 0xA4.toByte())) // U+27607
        encodeBig5("\uFFE2", byteArrayOf(0xC8.toByte(), 0xCD.toByte()))
        encodeBig5("\u79D4", byteArrayOf(0xFE.toByte(), 0xFE.toByte()))

        // Not in index
        encodeBig5("\u2603\u0061", "&#9731;a".encodeToByteArray())

        // duplicate low bits
        encodeBig5("\uD840\uDFB5", byteArrayOf(0xFD.toByte(), 0x6A.toByte())) // U+203B5
        encodeBig5("\uD855\uDE05", byteArrayOf(0xFE.toByte(), 0x46.toByte())) // U+25605

        // prefer last
        encodeBig5("\u2550", byteArrayOf(0xF9.toByte(), 0xF9.toByte()))
    }

    @Test
    fun testBig5EncodeFromTwoLowSurrogates() {
        val expectation = "&#65533;&#65533;".encodeToByteArray()
        val output = ByteArray(40)
        val encoder = Encoding.BIG5.newEncoder()
        val (result, read, written, hadErrors) =
            encoder.encodeFromUtf16(charArrayOf(0xDC00.toChar(), 0xDEDE.toChar()), output, true)
        assertEquals(CoderResult.InputEmpty, result)
        assertEquals(2, read)
        assertEquals(expectation.size, written)
        assertTrue(hadErrors)
        assertContentEquals(expectation, output.copyOfRange(0, written))
    }
}
