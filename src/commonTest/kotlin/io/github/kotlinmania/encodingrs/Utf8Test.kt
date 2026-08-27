// port-lint: tests encoding_rs/src/utf_8.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Utf8Test {
    private fun decodeUtf8ToUtf8(bytes: ByteArray, expect: String) {
        val (decoded, _) = Encoding.UTF_8.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun decodeValidUtf8(string: String) {
        decodeUtf8ToUtf8(string.encodeToByteArray(), string)
    }

    private fun encodeUtf8FromUtf16(string: CharArray, expect: ByteArray) {
        val (encoded, _, _) = Encoding.UTF_8.encodeFromUtf16(string)
        assertEquals(expect.toList(), encoded.toList())
    }

    private fun encodeUtf8FromUtf8(string: String, expect: ByteArray) {
        val (encoded, _, _) = Encoding.UTF_8.encode(string)
        assertEquals(expect.toList(), encoded.toList())
    }

    private fun encodeUtf8FromUtf16WithOutputLimit(
        string: CharArray,
        expect: String,
        limit: Int,
        expectResult: EncoderResult,
    ) {
        val dst = ByteArray(limit)
        val encoder = Encoding.UTF_8.newEncoder()
        val (result, read, written) = encoder.encodeFromUtf16WithoutReplacement(string, dst, false)
        assertEquals(expectResult, result)
        if (expectResult == EncoderResult.InputEmpty) {
            assertEquals(string.size, read)
        }
        assertEquals(expect.encodeToByteArray().toList(), dst.copyOf(written).toList())

        val dst64 = ByteArray(64) { it.toByte() }
        val encoder2 = Encoding.UTF_8.newEncoder()
        val (_, _, j) = encoder2.encodeFromUtf16WithoutReplacement(string, dst64, false)
        for (k in j until 64) {
            assertEquals(k.toByte(), dst64[k])
        }
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

    @Test
    fun testUtf8Encode() {
        encodeUtf8FromUtf16(charArrayOf(), byteArrayOf())
        encodeUtf8FromUtf8("", byteArrayOf())

        encodeUtf8FromUtf16(charArrayOf('\u0000'), "\u0000".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\u007F'), "\u007F".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\u0080'), "\u0080".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\u07FF'), "\u07FF".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\u0800'), "\u0800".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uD7FF'), "\uD7FF".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uD800'), "\uFFFD".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uD800', '\u0062'), "\uFFFD\u0062".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uDFFF'), "\uFFFD".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uDFFF', '\u0062'), "\uFFFD\u0062".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uE000'), "\uE000".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uFFFF'), "\uFFFF".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uD800', '\uDC00'), "\uD800\uDC00".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uDBFF', '\uDFFF'), "\uDBFF\uDFFF".encodeToByteArray())
        encodeUtf8FromUtf16(charArrayOf('\uDC00', '\uDEDE'), "\uFFFD\uFFFD".encodeToByteArray())
    }

    @Test
    fun testEncodeUtf8FromUtf16WithOutputLimit() {
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0062'), "\u0062", 1, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00A7'), "\u00A7", 2, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u2603'), "\u2603", 3, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDCA9'), "\uD83D\uDCA9", 4, EncoderResult.InputEmpty)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00A7'), "", 1, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u2603'), "", 2, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDCA9'), "", 3, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u0062'), "\u0063\u0062", 2, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00A7'), "\u0063\u00A7", 3, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u2603'), "\u0063\u2603", 4, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\uD83D', '\uDCA9'), "\u0063\uD83D\uDCA9", 5, EncoderResult.InputEmpty)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00A7'), "\u0063", 2, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u2603'), "\u0063", 3, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\uD83D', '\uDCA9'), "\u0063", 4, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\u0062'), "\u00B6\u0062", 3, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\u00A7'), "\u00B6\u00A7", 4, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\u2603'), "\u00B6\u2603", 5, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\uD83D', '\uDCA9'), "\u00B6\uD83D\uDCA9", 6, EncoderResult.InputEmpty)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\u00A7'), "\u00B6", 3, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\u2603'), "\u00B6", 4, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u00B6', '\uD83D', '\uDCA9'), "\u00B6", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062'), "\u263A\u0062", 4, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u00A7'), "\u263A\u00A7", 5, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u2603'), "\u263A\u2603", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\uD83D', '\uDCA9'), "\u263A\uD83D\uDCA9", 7, EncoderResult.InputEmpty)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u00A7'), "\u263A", 4, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u2603'), "\u263A", 5, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\uD83D', '\uDCA9'), "\u263A", 6, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\u0062'), "\uD83D\uDE0E\u0062", 5, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\u00A7'), "\uD83D\uDE0E\u00A7", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\u2603'), "\uD83D\uDE0E\u2603", 7, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\uD83D', '\uDCA9'), "\uD83D\uDE0E\uD83D\uDCA9", 8, EncoderResult.InputEmpty)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\u00A7'), "\uD83D\uDE0E", 5, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\u2603'), "\uD83D\uDE0E", 6, EncoderResult.OutputFull)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\uD83D', '\uDE0E', '\uD83D', '\uDCA9'), "\uD83D\uDE0E", 7, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u0062', '\u0062'), "\u0063\u00B6\u0062\u0062", 5, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u0062', '\u0062'), "\u0063\u00B6\u0062", 4, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u0062', '\u0062', '\u0062'), "\u0063\u00B6\u0062\u0062\u0062", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u0062', '\u0062', '\u0062'), "\u0063\u00B6\u0062\u0062", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062', '\u0062'), "\u263A\u0062\u0062", 5, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062', '\u0062'), "\u263A\u0062", 4, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062', '\u0062', '\u0062'), "\u263A\u0062\u0062\u0062", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062', '\u0062', '\u0062'), "\u263A\u0062\u0062", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u00A7'), "\u0063\u00B6\u00A7", 5, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u00A7'), "\u0063\u00B6", 4, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u00A7', '\u0062'), "\u0063\u00B6\u00A7\u0062", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u00A7', '\u0062'), "\u0063\u00B6\u00A7", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u00A7', '\u0062'), "\u263A\u00A7\u0062", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u00A7', '\u0062'), "\u263A\u00A7", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u0062', '\u00A7'), "\u0063\u00B6\u0062\u00A7", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u0062', '\u00A7'), "\u0063\u00B6\u0062", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062', '\u00A7'), "\u263A\u0062\u00A7", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u0062', '\u00A7'), "\u263A\u0062", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u2603'), "\u0063\u00B6\u2603", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\u2603'), "\u0063\u00B6", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u2603'), "\u263A\u2603", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\u2603'), "\u263A", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\uD83D'), "\u0063\u00B6\uFFFD", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\uD83D'), "\u0063\u00B6", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\uD83D'), "\u263A\uFFFD", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\uD83D'), "\u263A", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\uDCA9'), "\u0063\u00B6\uFFFD", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u0063', '\u00B6', '\uDCA9'), "\u0063\u00B6", 5, EncoderResult.OutputFull)

        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\uDCA9'), "\u263A\uFFFD", 6, EncoderResult.InputEmpty)
        encodeUtf8FromUtf16WithOutputLimit(charArrayOf('\u263A', '\uDCA9'), "\u263A", 5, EncoderResult.OutputFull)
    }

    @Test
    fun testUtf8MaxLengthFromUtf16() {
        val encoder = Encoding.UTF_8.newEncoder()
        val output = ByteArray(13)
        val input = charArrayOf('\u2C9F', '\u2CA9', '\u2CA3', '\u2C9F')
        val needed = encoder.maxBufferLengthFromUtf16WithoutReplacement(input.size)!!
        val (result, _, _) = encoder.encodeFromUtf16WithoutReplacement(input, output.copyOf(needed), true)
        assertEquals(EncoderResult.InputEmpty, result)
    }

    @Test
    fun testDecodeBomPrefixedSplitByteTriple() {
        val output = CharArray(20)
        val decoder = Encoding.UTF_8.newDecoder()
        val needed1 = decoder.maxUtf16BufferLength(1)!!
        val slice1 = CharArray(needed1)
        val r1 = decoder.decodeToUtf16(byteArrayOf(0xEF.toByte()), slice1, false)
        assertEquals(CoderResult.InputEmpty, r1.result)
        assertEquals(1, r1.read)
        assertEquals(0, r1.written)
        assertFalse(r1.hadErrors)

        val needed2 = decoder.maxUtf16BufferLength(1)!!
        val slice2 = CharArray(needed2)
        val r2 = decoder.decodeToUtf16(byteArrayOf(0xBF.toByte()), slice2, false)
        assertEquals(CoderResult.InputEmpty, r2.result)
        assertEquals(1, r2.read)
        assertEquals(0, r2.written)
        assertFalse(r2.hadErrors)

        val needed3 = decoder.maxUtf16BufferLength(1)!!
        val slice3 = CharArray(needed3)
        val r3 = decoder.decodeToUtf16(byteArrayOf(0xBE.toByte()), slice3, true)
        assertEquals(CoderResult.InputEmpty, r3.result)
        assertEquals(1, r3.read)
        assertEquals(1, r3.written)
        assertFalse(r3.hadErrors)
        assertEquals('\uFFFE', slice3[0])
    }

    @Test
    fun testDecodeBomPrefixedSplitBytePair() {
        val decoder = Encoding.UTF_8.newDecoder()
        val needed1 = decoder.maxUtf16BufferLength(1)!!
        val slice1 = CharArray(needed1)
        val r1 = decoder.decodeToUtf16(byteArrayOf(0xEF.toByte()), slice1, false)
        assertEquals(CoderResult.InputEmpty, r1.result)
        assertEquals(1, r1.read)
        assertEquals(0, r1.written)
        assertFalse(r1.hadErrors)

        val needed2 = decoder.maxUtf16BufferLength(1)!!
        val slice2 = CharArray(needed2)
        val r2 = decoder.decodeToUtf16(byteArrayOf(0xBC.toByte()), slice2, true)
        assertEquals(CoderResult.InputEmpty, r2.result)
        assertEquals(1, r2.read)
        assertEquals(1, r2.written)
        assertTrue(r2.hadErrors)
        assertEquals('\uFFFD', slice2[0])
    }

    @Test
    fun testDecodeBomPrefix() {
        val decoder = Encoding.UTF_8.newDecoder()
        val needed = decoder.maxUtf16BufferLength(1)!!
        val slice = CharArray(needed)
        val r = decoder.decodeToUtf16(byteArrayOf(0xEF.toByte()), slice, true)
        assertEquals(CoderResult.InputEmpty, r.result)
        assertEquals(1, r.read)
        assertEquals(1, r.written)
        assertTrue(r.hadErrors)
        assertEquals('\uFFFD', slice[0])
    }

    @Test
    fun testTail() {
        val output = CharArray(1)
        val decoder = Encoding.UTF_8.newDecoderWithoutBomHandling()
        val r = decoder.decodeToUtf16("\u00E4a".encodeToByteArray(), output, false)
        assertEquals(CoderResult.OutputFull, r.result)
        assertEquals(2, r.read)
        assertEquals(1, r.written)
        assertFalse(r.hadErrors)
        assertEquals('\u00E4', output[0])
    }
}
