package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EncoderGrowthTest {
    @Test
    fun completeInputGrowsForRepeatedOriginalSnowmanFixture() {
        // The original single-byte unmappable test maps U+2603 to &#9731;.
        val input = "\u2603".repeat(32)
        val expected = "&#9731;".repeat(32).encodeToByteArray()
        val (encoded, encoding, hadErrors) = WINDOWS_1253.encode(input)
        assertEquals(224, expected.size)
        assertContentEquals(expected, encoded)
        assertEquals(WINDOWS_1253, encoding)
        assertTrue(hadErrors)
        Testing.encode(WINDOWS_1253, input, expected)
    }

    @Test
    fun replacementSpaceIsReservedBeforeConsumingInput() {
        val destination = ByteArray(6) { 0x55 }
        val result = WINDOWS_1253.newEncoder().encodeFromUtf16(charArrayOf('\u2603'), destination, true)
        assertEquals(CoderResult.OutputFull, result.result)
        assertEquals(0, result.read)
        assertEquals(0, result.written)
        assertFalse(result.hadErrors)
        assertContentEquals(ByteArray(6) { 0x55 }, destination)
    }

    @Test
    fun utf16OutputFullResumesWithoutDroppingUnmappableInput() {
        val input = charArrayOf('\u2603', '\u2603')
        val encoder = WINDOWS_1253.newEncoder()
        val firstDestination = ByteArray(17)
        val first = encoder.encodeFromUtf16(input, firstDestination, true)
        assertEquals(CoderResult.OutputFull, first.result)
        assertEquals(1, first.read)
        assertEquals(7, first.written)
        assertTrue(first.hadErrors)

        val secondDestination = ByteArray(17)
        val second = encoder.encodeFromUtf16(input.copyOfRange(first.read, input.size), secondDestination, true)
        assertEquals(CoderResult.InputEmpty, second.result)
        assertEquals(1, second.read)
        assertEquals(7, second.written)
        assertTrue(second.hadErrors)
        assertEquals(input.size, first.read + second.read)
        assertContentEquals(
            "&#9731;&#9731;".encodeToByteArray(),
            firstDestination.copyOf(first.written) + secondDestination.copyOf(second.written),
        )
    }

    @Test
    fun utf8OutputFullCountsBytesAndResumesWithoutDroppingInput() {
        val input = "\u2603\u2603".encodeToByteArray()
        val encoder = WINDOWS_1253.newEncoder()
        val firstDestination = ByteArray(17)
        val first = encoder.encodeFromUtf8(input.decodeToString(), firstDestination, true)
        assertEquals(CoderResult.OutputFull, first.result)
        assertEquals(3, first.read)
        assertEquals(7, first.written)
        assertTrue(first.hadErrors)

        val secondDestination = ByteArray(17)
        val second = encoder.encodeFromUtf8(input.decodeToString(first.read, input.size), secondDestination, true)
        assertEquals(CoderResult.InputEmpty, second.result)
        assertEquals(3, second.read)
        assertEquals(7, second.written)
        assertTrue(second.hadErrors)
        assertEquals(input.size, first.read + second.read)
        assertContentEquals(
            "&#9731;&#9731;".encodeToByteArray(),
            firstDestination.copyOf(first.written) + secondDestination.copyOf(second.written),
        )
    }

    @Test
    fun numericReplacementRetainsAstralUnicodeScalars() {
        val cases =
            listOf(
                "\uD83D\uDCA9" to "&#128169;",
                "\uDBFF\uDFFF" to "&#1114111;",
            )
        for ((input, expected) in cases) {
            val destination = ByteArray(32)
            val result = WINDOWS_1253.newEncoder().encodeFromUtf8(input, destination, true)
            assertEquals(CoderResult.InputEmpty, result.result)
            assertEquals(4, result.read)
            assertEquals(expected.length, result.written)
            assertTrue(result.hadErrors)
            assertContentEquals(expected.encodeToByteArray(), destination.copyOf(result.written))
            val (complete, encoding, hadErrors) = WINDOWS_1253.encode(input)
            assertContentEquals(expected.encodeToByteArray(), complete)
            assertEquals(WINDOWS_1253, encoding)
            assertTrue(hadErrors)
        }
    }
}
