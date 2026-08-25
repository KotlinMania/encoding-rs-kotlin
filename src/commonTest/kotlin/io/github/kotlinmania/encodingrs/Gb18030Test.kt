// port-lint: tests gb18030.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Gb18030Test {
    private fun decodeGb18030(bytes: ByteArray, expect: String) {
        val (decoded, _, _) = Encoding.GB18030.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encodeGb18030(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.GB18030.encode(string)
        assertContentEquals(expect, encoded)
    }

    private fun encodeGbk(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.GBK.encode(string)
        assertContentEquals(expect, encoded)
    }

    @Test
    fun testGb18030Decode() {
        // Empty
        decodeGb18030(byteArrayOf(), "")

        // ASCII
        decodeGb18030(byteArrayOf(0x61, 0x62), "\u0061\u0062")

        // euro
        decodeGb18030(byteArrayOf(0x80.toByte()), "\u20AC")
        decodeGb18030(byteArrayOf(0xA2.toByte(), 0xE3.toByte()), "\u20AC")

        // two bytes
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x40), "\u4E02")
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x7E), "\u4E8A")
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x7F), "\uFFFD\u007F")
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x80.toByte()), "\u4E90")
        decodeGb18030(byteArrayOf(0x81.toByte(), 0xFE.toByte()), "\u4FA2")
        decodeGb18030(byteArrayOf(0xFE.toByte(), 0x40), "\uFA0C")
        decodeGb18030(byteArrayOf(0xFE.toByte(), 0x7F), "\uFFFD\u007F")
        decodeGb18030(byteArrayOf(0xFE.toByte(), 0x80.toByte()), "\u4723")
        decodeGb18030(byteArrayOf(0xFE.toByte(), 0xFE.toByte()), "\uE4C5")

        // Changes between GB18030-2005 and GB18030-2022
        decodeGb18030(byteArrayOf(0xFE.toByte(), 0x7E), "\u9FB9")
        decodeGb18030(byteArrayOf(0xA6.toByte(), 0xDD.toByte()), "\uFE14")

        // The difference from the original GB18030
        decodeGb18030(byteArrayOf(0xA3.toByte(), 0xA0.toByte()), "\u3000")
        decodeGb18030(byteArrayOf(0xA1.toByte(), 0xA1.toByte()), "\u3000")

        // 0xFF
        decodeGb18030(byteArrayOf(0xFF.toByte(), 0x40), "\uFFFD\u0040")
        decodeGb18030(byteArrayOf(0xE3.toByte(), 0xFF.toByte(), 0x9A.toByte(), 0x33), "\uFFFD\uFFFD")
        decodeGb18030(byteArrayOf(0xFF.toByte(), 0x32, 0x9A.toByte(), 0x33), "\uFFFD\u0032\uFFFD")

        // Four bytes
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x30, 0x81.toByte(), 0x30), "\u0080")
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x35, 0xF4.toByte(), 0x37), "\uE7C7")
        decodeGb18030(byteArrayOf(0x81.toByte(), 0x37, 0xA3.toByte(), 0x30), "\u2603")
        decodeGb18030(byteArrayOf(0x94.toByte(), 0x39, 0xDA.toByte(), 0x33), "\uD83D\uDCA9") // U+1F4A9
        decodeGb18030(byteArrayOf(0xE3.toByte(), 0x32, 0x9A.toByte(), 0x35), "\uDBFF\uDFFF") // U+10FFFF
        decodeGb18030(byteArrayOf(0xE3.toByte(), 0x32, 0x9A.toByte(), 0x36, 0x81.toByte(), 0x30), "\uFFFD\uFFFD")
        decodeGb18030(byteArrayOf(0xE3.toByte(), 0x32, 0x9A.toByte(), 0x36, 0x81.toByte(), 0x40), "\uFFFD\u4E02")
        decodeGb18030(byteArrayOf(0xE3.toByte(), 0x32, 0x9A.toByte()), "\uFFFD")
        decodeGb18030(byteArrayOf(0xE3.toByte(), 0x32, 0x9A.toByte(), 0x00), "\uFFFD\u0032\uFFFD\u0000")
    }

    @Test
    fun testGb18030Encode() {
        // Empty
        encodeGb18030("", byteArrayOf())

        // ASCII
        encodeGb18030("\u0061\u0062", byteArrayOf(0x61, 0x62))

        // euro
        encodeGb18030("\u20AC", byteArrayOf(0xA2.toByte(), 0xE3.toByte()))

        // two bytes
        encodeGb18030("\u4E02", byteArrayOf(0x81.toByte(), 0x40))
        encodeGb18030("\u4E8A", byteArrayOf(0x81.toByte(), 0x7E))

        // The difference from the original GB18030
        encodeGb18030("\uE5E5", "&#58853;".encodeToByteArray())
        encodeGb18030("\u3000", byteArrayOf(0xA1.toByte(), 0xA1.toByte()))

        // Four bytes
        encodeGb18030("\u0080", byteArrayOf(0x81.toByte(), 0x30, 0x81.toByte(), 0x30))
        encodeGb18030("\uE7C7", byteArrayOf(0x81.toByte(), 0x35, 0xF4.toByte(), 0x37))
        encodeGb18030("\u2603", byteArrayOf(0x81.toByte(), 0x37, 0xA3.toByte(), 0x30))
        encodeGb18030("\uD83D\uDCA9", byteArrayOf(0x94.toByte(), 0x39, 0xDA.toByte(), 0x33)) // U+1F4A9
        encodeGb18030("\uDBFF\uDFFF", byteArrayOf(0xE3.toByte(), 0x32, 0x9A.toByte(), 0x35)) // U+10FFFF

        // Edge cases
        encodeGb18030("\u00F7", byteArrayOf(0xA1.toByte(), 0xC2.toByte()))

        // GB18030-2022
        encodeGb18030("\u9FB9", byteArrayOf(0xFE.toByte(), 0x7E))
        encodeGb18030("\uFE14", byteArrayOf(0xA6.toByte(), 0xDD.toByte()))
        encodeGb18030("\uE843", byteArrayOf(0xFE.toByte(), 0x7E))
        encodeGb18030("\uE791", byteArrayOf(0xA6.toByte(), 0xDD.toByte()))

        // Non-change in GB18030-2022
        encodeGb18030("\uE817", byteArrayOf(0xFE.toByte(), 0x52))
    }

    @Test
    fun testGbkEncode() {
        // Empty
        encodeGbk("", byteArrayOf())

        // ASCII
        encodeGbk("\u0061\u0062", byteArrayOf(0x61, 0x62))

        // euro
        encodeGbk("\u20AC", byteArrayOf(0x80.toByte()))

        // two bytes
        encodeGbk("\u4E02", byteArrayOf(0x81.toByte(), 0x40))
        encodeGbk("\u4E8A", byteArrayOf(0x81.toByte(), 0x7E))

        // The difference from the original gb18030
        encodeGbk("\uE5E5", "&#58853;".encodeToByteArray())
        encodeGbk("\u3000", byteArrayOf(0xA1.toByte(), 0xA1.toByte()))

        // Four bytes unmappable in GBK
        encodeGbk("\u0080", "&#128;".encodeToByteArray())
        encodeGbk("\uE7C7", "&#59335;".encodeToByteArray())
        encodeGbk("\u2603", "&#9731;".encodeToByteArray())
        encodeGbk("\uD83D\uDCA9", "&#128169;".encodeToByteArray())
        encodeGbk("\uDBFF\uDFFF", "&#1114111;".encodeToByteArray())

        // Edge cases
        encodeGbk("\u00F7", byteArrayOf(0xA1.toByte(), 0xC2.toByte()))

        // GB18030-2022 in GBK
        encodeGbk("\u9FB9", byteArrayOf(0xFE.toByte(), 0x7E))
        encodeGbk("\uFE14", byteArrayOf(0xA6.toByte(), 0xDD.toByte()))
        encodeGbk("\uE843", byteArrayOf(0xFE.toByte(), 0x7E))
        encodeGbk("\uE791", byteArrayOf(0xA6.toByte(), 0xDD.toByte()))

        // Non-change in GB18030-2022
        encodeGbk("\uE817", byteArrayOf(0xFE.toByte(), 0x52))
    }

    @Test
    fun testGb18030EncodeFromUtf16MaxLength() {
        val encoder = Encoding.GB18030.newEncoder()
        val needed = encoder.maxBufferLengthFromUtf16WithoutReplacement(1)!!
        val output = ByteArray(needed)
        val (result, read, written) =
            encoder.encodeFromUtf16WithoutReplacement(
                charArrayOf('\u3000'),
                output,
                true,
            )
        assertEquals(EncoderResult.InputEmpty, result)
        assertEquals(1, read)
        assertEquals(2, written)
        assertEquals(0xA1.toByte(), output[0])
        assertEquals(0xA1.toByte(), output[1])
    }
}
