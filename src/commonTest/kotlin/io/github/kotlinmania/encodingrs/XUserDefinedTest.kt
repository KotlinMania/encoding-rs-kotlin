// port-lint: tests x_user_defined.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals

class XUserDefinedTest {
    private fun decodeXUserDefined(bytes: ByteArray, expect: String) {
        val decoder = UserDefinedDecoder.new()
        val dst = CharArray(bytes.size)
        val (res, read, written) = decoder.decodeToUtf16Raw(bytes, dst)
        assertEquals(DecoderResult.InputEmpty, res)
        assertEquals(bytes.size, read)
        assertEquals(bytes.size, written)
        assertEquals(expect, dst.concatToString())
    }

    private fun encodeXUserDefined(string: String, expect: ByteArray) {
        val encoder = UserDefinedEncoder.new()
        val src = string.toCharArray()
        val dst = ByteArray(src.size)
        val (res, read, written) = encoder.encodeFromUtf16Raw(src, dst)
        assertEquals(EncoderResult.InputEmpty, res)
        assertEquals(src.size, read)
        assertEquals(expect.size, written)
        for (i in expect.indices) {
            assertEquals(expect[i], dst[i])
        }
    }

    @Test
    fun testXUserDefinedDecode() {
        // Empty
        decodeXUserDefined(byteArrayOf(), "")

        // ASCII
        decodeXUserDefined(byteArrayOf(0x61, 0x62), "\u0061\u0062")

        decodeXUserDefined(byteArrayOf(0x80.toByte(), 0xFF.toByte()), "\uF780\uF7FF")
        decodeXUserDefined(
            byteArrayOf(
                0x80.toByte(),
                0xFF.toByte(),
                0x61,
                0x62,
                0x80.toByte(),
                0xFF.toByte(),
                0x61,
                0x62,
                0x80.toByte(),
                0xFF.toByte(),
                0x61,
                0x62,
                0x80.toByte(),
                0xFF.toByte(),
                0x61,
                0x62,
                0x80.toByte(),
                0xFF.toByte(),
                0x61,
                0x62,
            ),
            "\uF780\uF7FF\u0061\u0062\uF780\uF7FF\u0061\u0062\uF780\uF7FF\u0061\u0062\uF780\uF7FF\u0061\u0062\uF780\uF7FF\u0061\u0062",
        )
    }

    @Test
    fun testXUserDefinedEncode() {
        // Empty
        encodeXUserDefined("", byteArrayOf())

        // ASCII
        encodeXUserDefined("\u0061\u0062", byteArrayOf(0x61, 0x62))

        encodeXUserDefined("\uF780\uF7FF", byteArrayOf(0x80.toByte(), 0xFF.toByte()))
    }

    @Test
    fun testXUserDefinedFromTwoLowSurrogates() {
        val encoder = UserDefinedEncoder.new()
        val src = charArrayOf(0xDC00.toChar(), 0xDEDE.toChar())
        val dst = ByteArray(40)
        val (res, read, written) = encoder.encodeFromUtf16Raw(src, dst)
        assertEquals(EncoderResult.Unmappable(0xDC00.toChar()), res)
        assertEquals(0, read)
        assertEquals(0, written)
    }
}
