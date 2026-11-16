package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DecoderReplacementTest {
    @Test
    fun malformedUtf8WritesReplacementCharacterBytes() {
        // The original decode_to_utf8 malformed arm writes EF BF BD, not the EF BB BF BOM.
        val malformedInputs =
            listOf(
                byteArrayOf(0xFF.toByte()),
                byteArrayOf(0x80.toByte()),
                byteArrayOf(0xE2.toByte(), 0x82.toByte()),
            )
        for (input in malformedInputs) {
            val destination = ByteArray(16) { 0x55 }
            val result = UTF_8.newDecoderWithoutBomHandling().decodeToUtf8(input, destination, last = true)
            assertEquals(CoderResult.InputEmpty, result.result)
            assertEquals(input.size, result.read)
            assertEquals(3, result.written)
            assertTrue(result.hadErrors)
            assertContentEquals(byteArrayOf(0xEF.toByte(), 0xBF.toByte(), 0xBD.toByte()), destination.copyOfRange(0, result.written))
            assertEquals("\uFFFD", destination.decodeToString(0, result.written))
            assertContentEquals(ByteArray(13) { 0x55 }, destination.copyOfRange(result.written, destination.size))
        }
    }

    @Test
    fun replacementPreservesSurroundingAsciiAndByteCounts() {
        val input = byteArrayOf(0x61, 0xFF.toByte(), 0x62)
        val destination = ByteArray(16)
        val result = UTF_8.newDecoderWithoutBomHandling().decodeToUtf8(input, destination, last = true)
        assertEquals(CoderResult.InputEmpty, result.result)
        assertEquals(input.size, result.read)
        assertEquals(5, result.written)
        assertTrue(result.hadErrors)
        assertContentEquals(byteArrayOf(0x61, 0xEF.toByte(), 0xBF.toByte(), 0xBD.toByte(), 0x62), destination.copyOfRange(0, result.written))
        assertEquals("a\uFFFDb", destination.decodeToString(0, result.written))
    }

    @Test
    fun stringReceiverAppendsTheReplacementCharacter() {
        val destination = StringBuilder("prefix:")
        val result =
            UTF_8.newDecoderWithoutBomHandling().decodeToString(
                byteArrayOf(0xFF.toByte()),
                destination,
                last = true,
            )
        assertEquals(CoderResult.InputEmpty, result.result)
        assertEquals(1, result.read)
        assertEquals(3, result.written)
        assertTrue(result.hadErrors)
        assertEquals("prefix:\uFFFD", destination.toString())
    }
}
