// port-lint: tests iso_2022_jp.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Iso2022JpTest {
    private fun decodeIso2022Jp(bytes: ByteArray, expect: String) {
        val (decoded, _, _) = Encoding.ISO_2022_JP.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encodeIso2022Jp(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.ISO_2022_JP.encode(string)
        assertContentEquals(expect, encoded)
    }

    @Test
    fun testIso2022JpDecode() {
        // Empty
        decodeIso2022Jp(byteArrayOf(), "")

        // ASCII
        decodeIso2022Jp(byteArrayOf(0x5B), "\u005B")
        decodeIso2022Jp(byteArrayOf(0x5C), "\u005C")
        decodeIso2022Jp(byteArrayOf(0x7E), "\u007E")
        decodeIso2022Jp(byteArrayOf(0x0E), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x0F), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x80.toByte()), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0xFF.toByte()), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x5B), "\u005B")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x5C), "\u005C")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x7E), "\u007E")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x0E), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x0F), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x80.toByte()), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0xFF.toByte()), "\uFFFD")

        // Roman
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x5B), "\u005B")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x5C), "\u00A5")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x7E), "\u203E")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x0E), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x0F), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x80.toByte()), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0xFF.toByte()), "\uFFFD")

        // Katakana
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x20), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x21), "\uFF61")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x5F), "\uFF9F")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x60), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x0E), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x0F), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0x80.toByte()), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x49, 0xFF.toByte()), "\uFFFD")

        // 0208 differences from 1978 to 1983
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x40, 0x54, 0x64), "\u58FA")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x40, 0x44, 0x5B), "\u58F7")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x40, 0x74, 0x21), "\u582F")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x40, 0x36, 0x46), "\u5C2D")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x40, 0x28, 0x2E), "\u250F")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x54, 0x64), "\u58FA")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x44, 0x5B), "\u58F7")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x74, 0x21), "\u582F")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x36, 0x46), "\u5C2D")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x28, 0x2E), "\u250F")

        // Broken 0208
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x28, 0x41), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x40, 0x80.toByte(), 0x54, 0x64), "\uFFFD\u58FA")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x24, 0x42, 0x28, 0x80.toByte()), "\uFFFD")

        // Transitions
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x5C, 0x1B, 0x28, 0x4A, 0x5C), "\u005C\u00A5")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x5C, 0x1B, 0x28, 0x49, 0x21), "\u005C\uFF61")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x5C, 0x1B, 0x24, 0x40, 0x54, 0x64), "\u005C\u58FA")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x5C, 0x1B, 0x24, 0x42, 0x54, 0x64), "\u005C\u58FA")

        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x5C, 0x1B, 0x28, 0x42, 0x5C), "\u00A5\u005C")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x5C, 0x1B, 0x28, 0x49, 0x21), "\u00A5\uFF61")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x5C, 0x1B, 0x24, 0x40, 0x54, 0x64), "\u00A5\u58FA")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x4A, 0x5C, 0x1B, 0x24, 0x42, 0x54, 0x64), "\u00A5\u58FA")

        // Empty transitions
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x1B, 0x28, 0x4A), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x1B, 0x28, 0x49), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x1B, 0x24, 0x40), "\uFFFD")
        decodeIso2022Jp(byteArrayOf(0x1B, 0x28, 0x42, 0x1B, 0x24, 0x42), "\uFFFD")
    }

    @Test
    fun testIso2022JpEncode() {
        // Empty
        encodeIso2022Jp("", byteArrayOf())

        // ASCII
        encodeIso2022Jp("ab", "ab".encodeToByteArray())
        encodeIso2022Jp("\uD83D\uDCA9", "&#128169;".encodeToByteArray()) // U+1F4A9
        encodeIso2022Jp("\u001B", "&#65533;".encodeToByteArray())
        encodeIso2022Jp("\u000E", "&#65533;".encodeToByteArray())
        encodeIso2022Jp("\u000F", "&#65533;".encodeToByteArray())

        // Roman
        encodeIso2022Jp("a\u00A5b", byteArrayOf(0x61, 0x1B, 0x28, 0x4A, 0x5C, 0x62, 0x1B, 0x28, 0x42))
        encodeIso2022Jp("a\u203Eb", byteArrayOf(0x61, 0x1B, 0x28, 0x4A, 0x7E, 0x62, 0x1B, 0x28, 0x42))

        // Half-width Katakana
        encodeIso2022Jp("\uFF61", byteArrayOf(0x1B, 0x24, 0x42, 0x21, 0x23, 0x1B, 0x28, 0x42))
        encodeIso2022Jp("\uFF65", byteArrayOf(0x1B, 0x24, 0x42, 0x21, 0x26, 0x1B, 0x28, 0x42))

        // 0208
        encodeIso2022Jp("\u58FA", byteArrayOf(0x1B, 0x24, 0x42, 0x54, 0x64, 0x1B, 0x28, 0x42))
        encodeIso2022Jp("\u58FA\u250F", byteArrayOf(0x1B, 0x24, 0x42, 0x54, 0x64, 0x28, 0x2E, 0x1B, 0x28, 0x42))
    }

    @Test
    fun testIso2022JpHalfWidthKatakanaLength() {
        val output = ByteArray(20)
        val decoder = Encoding.ISO_2022_JP.newDecoder()
        val (res1, read1, written1) =
            decoder.decodeToUtf8WithoutReplacement(byteArrayOf(0x1B, 0x28, 0x49), output, false)
        assertEquals(DecoderResult.InputEmpty, res1)
        assertEquals(3, read1)
        assertEquals(0, written1)

        val needed = decoder.maxUtf8BufferLengthWithoutReplacement(1)!!
        val output2 = ByteArray(needed)
        val (res2, read2, written2) =
            decoder.decodeToUtf8WithoutReplacement(byteArrayOf(0x21), output2, true)
        assertEquals(DecoderResult.InputEmpty, res2)
        assertEquals(1, read2)
        assertEquals(3, written2)
        assertEquals(0xEF.toByte(), output2[0])
        assertEquals(0xBD.toByte(), output2[1])
        assertEquals(0xA1.toByte(), output2[2])
    }

    @Test
    fun testIso2022JpLengthAfterEscape() {
        val output = CharArray(20)
        val decoder = Encoding.ISO_2022_JP.newDecoder()
        val outcome1 =
            decoder.decodeToUtf16(byteArrayOf(0x1B), output, false)
        assertEquals(CoderResult.InputEmpty, outcome1.result)
        assertEquals(1, outcome1.read)
        assertEquals(0, outcome1.written)
        assertFalse(outcome1.hadErrors)

        val needed = decoder.maxUtf16BufferLength(1)!!
        val output2 = CharArray(needed)
        val outcome2 =
            decoder.decodeToUtf16("A".encodeToByteArray(), output2, true)
        assertEquals(CoderResult.InputEmpty, outcome2.result)
        assertEquals(1, outcome2.read)
        assertEquals(2, outcome2.written)
        assertTrue(outcome2.hadErrors)
        assertEquals('\uFFFD', output2[0])
        assertEquals('A', output2[1])
    }

    @Test
    fun testIso2022JpDecodeAll() {
        // Upstream ignores this full fixture under Miri because it is too slow; Kotlin has no Miri mode.
        val input = iso2022JpDecodeInput()
        val expectation = iso2022JpDecodeExpectation().decodeToString(throwOnInvalidSequence = true)
        val (decoded, hadErrors) = Encoding.ISO_2022_JP.decodeWithoutBomHandling(input)
        assertTrue(hadErrors, "Should have had errors.")
        assertEquals(expectation, decoded)
    }

    @Test
    fun testIso2022JpEncodeAll() {
        // Upstream ignores this full fixture under Miri because it is too slow; Kotlin has no Miri mode.
        val input = iso2022JpEncodeInput().decodeToString(throwOnInvalidSequence = true)
        val expectation = iso2022JpEncodeExpectation()
        val (encoded, encoding, hadErrors) = Encoding.ISO_2022_JP.encode(input)
        assertFalse(hadErrors, "Should not have had errors.")
        assertEquals(Encoding.ISO_2022_JP, encoding)
        assertContentEquals(expectation, encoded)
    }

    @Test
    fun testIso2022JpRetainsLeadWhenOutputIsFull() {
        val decoder = Iso2022JpDecoder()
        val prefix = byteArrayOf(0x1B, 0x24, 0x42, 0x21)
        assertEquals(
            Triple(DecoderResult.InputEmpty, prefix.size, 0),
            decoder.decodeToUtf16Raw(prefix, CharArray(1), false),
        )
        val trail = byteArrayOf(0x50)
        assertEquals(
            Triple(DecoderResult.OutputFull, 0, 0),
            decoder.decodeToUtf16Raw(trail, CharArray(0), false),
        )
        val output = CharArray(1)
        assertEquals(
            Triple(DecoderResult.InputEmpty, 1, 1),
            decoder.decodeToUtf16Raw(trail, output, true),
        )
        assertEquals("\uFF5B", output.concatToString())
    }

    @Test
    fun testIso2022JpEncodeFromTwoLowSurrogates() {
        val expectation = "&#65533;&#65533;".encodeToByteArray()
        val output = ByteArray(40)
        val encoder = Encoding.ISO_2022_JP.newEncoder()
        val outcome =
            encoder.encodeFromUtf16(charArrayOf(0xDC00.toChar(), 0xDEDE.toChar()), output, true)
        assertEquals(CoderResult.InputEmpty, outcome.result)
        assertEquals(2, outcome.read)
        assertEquals(expectation.size, outcome.written)
        assertTrue(outcome.hadErrors)
        assertContentEquals(expectation, output.copyOfRange(0, outcome.written))
    }
}
