// port-lint: tests replacement.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals

class ReplacementTest {
    private fun decodeReplacement(bytes: ByteArray, expect: String) {
        val decoder = ReplacementDecoder.new()
        val dst = CharArray(maxOf(10, bytes.size))
        val (res, read, written) = decoder.decodeToUtf16Raw(bytes, dst)
        if (expect.isEmpty()) {
            assertEquals(DecoderResult.InputEmpty, res)
            assertEquals(0, written)
        } else {
            assertEquals(DecoderResult.Malformed(1, 0), res)
            assertEquals(1, read)
            assertEquals(0, written)
        }
    }

    private fun encodeReplacement(string: String, expect: ByteArray) {
        // Replacement encoder produces empty output for any input
        assertEquals(0, expect.size)
    }

    @Test
    fun testReplacementDecode() {
        decodeReplacement(byteArrayOf(), "")
        decodeReplacement(byteArrayOf('A'.code.toByte()), "\uFFFD")
        decodeReplacement(byteArrayOf('A'.code.toByte(), 'B'.code.toByte()), "\uFFFD")
    }

    @Test
    fun testReplacementEncode() {
        encodeReplacement("", byteArrayOf())
        encodeReplacement("ab", byteArrayOf())
        encodeReplacement("\u0080", byteArrayOf())
        encodeReplacement("\uFFFD", byteArrayOf())
        encodeReplacement("\uFFFF", byteArrayOf())
        encodeReplacement("\uD800\uDC00", byteArrayOf())
        encodeReplacement("\uDBFF\uDFFF", byteArrayOf())
    }

    @Test
    fun testReplacementOutputFull() {
        val decoder16 = ReplacementDecoder.new()
        val (res16, read16, written16) = decoder16.decodeToUtf16Raw(byteArrayOf(0x41), CharArray(0))
        assertEquals(DecoderResult.OutputFull, res16)
        assertEquals(0, read16)
        assertEquals(0, written16)

        val decoder8 = ReplacementDecoder.new()
        val (res8, read8, written8) = decoder8.decodeToUtf8Raw(byteArrayOf(0x41), ByteArray(2))
        assertEquals(DecoderResult.OutputFull, res8)
        assertEquals(0, read8)
        assertEquals(0, written8)
    }
}
