// port-lint: tests shift_jis.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ShiftJisTest {
    private fun decodeShiftJis(bytes: ByteArray, expect: String) {
        val (decoded, _, _) = Encoding.SHIFT_JIS.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encodeShiftJis(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.SHIFT_JIS.encode(string)
        assertContentEquals(expect, encoded)
    }

    @Test
    fun testShiftJisDecode() {
        // Empty
        decodeShiftJis(byteArrayOf(), "")

        // ASCII
        decodeShiftJis(byteArrayOf(0x61, 0x62), "\u0061\u0062")

        // Half-width
        decodeShiftJis(byteArrayOf(0xA1.toByte()), "\uFF61")
        decodeShiftJis(byteArrayOf(0xDF.toByte()), "\uFF9F")
        decodeShiftJis(byteArrayOf(0xA0.toByte()), "\uFFFD")
        decodeShiftJis(byteArrayOf(0xE0.toByte()), "\uFFFD")
        decodeShiftJis(byteArrayOf(0xA0.toByte(), 0x2B), "\uFFFD+")
        decodeShiftJis(byteArrayOf(0xE0.toByte(), 0x2B), "\uFFFD+")

        // EUDC
        decodeShiftJis(byteArrayOf(0xF0.toByte(), 0x40), "\uE000")
        decodeShiftJis(byteArrayOf(0xF9.toByte(), 0xFC.toByte()), "\uE757")
        decodeShiftJis(byteArrayOf(0xEF.toByte(), 0xFC.toByte()), "\uFFFD")
        decodeShiftJis(byteArrayOf(0xFA.toByte(), 0x40), "\u2170")

        // JIS 0208
        decodeShiftJis(byteArrayOf(0x81.toByte(), 0x40), "\u3000")
        decodeShiftJis(byteArrayOf(0x81.toByte(), 0x3F), "\uFFFD?")
        decodeShiftJis(byteArrayOf(0xEE.toByte(), 0xFC.toByte()), "\uFF02")
        decodeShiftJis(byteArrayOf(0xEE.toByte(), 0xFD.toByte()), "\uFFFD")
        decodeShiftJis(byteArrayOf(0xFA.toByte(), 0x40), "\u2170")
        decodeShiftJis(byteArrayOf(0xFA.toByte(), 0x3F), "\uFFFD?")
        decodeShiftJis(byteArrayOf(0xFC.toByte(), 0x4B), "\u9ED1")
        decodeShiftJis(byteArrayOf(0xFC.toByte(), 0x4C), "\uFFFDL")
    }

    @Test
    fun testShiftJisEncode() {
        // Empty
        encodeShiftJis("", byteArrayOf())

        // ASCII
        encodeShiftJis("\u0061\u0062", byteArrayOf(0x61, 0x62))

        // Exceptional code points
        encodeShiftJis("\u0080", byteArrayOf(0x80.toByte()))
        encodeShiftJis("\u00A5", byteArrayOf(0x5C))
        encodeShiftJis("\u203E", byteArrayOf(0x7E))
        encodeShiftJis("\u2212", byteArrayOf(0x81.toByte(), 0x7C))

        // Half-width
        encodeShiftJis("\uFF61", byteArrayOf(0xA1.toByte()))
        encodeShiftJis("\uFF9F", byteArrayOf(0xDF.toByte()))

        // EUDC
        encodeShiftJis("\uE000", "&#57344;".encodeToByteArray())
        encodeShiftJis("\uE757", "&#59223;".encodeToByteArray())

        // JIS 0212
        encodeShiftJis("\u02D8", "&#728;".encodeToByteArray())

        // JIS 0208
        encodeShiftJis("\u3000", byteArrayOf(0x81.toByte(), 0x40))
        encodeShiftJis("\uFF02", byteArrayOf(0xFA.toByte(), 0x57))
        encodeShiftJis("\u2170", byteArrayOf(0xFA.toByte(), 0x40))
        encodeShiftJis("\u9ED1", byteArrayOf(0xFC.toByte(), 0x4B))
    }

    @Test
    fun testShiftJisHalfWidthKatakanaLength() {
        val decoder = Encoding.SHIFT_JIS.newDecoder()
        val needed = decoder.maxUtf8BufferLengthWithoutReplacement(1)!!
        val output = ByteArray(needed)
        val (result, read, written) =
            decoder.decodeToUtf8WithoutReplacement(byteArrayOf(0xA1.toByte()), output, true)
        assertEquals(DecoderResult.InputEmpty, result)
        assertEquals(1, read)
        assertEquals(3, written)
        assertEquals(0xEF.toByte(), output[0])
        assertEquals(0xBD.toByte(), output[1])
        assertEquals(0xA1.toByte(), output[2])
    }
}
