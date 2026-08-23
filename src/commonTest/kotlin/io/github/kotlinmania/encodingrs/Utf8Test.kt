// port-lint: tests utf_8.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals

class Utf8Test {
    private fun decodeUtf8ToUtf8(bytes: ByteArray, expect: String) {
        val (decoded, _) = Encoding.UTF_8.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun decodeValidUtf8(string: String) {
        decodeUtf8ToUtf8(string.encodeToByteArray(), string)
    }

    @Test
    fun testUtf8Decode() {
        // Empty
        decodeValidUtf8("")
        // ASCII
        decodeValidUtf8("ab")
        // Low BMP
        decodeValidUtf8("a\u00E4Z")
        // High BMP
        decodeValidUtf8("a\u2603Z")
        // Astral
        decodeValidUtf8("a\uD83D\uDCA9Z")

        // Low BMP with last byte missing
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC3.toByte(), 'Z'.code.toByte()), "a\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC3.toByte()), "a\uFFFD")

        // High BMP with last byte missing
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte(), 'Z'.code.toByte()), "a\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte()), "a\uFFFD")

        // Astral with last byte missing
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte(), 'Z'.code.toByte()), "a\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte()), "a\uFFFD")

        // Lone highest continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xBF.toByte()), "a\uFFFD")

        // Two lone highest continuations
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xBF.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xBF.toByte(), 0xBF.toByte()), "a\uFFFD\uFFFD")

        // Low BMP followed by lowest lone continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC3.toByte(), 0xA4.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\u00E4\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC3.toByte(), 0xA4.toByte(), 0x80.toByte()), "a\u00E4\uFFFD")

        // Low BMP followed by highest lone continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC3.toByte(), 0xA4.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\u00E4\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC3.toByte(), 0xA4.toByte(), 0xBF.toByte()), "a\u00E4\uFFFD")

        // High BMP followed by lowest lone continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte(), 0x83.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\u2603\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte(), 0x83.toByte(), 0x80.toByte()), "a\u2603\uFFFD")

        // High BMP followed by highest lone continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte(), 0x83.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\u2603\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte(), 0x83.toByte(), 0xBF.toByte()), "a\u2603\uFFFD")

        // Astral followed by lowest lone continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte(), 0xA9.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uD83D\uDCA9\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte(), 0xA9.toByte(), 0x80.toByte()), "a\uD83D\uDCA9\uFFFD")

        // Astral followed by highest lone continuation
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte(), 0xA9.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\uD83D\uDCA9\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte(), 0xA9.toByte(), 0xBF.toByte()), "a\uD83D\uDCA9\uFFFD")

        // Boundary conditions
        decodeValidUtf8("Z\u0000")
        decodeValidUtf8("Z\u0000Z")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC0.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC0.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x80.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x80.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xFF.toByte()), "a\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xFF.toByte(), 'Z'.code.toByte()), "a\uFFFDZ")
        decodeValidUtf8("a\u007F")
        decodeValidUtf8("a\u007FZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC1.toByte(), 0xBF.toByte()), "a\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC1.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x81.toByte(), 0xBF.toByte()), "a\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x81.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x81.toByte(), 0xBF.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x81.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte()), "a\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC2.toByte(), 0x80.toByte()), "a\u0080")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC2.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\u0080Z")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x82.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x82.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x82.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x82.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFD\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC1.toByte(), 0x80.toByte()), "a\uFFFD\uFFFD")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC1.toByte(), 0x80.toByte(), 'Z'.code.toByte()), "a\uFFFD\uFFFDZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC2.toByte(), 0x7F.toByte()), "a\uFFFD\u007F")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xC2.toByte(), 0x7F.toByte(), 'Z'.code.toByte()), "a\uFFFD\u007FZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xDF.toByte(), 0xBF.toByte()), "a\u07FF")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xDF.toByte(), 0xBF.toByte(), 'Z'.code.toByte()), "a\u07FFZ")
        decodeUtf8ToUtf8(byteArrayOf('a'.code.toByte(), 0xE0.toByte(), 0x9F.toByte(), 0xBF.toByte()), "a\uFFFD\uFFFD\uFFFD")
    }

    @Test
    fun testUtf8ValidUpTo() {
        assertEquals(2, Utf8.utf8ValidUpTo("ab".encodeToByteArray()))
        assertEquals(3, Utf8.utf8ValidUpTo("a\u00E4".encodeToByteArray()))
        assertEquals(4, Utf8.utf8ValidUpTo("a\u2603".encodeToByteArray()))
        assertEquals(5, Utf8.utf8ValidUpTo("a\uD83D\uDCA9".encodeToByteArray()))
        assertEquals(1, Utf8.utf8ValidUpTo(byteArrayOf('a'.code.toByte(), 0xC3.toByte())))
        assertEquals(1, Utf8.utf8ValidUpTo(byteArrayOf('a'.code.toByte(), 0xE2.toByte(), 0x98.toByte())))
        assertEquals(1, Utf8.utf8ValidUpTo(byteArrayOf('a'.code.toByte(), 0xF0.toByte(), 0x9F.toByte(), 0x92.toByte())))
    }
}
