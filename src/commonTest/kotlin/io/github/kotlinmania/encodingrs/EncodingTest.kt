// port-lint: tests encoding_rs/src/lib.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EncodingTest {
    private fun sniffToUtf16(
        initialEncoding: Encoding,
        expectedEncoding: Encoding,
        bytes: ByteArray,
        expect: CharArray,
        breaks: IntArray,
    ) {
        val decoder = initialEncoding.newDecoder()
        val capacity = decoder.maxUtf16BufferLength(bytes.size) ?: bytes.size
        val dest = CharArray(capacity)

        var totalWritten = 0
        var start = 0
        for (br in breaks) {
            val slice = bytes.copyOfRange(start, br)
            val subDest = CharArray(dest.size - totalWritten)
            val res = decoder.decodeToUtf16(slice, subDest, false)
            subDest.copyInto(dest, destinationOffset = totalWritten, startIndex = 0, endIndex = res.written)
            totalWritten += res.written
            assertEquals(br - start, res.read)
            assertEquals(CoderResult.InputEmpty, res.result)
            start = br
        }
        val slice = bytes.copyOfRange(start, bytes.size)
        val subDest = CharArray(dest.size - totalWritten)
        val res = decoder.decodeToUtf16(slice, subDest, true)
        subDest.copyInto(dest, destinationOffset = totalWritten, startIndex = 0, endIndex = res.written)
        totalWritten += res.written
        assertEquals(CoderResult.InputEmpty, res.result)
        assertEquals(bytes.size - start, res.read)
        assertEquals(expect.size, totalWritten)
        val actualChars = dest.copyOf(totalWritten)
        assertTrue(expect.contentEquals(actualChars))
        assertEquals(expectedEncoding, decoder.encoding())
    }

    @Test
    fun testBomSniffing() {
        // ASCII
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(),
        )
        // UTF-8
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(2),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(3),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(4),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(2, 3),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(1, 2),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(1, 3),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62),
            charArrayOf('a', 'b'),
            intArrayOf(1, 2, 3, 4),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()),
            charArrayOf(),
            intArrayOf(),
        )
        // Not UTF-8
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0x61, 0x62),
            charArrayOf(0x00EF.toChar(), 0x00BB.toChar(), 'a', 'b'),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0x61, 0x62),
            charArrayOf(0x00EF.toChar(), 0x00BB.toChar(), 'a', 'b'),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte(), 0x61, 0x62),
            charArrayOf(0x00EF.toChar(), 'a', 'b'),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte(), 0x61, 0x62),
            charArrayOf(0x00EF.toChar(), 'a', 'b'),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte()),
            charArrayOf(0x00EF.toChar(), 0x00BB.toChar()),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte()),
            charArrayOf(0x00EF.toChar(), 0x00BB.toChar()),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xEF.toByte()),
            charArrayOf(0x00EF.toChar()),
            intArrayOf(),
        )
        // Not UTF-16
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xFE.toByte(), 0x61, 0x62),
            charArrayOf(0x00FE.toChar(), 'a', 'b'),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xFE.toByte(), 0x61, 0x62),
            charArrayOf(0x00FE.toChar(), 'a', 'b'),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xFE.toByte()),
            charArrayOf(0x00FE.toChar()),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xFF.toByte(), 0x61, 0x62),
            charArrayOf(0x00FF.toChar(), 'a', 'b'),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xFF.toByte(), 0x61, 0x62),
            charArrayOf(0x00FF.toChar(), 'a', 'b'),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            WINDOWS_1252,
            byteArrayOf(0xFF.toByte()),
            charArrayOf(0x00FF.toChar()),
            intArrayOf(),
        )
        // UTF-16
        sniffToUtf16(
            WINDOWS_1252,
            UTF_16BE,
            byteArrayOf(0xFE.toByte(), 0xFF.toByte()),
            charArrayOf(),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_16BE,
            byteArrayOf(0xFE.toByte(), 0xFF.toByte()),
            charArrayOf(),
            intArrayOf(1),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_16LE,
            byteArrayOf(0xFF.toByte(), 0xFE.toByte()),
            charArrayOf(),
            intArrayOf(),
        )
        sniffToUtf16(
            WINDOWS_1252,
            UTF_16LE,
            byteArrayOf(0xFF.toByte(), 0xFE.toByte()),
            charArrayOf(),
            intArrayOf(1),
        )
    }

    @Test
    fun testOutputEncoding() {
        assertEquals(UTF_8, REPLACEMENT.outputEncoding())
        assertEquals(UTF_8, UTF_16BE.outputEncoding())
        assertEquals(UTF_8, UTF_16LE.outputEncoding())
        assertEquals(UTF_8, UTF_8.outputEncoding())
        assertEquals(WINDOWS_1252, WINDOWS_1252.outputEncoding())
        assertEquals(UTF_8, REPLACEMENT.newEncoder().encoding())
        assertEquals(UTF_8, UTF_16BE.newEncoder().encoding())
        assertEquals(UTF_8, UTF_16LE.newEncoder().encoding())
        assertEquals(UTF_8, UTF_8.newEncoder().encoding())
        assertEquals(WINDOWS_1252, WINDOWS_1252.newEncoder().encoding())
    }

    @Test
    fun testLabelResolution() {
        assertEquals(UTF_8, Encoding.forLabel("utf-8".encodeToByteArray()))
        assertEquals(UTF_8, Encoding.forLabel("UTF-8".encodeToByteArray()))
        assertEquals(
            UTF_8,
            Encoding.forLabel(" \t \n \u000C \n utf-8 \r \n \t \u000C ".encodeToByteArray()),
        )
        assertNull(Encoding.forLabel("utf-8 _".encodeToByteArray()))
        assertNull(Encoding.forLabel("bogus".encodeToByteArray()))
        assertNull(Encoding.forLabel("bogusbogusbogusbogus".encodeToByteArray()))
    }

    @Test
    fun testDecodeValidWindows1257ToCow() {
        val (str, encoding, hadErrors) = WINDOWS_1257.decode(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xE4.toByte()))
        assertEquals("abc\u20AC\u00E4", str)
        assertEquals(WINDOWS_1257, encoding)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeInvalidWindows1257ToCow() {
        val (str, encoding, hadErrors) = WINDOWS_1257.decode(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xA1.toByte(), 0xE4.toByte()))
        assertEquals("abc\u20AC\uFFFD\u00E4", str)
        assertEquals(WINDOWS_1257, encoding)
        assertTrue(hadErrors)
    }

    @Test
    fun testDecodeAsciiOnlyWindows1257ToCow() {
        val (str, encoding, hadErrors) = WINDOWS_1257.decode(byteArrayOf(0x61, 0x62, 0x63))
        assertEquals("abc", str)
        assertEquals(WINDOWS_1257, encoding)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulValidUtf8AsWindows1257ToCow() {
        val (str, encoding, hadErrors) =
            WINDOWS_1257.decode(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\u20AC\u00E4", str)
        assertEquals(UTF_8, encoding)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulInvalidUtf8AsWindows1257ToCow() {
        val (str, encoding, hadErrors) =
            WINDOWS_1257.decode(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0x80.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\u20AC\uFFFD\u00E4", str)
        assertEquals(UTF_8, encoding)
        assertTrue(hadErrors)
    }

    @Test
    fun testDecodeBomfulValidUtf8AsUtf8ToCow() {
        val (str, encoding, hadErrors) =
            UTF_8.decode(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\u20AC\u00E4", str)
        assertEquals(UTF_8, encoding)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulInvalidUtf8AsUtf8ToCow() {
        val (str, encoding, hadErrors) =
            UTF_8.decode(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0x80.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\u20AC\uFFFD\u00E4", str)
        assertEquals(UTF_8, encoding)
        assertTrue(hadErrors)
    }

    @Test
    fun testDecodeBomfulValidUtf8AsUtf8ToCowWithBomRemoval() {
        val (str, hadErrors) =
            UTF_8.decodeWithBomRemoval(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\u20AC\u00E4", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulValidUtf8AsWindows1257ToCowWithBomRemoval() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithBomRemoval(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\u013C\u00BB\u00E6\u0101\u201A\u00AC\u0106\u00A4", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeValidWindows1257ToCowWithBomRemoval() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithBomRemoval(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xE4.toByte()))
        assertEquals("abc\u20AC\u00E4", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeInvalidWindows1257ToCowWithBomRemoval() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithBomRemoval(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xA1.toByte(), 0xE4.toByte()))
        assertEquals("abc\u20AC\uFFFD\u00E4", str)
        assertTrue(hadErrors)
    }

    @Test
    fun testDecodeAsciiOnlyWindows1257ToCowWithBomRemoval() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithBomRemoval(byteArrayOf(0x61, 0x62, 0x63))
        assertEquals("abc", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulValidUtf8ToCowWithoutBomHandling() {
        val (str, hadErrors) =
            UTF_8.decodeWithoutBomHandling(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\uFEFF\u20AC\u00E4", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulInvalidUtf8ToCowWithoutBomHandling() {
        val (str, hadErrors) =
            UTF_8.decodeWithoutBomHandling(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0x80.toByte(), 0xC3.toByte(), 0xA4.toByte()))
        assertEquals("\uFEFF\u20AC\uFFFD\u00E4", str)
        assertTrue(hadErrors)
    }

    @Test
    fun testDecodeValidWindows1257ToCowWithoutBomHandling() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithoutBomHandling(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xE4.toByte()))
        assertEquals("abc\u20AC\u00E4", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeInvalidWindows1257ToCowWithoutBomHandling() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithoutBomHandling(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xA1.toByte(), 0xE4.toByte()))
        assertEquals("abc\u20AC\uFFFD\u00E4", str)
        assertTrue(hadErrors)
    }

    @Test
    fun testDecodeAsciiOnlyWindows1257ToCowWithoutBomHandling() {
        val (str, hadErrors) =
            WINDOWS_1257.decodeWithoutBomHandling(byteArrayOf(0x61, 0x62, 0x63))
        assertEquals("abc", str)
        assertFalse(hadErrors)
    }

    @Test
    fun testDecodeBomfulValidUtf8ToCowWithoutBomHandlingAndWithoutReplacement() {
        val res =
            UTF_8.decodeWithoutBomHandlingAndWithoutReplacement(
                byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0xC3.toByte(), 0xA4.toByte()),
            )
        assertEquals("\uFEFF\u20AC\u00E4", res)
    }

    @Test
    fun testDecodeBomfulInvalidUtf8ToCowWithoutBomHandlingAndWithoutReplacement() {
        val res =
            UTF_8.decodeWithoutBomHandlingAndWithoutReplacement(
                byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0xE2.toByte(), 0x82.toByte(), 0xAC.toByte(), 0x80.toByte(), 0xC3.toByte(), 0xA4.toByte()),
            )
        assertNull(res)
    }

    @Test
    fun testDecodeValidWindows1257ToCowWithoutBomHandlingAndWithoutReplacement() {
        val res =
            WINDOWS_1257.decodeWithoutBomHandlingAndWithoutReplacement(
                byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xE4.toByte()),
            )
        assertEquals("abc\u20AC\u00E4", res)
    }

    @Test
    fun testDecodeInvalidWindows1257ToCowWithoutBomHandlingAndWithoutReplacement() {
        val res =
            WINDOWS_1257.decodeWithoutBomHandlingAndWithoutReplacement(
                byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xA1.toByte(), 0xE4.toByte()),
            )
        assertNull(res)
    }

    @Test
    fun testDecodeAsciiOnlyWindows1257ToCowWithoutBomHandlingAndWithoutReplacement() {
        val res =
            WINDOWS_1257.decodeWithoutBomHandlingAndWithoutReplacement(
                byteArrayOf(0x61, 0x62, 0x63),
            )
        assertEquals("abc", res)
    }

    @Test
    fun testEncodeAsciiOnlyWindows1257ToCow() {
        val (bytes, encoding, hadErrors) = WINDOWS_1257.encode("abc")
        assertTrue(byteArrayOf(0x61, 0x62, 0x63).contentEquals(bytes))
        assertEquals(WINDOWS_1257, encoding)
        assertFalse(hadErrors)
    }

    @Test
    fun testEncodeValidWindows1257ToCow() {
        val (bytes, encoding, hadErrors) = WINDOWS_1257.encode("abc\u20AC\u00E4")
        assertTrue(byteArrayOf(0x61, 0x62, 0x63, 0x80.toByte(), 0xE4.toByte()).contentEquals(bytes))
        assertEquals(WINDOWS_1257, encoding)
        assertFalse(hadErrors)
    }

    @Test
    fun testUtf16SpaceWithOneBomByte() {
        val decoder = UTF_16LE.newDecoder()
        val dst = CharArray(12)
        val needed = decoder.maxUtf16BufferLength(1)!!
        val sub1 = CharArray(needed)
        val res1 = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte()), sub1, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte()), sub1, true)
        assertEquals(CoderResult.InputEmpty, res2.result)
    }

    @Test
    fun testUtf8SpaceWithOneBomByte() {
        val decoder = UTF_8.newDecoder()
        val needed = decoder.maxUtf16BufferLength(1)!!
        val sub1 = CharArray(needed)
        val res1 = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte()), sub1, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte()), sub1, true)
        assertEquals(CoderResult.InputEmpty, res2.result)
    }

    @Test
    fun testUtf16SpaceWithTwoBomBytes() {
        val decoder = UTF_16LE.newDecoder()
        val needed = decoder.maxUtf16BufferLength(1)!!
        val sub = CharArray(needed)
        val res1 = decoder.decodeToUtf16(byteArrayOf(0xEF.toByte()), sub, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = decoder.decodeToUtf16(byteArrayOf(0xBB.toByte()), sub, false)
        assertEquals(CoderResult.InputEmpty, res2.result)

        val res3 = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte()), sub, true)
        assertEquals(CoderResult.InputEmpty, res3.result)
    }

    @Test
    fun testUtf8SpaceWithTwoBomBytes() {
        val decoder = UTF_8.newDecoder()
        val needed = decoder.maxUtf16BufferLength(1)!!
        val sub = CharArray(needed)
        val res1 = decoder.decodeToUtf16(byteArrayOf(0xEF.toByte()), sub, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = decoder.decodeToUtf16(byteArrayOf(0xBB.toByte()), sub, false)
        assertEquals(CoderResult.InputEmpty, res2.result)

        val res3 = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte()), sub, true)
        assertEquals(CoderResult.InputEmpty, res3.result)
    }

    @Test
    fun testUtf16SpaceWithOneBomByteAndASecondByteInSameCall() {
        val decoder = UTF_16LE.newDecoder()
        val needed = decoder.maxUtf16BufferLength(2)!!
        val dst = CharArray(needed)
        val res = decoder.decodeToUtf16(byteArrayOf(0xFF.toByte(), 0xFF.toByte()), dst, true)
        assertEquals(CoderResult.InputEmpty, res.result)
    }

    @Test
    fun testTooShortBufferWithIso2022JpAsciiFromUtf8() {
        val dst = ByteArray(8)
        val encoder = ISO_2022_JP.newEncoder()
        val res1 = encoder.encodeFromUtf8("", dst, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = encoder.encodeFromUtf8("", dst, true)
        assertEquals(CoderResult.InputEmpty, res2.result)
    }

    @Test
    fun testTooShortBufferWithIso2022JpRomanFromUtf8() {
        val dst = ByteArray(16)
        val encoder = ISO_2022_JP.newEncoder()
        val res1 = encoder.encodeFromUtf8("\u00A5", dst, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = encoder.encodeFromUtf8("", dst.copyOfRange(0, 8), false)
        assertEquals(CoderResult.InputEmpty, res2.result)
    }

    @Test
    fun testBufferEndIso2022JpFromUtf8() {
        val dst = ByteArray(18)
        val encoder1 = ISO_2022_JP.newEncoder()
        val res1 = encoder1.encodeFromUtf8("\u00A5\uD83D\uDCA9", dst, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val encoder3 = ISO_2022_JP.newEncoder()
        val res3 = encoder3.encodeFromUtf8("\uD83D\uDCA9", dst.copyOfRange(0, 13), false)
        assertEquals(CoderResult.InputEmpty, res3.result)
    }

    @Test
    fun testTooShortBufferWithIso2022JpAsciiFromUtf16() {
        val dst = ByteArray(8)
        val encoder = ISO_2022_JP.newEncoder()
        val res1 = encoder.encodeFromUtf16(charArrayOf(), dst, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = encoder.encodeFromUtf16(charArrayOf(), dst, true)
        assertEquals(CoderResult.InputEmpty, res2.result)
    }

    @Test
    fun testTooShortBufferWithIso2022JpRomanFromUtf16() {
        val dst = ByteArray(16)
        val encoder = ISO_2022_JP.newEncoder()
        val res1 = encoder.encodeFromUtf16(charArrayOf(0x00A5.toChar()), dst, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val res2 = encoder.encodeFromUtf16(charArrayOf(), dst.copyOfRange(0, 8), false)
        assertEquals(CoderResult.InputEmpty, res2.result)
    }

    @Test
    fun testBufferEndIso2022JpFromUtf16() {
        val dst = ByteArray(18)
        val encoder1 = ISO_2022_JP.newEncoder()
        val res1 = encoder1.encodeFromUtf16(charArrayOf(0x00A5.toChar(), 0xD83D.toChar(), 0xDCA9.toChar()), dst, false)
        assertEquals(CoderResult.InputEmpty, res1.result)

        val encoder3 = ISO_2022_JP.newEncoder()
        val res3 = encoder3.encodeFromUtf16(charArrayOf(0xD83D.toChar(), 0xDCA9.toChar()), dst.copyOfRange(0, 13), false)
        assertEquals(CoderResult.InputEmpty, res3.result)
    }

    @Test
    fun testBufferEndUtf16be() {
        val decoder = UTF_16BE.newDecoderWithoutBomHandling()
        val dest = ByteArray(4)
        val res = decoder.decodeToUtf8WithoutReplacement(byteArrayOf(0xD8.toByte(), 0x00), dest, false)
        assertEquals(DecoderResult.InputEmpty, res.first)
        assertEquals(2, res.second)
    }

    @Test
    fun testHash() {
        val encodings = mutableSetOf<Encoding>()
        encodings.add(UTF_8)
        encodings.add(ISO_2022_JP)
        assertTrue(encodings.contains(UTF_8))
        assertTrue(encodings.contains(ISO_2022_JP))
        assertFalse(encodings.contains(WINDOWS_1252))
        encodings.remove(ISO_2022_JP)
        assertFalse(encodings.contains(ISO_2022_JP))
    }

    @Test
    fun testIso2022JpNcrExtraFromUtf16() {
        val dst = ByteArray(10)
        val encoder = ISO_2022_JP.newEncoder()
        val (result, _, _, _) =
            encoder.encodeFromUtf16(charArrayOf(0x3041.toChar(), 0xFFFF.toChar()), dst, true)
        assertEquals(CoderResult.OutputFull, result)
    }

    @Test
    fun testIso2022JpNcrExtraFromUtf8() {
        val dst = ByteArray(10)
        val encoder = ISO_2022_JP.newEncoder()
        val (result, _, _, _) =
            encoder.encodeFromUtf8("\u3041\uFFFF", dst, true)
        assertEquals(CoderResult.OutputFull, result)
    }

    @Test
    fun testSerde() {
        // Serde roundtrip parity: verify encoding name resolution and label roundtrip
        val enc = UTF_8
        val name = enc.name
        val resolved = Encoding.forLabel(name)
        assertEquals(enc, resolved)
    }

    @Test
    fun testMaxLengthWithBomToUtf8() {
        val output = ByteArray(20)
        val decoder = REPLACEMENT.newDecoder()
        val input = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 'A'.code.toByte())
        val needed = decoder.maxUtf8BufferLengthWithoutReplacement(input.size)!!
        val subOutput = ByteArray(needed)
        val (result, read, written) = decoder.decodeToUtf8WithoutReplacement(input, subOutput, true)
        assertEquals(DecoderResult.InputEmpty, result)
        assertEquals(input.size, read)
        assertEquals(1, written)
        assertEquals('A'.code.toByte(), subOutput[0])
    }

    @Test
    fun testIsSingleByte() {
        assertFalse(BIG5.isSingleByte())
        assertFalse(EUC_JP.isSingleByte())
        assertFalse(EUC_KR.isSingleByte())
        assertFalse(GB18030.isSingleByte())
        assertFalse(GBK.isSingleByte())
        assertFalse(REPLACEMENT.isSingleByte())
        assertFalse(SHIFT_JIS.isSingleByte())
        assertFalse(UTF_8.isSingleByte())
        assertFalse(UTF_16BE.isSingleByte())
        assertFalse(UTF_16LE.isSingleByte())
        assertFalse(ISO_2022_JP.isSingleByte())

        assertTrue(IBM866.isSingleByte())
        assertTrue(ISO_8859_2.isSingleByte())
        assertTrue(ISO_8859_3.isSingleByte())
        assertTrue(ISO_8859_4.isSingleByte())
        assertTrue(ISO_8859_5.isSingleByte())
        assertTrue(ISO_8859_6.isSingleByte())
        assertTrue(ISO_8859_7.isSingleByte())
        assertTrue(ISO_8859_8.isSingleByte())
        assertTrue(ISO_8859_10.isSingleByte())
        assertTrue(ISO_8859_13.isSingleByte())
        assertTrue(ISO_8859_14.isSingleByte())
        assertTrue(ISO_8859_15.isSingleByte())
        assertTrue(ISO_8859_16.isSingleByte())
        assertTrue(ISO_8859_8_I.isSingleByte())
        assertTrue(KOI8_R.isSingleByte())
        assertTrue(KOI8_U.isSingleByte())
        assertTrue(MACINTOSH.isSingleByte())
        assertTrue(WINDOWS_874.isSingleByte())
        assertTrue(WINDOWS_1250.isSingleByte())
        assertTrue(WINDOWS_1251.isSingleByte())
        assertTrue(WINDOWS_1252.isSingleByte())
        assertTrue(WINDOWS_1253.isSingleByte())
        assertTrue(WINDOWS_1254.isSingleByte())
        assertTrue(WINDOWS_1255.isSingleByte())
        assertTrue(WINDOWS_1256.isSingleByte())
        assertTrue(WINDOWS_1257.isSingleByte())
        assertTrue(WINDOWS_1258.isSingleByte())
        assertTrue(X_MAC_CYRILLIC.isSingleByte())
        assertTrue(X_USER_DEFINED.isSingleByte())
    }

    @Test
    fun testLatin1ByteCompatibleUpTo() {
        val buffer = byteArrayOf('a'.code.toByte(), 0x81.toByte(), 0xB6.toByte(), 0xF6.toByte(), 0xF0.toByte(), 0x82.toByte(), 0xB4.toByte())
        assertEquals(1, BIG5.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, EUC_JP.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, EUC_KR.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, GB18030.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, GBK.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertNull(REPLACEMENT.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, SHIFT_JIS.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, UTF_8.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertNull(UTF_16BE.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertNull(UTF_16LE.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, ISO_2022_JP.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))

        assertEquals(1, IBM866.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_2.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_3.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_4.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_5.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_6.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_7.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(3, ISO_8859_8.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, ISO_8859_10.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, ISO_8859_13.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, ISO_8859_14.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(6, ISO_8859_15.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, ISO_8859_16.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(3, ISO_8859_8_I.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, KOI8_R.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, KOI8_U.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, MACINTOSH.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(2, WINDOWS_874.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, WINDOWS_1250.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, WINDOWS_1251.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(5, WINDOWS_1252.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(3, WINDOWS_1253.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, WINDOWS_1254.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(3, WINDOWS_1255.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, WINDOWS_1256.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, WINDOWS_1257.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(4, WINDOWS_1258.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, X_MAC_CYRILLIC.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))
        assertEquals(1, X_USER_DEFINED.newDecoderWithoutBomHandling().latin1ByteCompatibleUpTo(buffer))

        assertNull(UTF_8.newDecoder().latin1ByteCompatibleUpTo(buffer))
    }
}
