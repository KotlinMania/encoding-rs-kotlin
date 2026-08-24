// port-lint: tests euc_jp.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class EucJpTest {
    private fun decodeEucJp(bytes: ByteArray, expect: String) {
        val (decoded, _, _) = Encoding.EUC_JP.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encodeEucJp(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.EUC_JP.encode(string)
        assertContentEquals(expect, encoded)
    }

    @Test
    fun testEucJpDecode() {
        // Empty
        decodeEucJp(byteArrayOf(), "")

        // ASCII
        decodeEucJp(byteArrayOf(0x61, 0x62), "\u0061\u0062")

        // Half-width
        decodeEucJp(byteArrayOf(0x8E.toByte(), 0xA1.toByte()), "\uFF61")
        decodeEucJp(byteArrayOf(0x8E.toByte(), 0xDF.toByte()), "\uFF9F")
        decodeEucJp(byteArrayOf(0x8E.toByte(), 0xA0.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0x8E.toByte(), 0xE0.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0x8E.toByte(), 0xFF.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0x8E.toByte()), "\uFFFD")

        // JIS 0212
        decodeEucJp(byteArrayOf(0x8F.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0x8F.toByte(), 0xA2.toByte(), 0xAF.toByte()), "\u02D8")
        decodeEucJp(byteArrayOf(0x8F.toByte(), 0xA2.toByte(), 0xFF.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0x8F.toByte(), 0xA1.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0x8F.toByte()), "\uFFFD")

        // JIS 0208
        decodeEucJp(byteArrayOf(0xA1.toByte(), 0xA1.toByte()), "\u3000")
        decodeEucJp(byteArrayOf(0xA1.toByte(), 0xA0.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0xFC.toByte(), 0xFE.toByte()), "\uFF02")
        decodeEucJp(byteArrayOf(0xFE.toByte(), 0xFE.toByte()), "\uFFFD")
        decodeEucJp(byteArrayOf(0xA1.toByte()), "\uFFFD")

        // Bad leads
        decodeEucJp(byteArrayOf(0xFF.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0xA0.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x80.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x81.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x82.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x83.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x84.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x85.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x86.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x87.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x88.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x89.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x8A.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x8B.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x8C.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")
        decodeEucJp(byteArrayOf(0x8D.toByte(), 0xA1.toByte(), 0xA1.toByte()), "\uFFFD\u3000")

        // Bad ASCII trail
        decodeEucJp(byteArrayOf(0xA1.toByte(), 0x40), "\uFFFD\u0040")
    }

    @Test
    fun testEucJpEncode() {
        // Empty
        encodeEucJp("", byteArrayOf())

        // ASCII
        encodeEucJp("\u0061\u0062", byteArrayOf(0x61, 0x62))

        // Exceptional code points
        encodeEucJp("\u00A5", byteArrayOf(0x5C))
        encodeEucJp("\u203E", byteArrayOf(0x7E))
        encodeEucJp("\u2212", byteArrayOf(0xA1.toByte(), 0xDD.toByte()))

        // Half-width
        encodeEucJp("\uFF61", byteArrayOf(0x8E.toByte(), 0xA1.toByte()))
        encodeEucJp("\uFF9F", byteArrayOf(0x8E.toByte(), 0xDF.toByte()))

        // JIS 0212
        encodeEucJp("\u02D8", "&#728;".encodeToByteArray())

        // JIS 0208
        encodeEucJp("\u3000", byteArrayOf(0xA1.toByte(), 0xA1.toByte()))
        encodeEucJp("\uFF02", byteArrayOf(0xFC.toByte(), 0xFE.toByte()))
    }
}
