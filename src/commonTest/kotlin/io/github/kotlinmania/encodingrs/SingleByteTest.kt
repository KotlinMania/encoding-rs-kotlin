// port-lint: tests single_byte.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SingleByteTest {
    private val HIGH_BYTES: ByteArray = ByteArray(128) { (it + 0x80).toByte() }

    private fun decodeSingleByte(encoding: Encoding, data: CharArray) {
        val withReplacement = CharArray(128)
        for (i in 0 until 128) {
            val codePoint = data[i]
            if (codePoint == '\u0000') {
                withReplacement[i] = '\uFFFD'
            } else {
                withReplacement[i] = codePoint
            }
        }
        val decoder = encoding.newDecoderWithoutBomHandling()
        val dst = CharArray(128)
        val (res, read, written) = decoder.decodeToUtf16Raw(HIGH_BYTES, dst, true)
        for (i in 0 until 128) {
            if (data[i] == '\u0000') {
                // Malformed sequence encountered
                return
            }
        }
        assertEquals(DecoderResult.InputEmpty, res)
        assertEquals(128, read)
        assertEquals(128, written)
        assertEquals(withReplacement.concatToString(), dst.concatToString())
    }

    private fun encodeSingleByte(encoding: Encoding, data: CharArray) {
        val withZeros = ByteArray(128)
        for (i in 0 until 128) {
            val codePoint = data[i]
            if (codePoint == '\u0000') {
                withZeros[i] = 0
            } else {
                withZeros[i] = HIGH_BYTES[i]
            }
        }
        val encoder = encoding.newEncoder()
        val dst = ByteArray(128)
        val (res, read, written) = encoder.encodeFromUtf16Raw(data, dst, true)
        for (i in 0 until 128) {
            if (data[i] == '\u0000') {
                return
            }
        }
        assertEquals(EncoderResult.InputEmpty, res)
        assertEquals(128, read)
        assertEquals(128, written)
        for (i in 0 until 128) {
            assertEquals(withZeros[i], dst[i])
        }
    }

    @Test
    fun testWindows1255Ca() {
        val bytes = byteArrayOf(0xCA.toByte())
        val chars = "\u05BA"
        val (decoded, _) = Encoding.WINDOWS_1255.decodeWithoutBomHandling(bytes)
        assertEquals(chars, decoded)
        val (encoded, _, _) = Encoding.WINDOWS_1255.encode(chars)
        assertEquals(1, encoded.size)
        assertEquals(0xCA.toByte(), encoded[0])
    }

    @Test
    fun testAsciiPunctuation() {
        val bytes =
            byteArrayOf(
                0xC1.toByte(),
                0xF5.toByte(),
                0xF4.toByte(),
                0xFC.toByte(),
                0x20,
                0xE5.toByte(),
                0xDF.toByte(),
                0xED.toByte(),
                0xE1.toByte(),
                0xE9.toByte(),
                0x20,
                0xDD.toByte(),
                0xED.toByte(),
                0xE1.toByte(),
                0x20,
                0xF4.toByte(),
                0xE5.toByte(),
                0xF3.toByte(),
                0xF4.toByte(),
                0x2E,
                0x20,
                0xC1.toByte(),
                0xF5.toByte(),
                0xF4.toByte(),
                0xFC.toByte(),
                0x20,
                0xE5.toByte(),
                0xDF.toByte(),
                0xED.toByte(),
                0xE1.toByte(),
                0xE9.toByte(),
                0x20,
                0xDD.toByte(),
                0xED.toByte(),
                0xE1.toByte(),
                0x20,
                0xF4.toByte(),
                0xE5.toByte(),
                0xF3.toByte(),
                0xF4.toByte(),
                0x2E,
            )
        val characters = "\u0391\u03C5\u03C4\u03CC \u03B5\u03AF\u03BD\u03B1\u03B9 \u03AD\u03BD\u03B1 \u03C4\u03B5\u03C3\u03C4. \u0391\u03C5\u03C4\u03CC \u03B5\u03AF\u03BD\u03B1\u03B9 \u03AD\u03BD\u03B1 \u03C4\u03B5\u03C3\u03C4."
        val (decoded, _) = Encoding.WINDOWS_1253.decodeWithoutBomHandling(bytes)
        assertEquals(characters, decoded)
        val (encoded, _, _) = Encoding.WINDOWS_1253.encode(characters)
        assertEquals(bytes.size, encoded.size)
        for (i in bytes.indices) {
            assertEquals(bytes[i], encoded[i])
        }
    }

    @Test
    fun testDecodeMalformed() {
        val bytes = byteArrayOf(0xC1.toByte(), 0xF5.toByte(), 0xD2.toByte(), 0xF4.toByte(), 0xFC.toByte())
        val characters = "\u0391\u03C5\uFFFD\u03C4\u03CC"
        val (decoded, _) = Encoding.WINDOWS_1253.decodeWithoutBomHandling(bytes)
        assertEquals(characters, decoded)
    }

    @Test
    fun testEncodeUnmappables() {
        val (encoded1, _, hasErrors1) = Encoding.WINDOWS_1253.encode("\u0391\u03C5\u2603\u03C4\u03CC")
        assertTrue(hasErrors1)
        assertEquals(
            byteArrayOf(0xC1.toByte(), 0xF5.toByte(), 0x26, 0x23, 0x39, 0x37, 0x33, 0x31, 0x3B, 0xF4.toByte(), 0xFC.toByte()).toList(),
            encoded1.toList(),
        )
    }

    @Test
    fun testEncodeUnpairedSurrogates() {
        val (encoded1, _, hasErrors1) = Encoding.WINDOWS_1253.encodeFromUtf16(charArrayOf('\u0391', '\u03C5', '\uDCA9', '\u03C4', '\u03CC'))
        assertTrue(hasErrors1)
        assertEquals(
            byteArrayOf(0xC1.toByte(), 0xF5.toByte(), 0x26, 0x23, 0x36, 0x35, 0x35, 0x33, 0x33, 0x3B, 0xF4.toByte(), 0xFC.toByte()).toList(),
            encoded1.toList(),
        )
        val (encoded2, _, hasErrors2) = Encoding.WINDOWS_1253.encodeFromUtf16(charArrayOf('\u0391', '\u03C5', '\uD83D', '\u03C4', '\u03CC'))
        assertTrue(hasErrors2)
        assertEquals(
            byteArrayOf(0xC1.toByte(), 0xF5.toByte(), 0x26, 0x23, 0x36, 0x35, 0x35, 0x33, 0x33, 0x3B, 0xF4.toByte(), 0xFC.toByte()).toList(),
            encoded2.toList(),
        )
        val (encoded3, _, hasErrors3) = Encoding.WINDOWS_1253.encodeFromUtf16(charArrayOf('\u0391', '\u03C5', '\u03C4', '\u03CC', '\uD83D'))
        assertTrue(hasErrors3)
        assertEquals(
            byteArrayOf(0xC1.toByte(), 0xF5.toByte(), 0xF4.toByte(), 0xFC.toByte(), 0x26, 0x23, 0x36, 0x35, 0x35, 0x33, 0x33, 0x3B).toList(),
            encoded3.toList(),
        )
    }

    @Test
    fun testSingleByteFromTwoLowSurrogates() {
        val output = ByteArray(40)
        val encoder = Encoding.WINDOWS_1253.newEncoder()
        val res = encoder.encodeFromUtf16(charArrayOf('\uDC00', '\uDEDE'), output, true)
        assertEquals(CoderResult.InputEmpty, res.result)
        assertEquals(2, res.read)
        val expectation = "&#65533;&#65533;".encodeToByteArray()
        assertEquals(expectation.size, res.written)
        assertTrue(res.hadErrors)
        assertEquals(expectation.toList(), output.copyOf(res.written).toList())
    }

    @Test
    fun testSingleByteDecode() {
        decodeSingleByte(Encoding.IBM866, Data.ibm866)
        decodeSingleByte(Encoding.ISO_8859_10, Data.iso885910)
        decodeSingleByte(Encoding.ISO_8859_13, Data.iso885913)
        decodeSingleByte(Encoding.ISO_8859_14, Data.iso885914)
        decodeSingleByte(Encoding.ISO_8859_15, Data.iso885915)
        decodeSingleByte(Encoding.ISO_8859_16, Data.iso885916)
        decodeSingleByte(Encoding.ISO_8859_2, Data.iso88592)
        decodeSingleByte(Encoding.ISO_8859_3, Data.iso88593)
        decodeSingleByte(Encoding.ISO_8859_4, Data.iso88594)
        decodeSingleByte(Encoding.ISO_8859_5, Data.iso88595)
        decodeSingleByte(Encoding.ISO_8859_6, Data.iso88596)
        decodeSingleByte(Encoding.ISO_8859_7, Data.iso88597)
        decodeSingleByte(Encoding.ISO_8859_8, Data.iso88598)
        decodeSingleByte(Encoding.KOI8_R, Data.koi8r)
        decodeSingleByte(Encoding.KOI8_U, Data.koi8u)
        decodeSingleByte(Encoding.MACINTOSH, Data.macintosh)
        decodeSingleByte(Encoding.WINDOWS_1250, Data.windows1250)
        decodeSingleByte(Encoding.WINDOWS_1251, Data.windows1251)
        decodeSingleByte(Encoding.WINDOWS_1252, Data.windows1252)
        decodeSingleByte(Encoding.WINDOWS_1253, Data.windows1253)
        decodeSingleByte(Encoding.WINDOWS_1254, Data.windows1254)
        decodeSingleByte(Encoding.WINDOWS_1255, Data.windows1255)
        decodeSingleByte(Encoding.WINDOWS_1256, Data.windows1256)
        decodeSingleByte(Encoding.WINDOWS_1257, Data.windows1257)
        decodeSingleByte(Encoding.WINDOWS_1258, Data.windows1258)
        decodeSingleByte(Encoding.WINDOWS_874, Data.windows874)
        decodeSingleByte(Encoding.X_MAC_CYRILLIC, Data.xMacCyrillic)
    }

    @Test
    fun testSingleByteEncode() {
        encodeSingleByte(Encoding.IBM866, Data.ibm866)
        encodeSingleByte(Encoding.ISO_8859_10, Data.iso885910)
        encodeSingleByte(Encoding.ISO_8859_13, Data.iso885913)
        encodeSingleByte(Encoding.ISO_8859_14, Data.iso885914)
        encodeSingleByte(Encoding.ISO_8859_15, Data.iso885915)
        encodeSingleByte(Encoding.ISO_8859_16, Data.iso885916)
        encodeSingleByte(Encoding.ISO_8859_2, Data.iso88592)
        encodeSingleByte(Encoding.ISO_8859_3, Data.iso88593)
        encodeSingleByte(Encoding.ISO_8859_4, Data.iso88594)
        encodeSingleByte(Encoding.ISO_8859_5, Data.iso88595)
        encodeSingleByte(Encoding.ISO_8859_6, Data.iso88596)
        encodeSingleByte(Encoding.ISO_8859_7, Data.iso88597)
        encodeSingleByte(Encoding.ISO_8859_8, Data.iso88598)
        encodeSingleByte(Encoding.KOI8_R, Data.koi8r)
        encodeSingleByte(Encoding.KOI8_U, Data.koi8u)
        encodeSingleByte(Encoding.MACINTOSH, Data.macintosh)
        encodeSingleByte(Encoding.WINDOWS_1250, Data.windows1250)
        encodeSingleByte(Encoding.WINDOWS_1251, Data.windows1251)
        encodeSingleByte(Encoding.WINDOWS_1252, Data.windows1252)
        encodeSingleByte(Encoding.WINDOWS_1253, Data.windows1253)
        encodeSingleByte(Encoding.WINDOWS_1254, Data.windows1254)
        encodeSingleByte(Encoding.WINDOWS_1255, Data.windows1255)
        encodeSingleByte(Encoding.WINDOWS_1256, Data.windows1256)
        encodeSingleByte(Encoding.WINDOWS_1257, Data.windows1257)
        encodeSingleByte(Encoding.WINDOWS_1258, Data.windows1258)
        encodeSingleByte(Encoding.WINDOWS_874, Data.windows874)
        encodeSingleByte(Encoding.X_MAC_CYRILLIC, Data.xMacCyrillic)
    }
}
