// port-lint: tests mem.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MemTest {
    @Test
    fun testIsAsciiSuccess() {
        val src = ByteArray(128) { it.toByte() }
        for (i in 0 until src.size) {
            assertTrue(Mem.isAscii(src.copyOfRange(i, src.size)))
        }
    }

    @Test
    fun testIsAsciiFail() {
        val src = ByteArray(128) { it.toByte() }
        for (i in 0 until src.size) {
            val tail = src.copyOfRange(i, src.size)
            for (j in 0 until tail.size) {
                tail[j] = 0xA0.toByte()
                assertFalse(Mem.isAscii(tail))
            }
        }
    }

    @Test
    fun testIsBasicLatinSuccess() {
        val src = CharArray(128) { it.toChar() }
        for (i in 0 until src.size) {
            assertTrue(Mem.isBasicLatin(src.copyOfRange(i, src.size)))
        }
    }

    @Test
    fun testIsBasicLatinFail() {
        val src = CharArray(128) { it.toChar() }
        for (i in 0 until src.size) {
            val tail = src.copyOfRange(i, src.size)
            for (j in 0 until tail.size) {
                tail[j] = 0xA0.toChar()
                assertFalse(Mem.isBasicLatin(tail))
            }
        }
    }

    @Test
    fun testIsUtf16Latin1Success() {
        val src = CharArray(256) { it.toChar() }
        for (i in 0 until src.size) {
            val slice = src.copyOfRange(i, src.size)
            assertTrue(Mem.isUtf16Latin1(slice))
            assertEquals(Latin1Bidi.Latin1, Mem.checkUtf16ForLatin1AndBidi(slice))
        }
    }

    @Test
    fun testIsUtf16Latin1Fail() {
        val len = 64
        val src = CharArray(len) { it.toChar() }
        for (i in 0 until src.size) {
            val tail = src.copyOfRange(i, src.size)
            for (j in 0 until tail.size) {
                tail[j] = (0x100 + j).toChar()
                assertFalse(Mem.isUtf16Latin1(tail))
                assertNotEquals(Latin1Bidi.Latin1, Mem.checkUtf16ForLatin1AndBidi(tail))
            }
        }
    }

    @Test
    fun testIsStrLatin1Success() {
        val len = 64
        val src = CharArray(len) { it.toChar() }
        for (i in 0 until src.size) {
            val s = src.copyOfRange(i, src.size).concatToString()
            assertTrue(Mem.isStrLatin1(s))
            assertEquals(Latin1Bidi.Latin1, Mem.checkStrForLatin1AndBidi(s))
        }
    }

    @Test
    fun testIsStrLatin1Fail() {
        val len = 32
        val src = CharArray(len) { it.toChar() }
        for (i in 0 until src.size) {
            val tail = src.copyOfRange(i, src.size)
            for (j in 0 until tail.size) {
                tail[j] = (0x100 + j).toChar()
                val s = tail.concatToString()
                assertFalse(Mem.isStrLatin1(s))
                assertNotEquals(Latin1Bidi.Latin1, Mem.checkStrForLatin1AndBidi(s))
            }
        }
    }

    @Test
    fun testIsUtf8Latin1Success() {
        val len = 64
        val src = CharArray(len) { it.toChar() }
        for (i in 0 until src.size) {
            val s = src.copyOfRange(i, src.size).concatToString()
            val bytes = s.encodeToByteArray()
            assertTrue(Mem.isUtf8Latin1(bytes))
            assertEquals(Latin1Bidi.Latin1, Mem.checkUtf8ForLatin1AndBidi(bytes))
        }
    }

    @Test
    fun testIsUtf8Latin1Fail() {
        val len = 32
        val src = CharArray(len) { it.toChar() }
        for (i in 0 until src.size) {
            val tail = src.copyOfRange(i, src.size)
            for (j in 0 until tail.size) {
                tail[j] = (0x100 + j).toChar()
                val s = tail.concatToString()
                val bytes = s.encodeToByteArray()
                assertFalse(Mem.isUtf8Latin1(bytes))
                assertNotEquals(Latin1Bidi.Latin1, Mem.checkUtf8ForLatin1AndBidi(bytes))
            }
        }
    }

    @Test
    fun testIsUtf8Latin1Invalid() {
        assertFalse(Mem.isUtf8Latin1(byteArrayOf(0xC3.toByte())))
        assertFalse(Mem.isUtf8Latin1(byteArrayOf('a'.code.toByte(), 0xC3.toByte())))
        assertFalse(Mem.isUtf8Latin1(byteArrayOf(0xFF.toByte())))
        assertFalse(Mem.isUtf8Latin1(byteArrayOf('a'.code.toByte(), 0xFF.toByte())))
        assertFalse(Mem.isUtf8Latin1(byteArrayOf(0xC3.toByte(), 0xFF.toByte())))
        assertFalse(Mem.isUtf8Latin1(byteArrayOf('a'.code.toByte(), 0xC3.toByte(), 0xFF.toByte())))
    }

    @Test
    fun testConvertUtf8ToUtf16() {
        val src = "abcdefghijklmnopqrstu\uD83D\uDCA9v\u2603w\u00B6xyzz"
        val bytes = src.encodeToByteArray()
        val dst = CharArray(bytes.size + 1)
        val len = Mem.convertUtf8ToUtf16(bytes, dst)
        val result = dst.copyOfRange(0, len).concatToString()
        assertEquals(src, result)
    }

    @Test
    fun testConvertStrToUtf16() {
        val src = "abcdefghijklmnopqrstu\uD83D\uDCA9v\u2603w\u00B6xyzz"
        val dst = CharArray(src.length)
        val len = Mem.convertStrToUtf16(src, dst)
        val result = dst.copyOfRange(0, len).concatToString()
        assertEquals(src, result)
    }

    @Test
    fun testConvertUtf16ToUtf8Partial() {
        val reference = "abcdefghijklmnopqrstu\uD83D\uDCA9v\u2603w\u00B6xyzz"
        val src = reference.toCharArray()
        val dst = ByteArray(src.size * 3 + 1)
        val dstSlice = ByteArray(24)
        val (read, written) = Mem.convertUtf16ToUtf8Partial(src, dstSlice)
        dstSlice.copyInto(dst, 0, 0, written)
        val remainingDst = ByteArray(dst.size - written)
        val len2 = Mem.convertUtf16ToUtf8(src.copyOfRange(read, src.size), remainingDst)
        remainingDst.copyInto(dst, written, 0, len2)
        val finalLen = written + len2
        val actual = dst.copyOfRange(0, finalLen).decodeToString()
        assertEquals(reference, actual)
    }

    @Test
    fun testConvertUtf16ToUtf8() {
        val reference = "abcdefghijklmnopqrstu\uD83D\uDCA9v\u2603w\u00B6xyzz"
        val src = reference.toCharArray()
        val dst = ByteArray(src.size * 3 + 1)
        val len = Mem.convertUtf16ToUtf8(src, dst)
        val actual = dst.copyOfRange(0, len).decodeToString()
        assertEquals(reference, actual)
    }

    @Test
    fun testConvertLatin1ToUtf16() {
        val src = ByteArray(256) { it.toByte() }
        val reference = CharArray(256) { it.toChar() }
        val dst = CharArray(256)
        Mem.convertLatin1ToUtf16(src, dst)
        assertEquals(reference.concatToString(), dst.concatToString())
    }

    @Test
    fun testConvertLatin1ToUtf8Partial() {
        val dst = ByteArray(2)
        val input = byteArrayOf('a'.code.toByte(), 0xFF.toByte())
        val (read, written) = Mem.convertLatin1ToUtf8Partial(input, dst)
        assertEquals(1, read)
        assertEquals(1, written)
    }

    @Test
    fun testConvertLatin1ToUtf8() {
        val src = ByteArray(256) { it.toByte() }
        val reference = CharArray(256) { it.toChar() }.concatToString()
        val dst = ByteArray(src.size * 2)
        val len = Mem.convertLatin1ToUtf8(src, dst)
        val actual = dst.copyOfRange(0, len).decodeToString()
        assertEquals(reference, actual)
    }

    @Test
    fun testConvertUtf8ToLatin1Lossy() {
        val reference = ByteArray(256) { it.toByte() }
        val src = CharArray(256) { it.toChar() }.concatToString()
        val bytes = src.encodeToByteArray()
        val dst = ByteArray(bytes.size)
        val len = Mem.convertUtf8ToLatin1Lossy(bytes, dst)
        val actual = dst.copyOfRange(0, len)
        assertEquals(reference.size, actual.size)
        for (i in 0 until 256) {
            assertEquals(reference[i], actual[i])
        }
    }

    @Test
    fun testConvertUtf16ToLatin1Lossy() {
        val src = CharArray(256) { it.toChar() }
        val reference = ByteArray(256) { it.toByte() }
        val dst = ByteArray(src.size)
        Mem.convertUtf16ToLatin1Lossy(src, dst)
        for (i in 0 until 256) {
            assertEquals(reference[i], dst[i])
        }
    }

    @Test
    fun testUtf16ValidUpTo() {
        val valid = charArrayOf(
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0x2603.toChar(), 0xD83D.toChar(), 0xDCA9.toChar(), 0x00B6.toChar(),
        )
        assertEquals(16, Mem.utf16ValidUpTo(valid))

        val loneHigh = charArrayOf(
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0x2603.toChar(), 0xD83D.toChar(), 0x00B6.toChar(),
        )
        assertEquals(14, Mem.utf16ValidUpTo(loneHigh))

        val loneLow = charArrayOf(
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0x2603.toChar(), 0xDCA9.toChar(), 0x00B6.toChar(),
        )
        assertEquals(14, Mem.utf16ValidUpTo(loneLow))

        val loneHighAtEnd = charArrayOf(
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0x2603.toChar(), 0x00B6.toChar(), 0xD83D.toChar(),
        )
        assertEquals(15, Mem.utf16ValidUpTo(loneHighAtEnd))
    }

    @Test
    fun testEnsureUtf16Validity() {
        val src = charArrayOf(
            0.toChar(), 0xD83D.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0xD83D.toChar(), 0xDCA9.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0xDCA9.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
        )
        val reference = charArrayOf(
            0.toChar(), 0xFFFD.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0xD83D.toChar(), 0xDCA9.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0xFFFD.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
            0.toChar(), 0.toChar(), 0.toChar(), 0.toChar(),
        )
        Mem.ensureUtf16Validity(src)
        assertEquals(reference.concatToString(), src.concatToString())
    }

    @Test
    fun testIsCharBidi() {
        assertFalse(Mem.isCharBidi('a'))
        assertFalse(Mem.isCharBidi('\u03B1'))
        assertFalse(Mem.isCharBidi('\u3041'))
        assertFalse(Mem.isCharBidi(0x1F4A9))
        assertFalse(Mem.isCharBidi('\uFE00'))
        assertFalse(Mem.isCharBidi('\u202C'))
        assertFalse(Mem.isCharBidi('\uFEFF'))
        assertTrue(Mem.isCharBidi('\u0590'))
        assertTrue(Mem.isCharBidi('\u08FF'))
        assertTrue(Mem.isCharBidi('\u061C'))
        assertTrue(Mem.isCharBidi('\uFB50'))
        assertTrue(Mem.isCharBidi('\uFDFF'))
        assertTrue(Mem.isCharBidi('\uFE70'))
        assertTrue(Mem.isCharBidi('\uFEFE'))
        assertTrue(Mem.isCharBidi('\u200F'))
        assertTrue(Mem.isCharBidi('\u202B'))
        assertTrue(Mem.isCharBidi('\u202E'))
        assertTrue(Mem.isCharBidi('\u2067'))
        assertTrue(Mem.isCharBidi(0x10800))
        assertTrue(Mem.isCharBidi(0x10FFF))
        assertTrue(Mem.isCharBidi(0x1E800))
        assertTrue(Mem.isCharBidi(0x1EFFF))
    }

    @Test
    fun testIsUtf16CodeUnitBidi() {
        assertFalse(Mem.isUtf16CodeUnitBidi(0x0062))
        assertFalse(Mem.isUtf16CodeUnitBidi(0x03B1))
        assertFalse(Mem.isUtf16CodeUnitBidi(0x3041))
        assertFalse(Mem.isUtf16CodeUnitBidi(0xD801))
        assertFalse(Mem.isUtf16CodeUnitBidi(0xFE00))
        assertFalse(Mem.isUtf16CodeUnitBidi(0x202C))
        assertFalse(Mem.isUtf16CodeUnitBidi(0xFEFF))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x0590))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x08FF))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x061C))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xFB1D))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xFB50))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xFDFF))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xFE70))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xFEFE))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x200F))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x202B))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x202E))
        assertTrue(Mem.isUtf16CodeUnitBidi(0x2067))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xD802))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xD803))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xD83A))
        assertTrue(Mem.isUtf16CodeUnitBidi(0xD83B))
    }

    @Test
    fun testIsStrBidi() {
        assertFalse(Mem.isStrBidi("abcdefghijklmnopaabcdefghijklmnop"))
        assertFalse(Mem.isStrBidi("abcdefghijklmnop\u03B1abcdefghijklmnop"))
        assertFalse(Mem.isStrBidi("abcdefghijklmnop\u3041abcdefghijklmnop"))
        assertFalse(Mem.isStrBidi("abcdefghijklmnop\uD83D\uDCA9abcdefghijklmnop"))
        assertFalse(Mem.isStrBidi("abcdefghijklmnop\uFE00abcdefghijklmnop"))
        assertFalse(Mem.isStrBidi("abcdefghijklmnop\u202Cabcdefghijklmnop"))
        assertFalse(Mem.isStrBidi("abcdefghijklmnop\uFEFFabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u0590abcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u08FFabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u061Cabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\uFB50abcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\uFDFFabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\uFE70abcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\uFEFEabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u200Fabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u202Babcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u202Eabcdefghijklmnop"))
        assertTrue(Mem.isStrBidi("abcdefghijklmnop\u2067abcdefghijklmnop"))
    }
}
