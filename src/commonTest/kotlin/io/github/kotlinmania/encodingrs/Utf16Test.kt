// port-lint: tests utf_16.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Utf16Test {
    private fun decodeUtf16le(bytes: ByteArray, expect: String) {
        val decoder = Utf16Decoder.new(false)
        val dst = CharArray(expect.length + 10)
        val (res, _, written) = decoder.decodeToUtf16Raw(bytes, dst, last = true)
        val out = dst.concatToString(0, written)
        assertEquals(expect, out)
    }

    private fun decodeUtf16be(bytes: ByteArray, expect: String) {
        val decoder = Utf16Decoder.new(true)
        val dst = CharArray(expect.length + 10)
        val (res, _, written) = decoder.decodeToUtf16Raw(bytes, dst, last = true)
        val out = dst.concatToString(0, written)
        assertEquals(expect, out)
    }

    private fun encodeUtf16le(string: String, expect: ByteArray) {
        val bytes = string.encodeToByteArray()
        assertEquals(expect.size, bytes.size)
    }

    private fun encodeUtf16be(string: String, expect: ByteArray) {
        val bytes = string.encodeToByteArray()
        assertEquals(expect.size, bytes.size)
    }

    @Test
    fun testUtf16Decode() {
        decodeUtf16le(byteArrayOf(), "")
        decodeUtf16be(byteArrayOf(), "")

        decodeUtf16le(byteArrayOf(0x61, 0x00, 0x62, 0x00), "\u0061\u0062")
        decodeUtf16be(byteArrayOf(0x00, 0x61, 0x00, 0x62), "\u0061\u0062")
    }

    @Test
    fun testUtf16Encode() {
        encodeUtf16be("", byteArrayOf())
        encodeUtf16le("", byteArrayOf())
    }

    @Test
    fun testUtf16beDecodeOneByOne() {
        val input = byteArrayOf(0x00, 0x61, 0x00, 0xE4.toByte(), 0x26, 0x03, 0xD8.toByte(), 0x3D, 0xDC.toByte(), 0xA9.toByte())
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(true)
        for (i in input.indices) {
            val b = byteArrayOf(input[i])
            val (result, read, _) = decoder.decodeToUtf16Raw(b, output, false)
            assertEquals(DecoderResult.InputEmpty, result)
            assertEquals(1, read)
        }
    }

    @Test
    fun testUtf16leDecodeOneByOne() {
        val input = byteArrayOf(0x61, 0x00, 0xE4.toByte(), 0x00, 0x03, 0x26, 0x3D, 0xD8.toByte(), 0xA9.toByte(), 0xDC.toByte())
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(false)
        for (i in input.indices) {
            val b = byteArrayOf(input[i])
            val (result, read, _) = decoder.decodeToUtf16Raw(b, output, false)
            assertEquals(DecoderResult.InputEmpty, result)
            assertEquals(1, read)
        }
    }

    @Test
    fun testUtf16beDecodeThreeAtATime() {
        val input = byteArrayOf(0x00, 0xE4.toByte(), 0x26, 0x03, 0xD8.toByte(), 0x3D, 0xDC.toByte(), 0xA9.toByte(), 0x00, 0x61, 0x00, 0xE4.toByte())
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(true)
        var i = 0
        while (i < input.size) {
            val chunkLen = minOf(3, input.size - i)
            val chunk = input.copyOfRange(i, i + chunkLen)
            val (result, read, _) = decoder.decodeToUtf16Raw(chunk, output, false)
            assertEquals(DecoderResult.InputEmpty, result)
            assertEquals(chunkLen, read)
            i += chunkLen
        }
    }

    @Test
    fun testUtf16leDecodeThreeAtATime() {
        val input = byteArrayOf(0xE4.toByte(), 0x00, 0x03, 0x26, 0x3D, 0xD8.toByte(), 0xA9.toByte(), 0xDC.toByte(), 0x61, 0x00, 0xE4.toByte(), 0x00)
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(false)
        var i = 0
        while (i < input.size) {
            val chunkLen = minOf(3, input.size - i)
            val chunk = input.copyOfRange(i, i + chunkLen)
            val (result, read, _) = decoder.decodeToUtf16Raw(chunk, output, false)
            assertEquals(DecoderResult.InputEmpty, result)
            assertEquals(chunkLen, read)
            i += chunkLen
        }
    }

    @Test
    fun testUtf16leDecodeBomPrefixedSplitBytePair() {
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(false)
        val (res1, read1, written1) = decoder.decodeToUtf16Raw(byteArrayOf(0xFF.toByte()), output, false)
        assertEquals(DecoderResult.InputEmpty, res1)
        assertEquals(1, read1)
        assertEquals(0, written1)

        val (res2, read2, written2) = decoder.decodeToUtf16Raw(byteArrayOf(0xFD.toByte()), output, true)
        assertEquals(DecoderResult.InputEmpty, res2)
        assertEquals(1, read2)
        assertEquals(1, written2)
        assertEquals(0xFDFF.toChar(), output[0])
    }

    @Test
    fun testUtf16beDecodeBomPrefixedSplitBytePair() {
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(true)
        val (res1, read1, written1) = decoder.decodeToUtf16Raw(byteArrayOf(0xFE.toByte()), output, false)
        assertEquals(DecoderResult.InputEmpty, res1)
        assertEquals(1, read1)
        assertEquals(0, written1)

        val (res2, read2, written2) = decoder.decodeToUtf16Raw(byteArrayOf(0xFD.toByte()), output, true)
        assertEquals(DecoderResult.InputEmpty, res2)
        assertEquals(1, read2)
        assertEquals(1, written2)
        assertEquals(0xFEFD.toChar(), output[0])
    }

    @Test
    fun testUtf16leDecodeBomPrefix() {
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(false)
        val (res, read, written) = decoder.decodeToUtf16Raw(byteArrayOf(0xFF.toByte()), output, true)
        assertEquals(DecoderResult.Malformed(1u, 0u), res)
        assertEquals(1, read)
        assertEquals(0, written)
    }

    @Test
    fun testUtf16beDecodeBomPrefix() {
        val output = CharArray(20)
        val decoder = Utf16Decoder.new(true)
        val (res, read, written) = decoder.decodeToUtf16Raw(byteArrayOf(0xFE.toByte()), output, true)
        assertEquals(DecoderResult.Malformed(1u, 0u), res)
        assertEquals(1, read)
        assertEquals(0, written)
    }

    @Test
    fun testUtf16leDecodeNearEnd() {
        val output = ByteArray(4)
        val decoder = Utf16Decoder.new(false)
        val (res1, read1, written1) = decoder.decodeToUtf8Raw(byteArrayOf(0x03), output, false)
        assertEquals(DecoderResult.InputEmpty, res1)
        assertEquals(1, read1)
        assertEquals(0, written1)

        val (res2, read2, written2) = decoder.decodeToUtf8Raw(byteArrayOf(0x26, 0x03, 0x26), output, false)
        assertEquals(DecoderResult.OutputFull, res2)
        assertEquals(1, read2)
        assertEquals(3, written2)
        assertEquals(0xE2.toByte(), output[0])
        assertEquals(0x98.toByte(), output[1])
        assertEquals(0x83.toByte(), output[2])
    }

    @Test
    fun testUtf16beDecodeNearEnd() {
        val output = ByteArray(4)
        val decoder = Utf16Decoder.new(true)
        val (res1, read1, written1) = decoder.decodeToUtf8Raw(byteArrayOf(0x26), output, false)
        assertEquals(DecoderResult.InputEmpty, res1)
        assertEquals(1, read1)
        assertEquals(0, written1)

        val (res2, read2, written2) = decoder.decodeToUtf8Raw(byteArrayOf(0x03, 0x26, 0x03), output, false)
        assertEquals(DecoderResult.OutputFull, res2)
        assertEquals(1, read2)
        assertEquals(3, written2)
        assertEquals(0xE2.toByte(), output[0])
        assertEquals(0x98.toByte(), output[1])
        assertEquals(0x83.toByte(), output[2])
    }
}
