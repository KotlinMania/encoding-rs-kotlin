// port-lint: tests euc_kr.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EucKrTest {
    private fun decodeEucKr(bytes: ByteArray, expect: String) {
        val (decoded, _, _) = Encoding.EUC_KR.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encodeEucKr(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.EUC_KR.encode(string)
        assertContentEquals(expect, encoded)
    }

    @Test
    fun testEucKrDecode() {
        // Empty
        decodeEucKr(byteArrayOf(), "")

        // ASCII
        decodeEucKr(byteArrayOf(0x61, 0x62), "\u0061\u0062")

        decodeEucKr(byteArrayOf(0x81.toByte(), 0x41), "\uAC02")
        decodeEucKr(byteArrayOf(0x81.toByte(), 0x5B), "\uFFFD\u005B")
        decodeEucKr(byteArrayOf(0xFD.toByte(), 0xFE.toByte()), "\u8A70")
        decodeEucKr(byteArrayOf(0xFE.toByte(), 0x41), "\uFFFD\u0041")
        decodeEucKr(byteArrayOf(0xFF.toByte(), 0x41), "\uFFFD\u0041")
        decodeEucKr(byteArrayOf(0x80.toByte(), 0x41), "\uFFFD\u0041")
        decodeEucKr(byteArrayOf(0xA1.toByte(), 0xFF.toByte()), "\uFFFD")
        decodeEucKr(byteArrayOf(0x81.toByte(), 0xFF.toByte()), "\uFFFD")
    }

    @Test
    fun testEucKrEncode() {
        // Empty
        encodeEucKr("", byteArrayOf())

        // ASCII
        encodeEucKr("\u0061\u0062", byteArrayOf(0x61, 0x62))

        encodeEucKr("\uAC02", byteArrayOf(0x81.toByte(), 0x41))
        encodeEucKr("\u8A70", byteArrayOf(0xFD.toByte(), 0xFE.toByte()))
    }

    @Test
    fun testEucKrEncodeFromTwoLowSurrogates() {
        val expectation = "&#65533;&#65533;".encodeToByteArray()
        val output = ByteArray(40)
        val encoder = Encoding.EUC_KR.newEncoder()
        val (result, read, written, hadErrors) =
            encoder.encodeFromUtf16(charArrayOf(0xDC00.toChar(), 0xDEDE.toChar()), output, true)
        assertEquals(CoderResult.InputEmpty, result)
        assertEquals(2, read)
        assertEquals(expectation.size, written)
        assertTrue(hadErrors)
        assertContentEquals(expectation, output.copyOfRange(0, written))
    }
}
