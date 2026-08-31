// port-lint: tests encoding_rs/src/mem.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
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
        val valid =
            charArrayOf(
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0x2603.toChar(),
                0xD83D.toChar(),
                0xDCA9.toChar(),
                0x00B6.toChar(),
            )
        assertEquals(16, Mem.utf16ValidUpTo(valid))

        val loneHigh =
            charArrayOf(
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0x2603.toChar(),
                0xD83D.toChar(),
                0x00B6.toChar(),
            )
        assertEquals(14, Mem.utf16ValidUpTo(loneHigh))

        val loneLow =
            charArrayOf(
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0x2603.toChar(),
                0xDCA9.toChar(),
                0x00B6.toChar(),
            )
        assertEquals(14, Mem.utf16ValidUpTo(loneLow))

        val loneHighAtEnd =
            charArrayOf(
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0x2603.toChar(),
                0x00B6.toChar(),
                0xD83D.toChar(),
            )
        assertEquals(15, Mem.utf16ValidUpTo(loneHighAtEnd))
    }

    @Test
    fun testEnsureUtf16Validity() {
        val src =
            charArrayOf(
                0.toChar(),
                0xD83D.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0xD83D.toChar(),
                0xDCA9.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0xDCA9.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
            )
        val reference =
            charArrayOf(
                0.toChar(),
                0xFFFD.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0xD83D.toChar(),
                0xDCA9.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0xFFFD.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
                0.toChar(),
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

    @Test
    fun testIsUtf8Bidi() {
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnopaabcdefghijklmnop".encodeToByteArray()))
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnop\u03B1abcdefghijklmnop".encodeToByteArray()))
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnop\u3041abcdefghijklmnop".encodeToByteArray()))
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnop\uD83D\uDCA9abcdefghijklmnop".encodeToByteArray()))
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnop\uFE00abcdefghijklmnop".encodeToByteArray()))
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnop\u202Cabcdefghijklmnop".encodeToByteArray()))
        assertFalse(Mem.isUtf8Bidi("abcdefghijklmnop\uFEFFabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u0590abcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u08FFabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u061Cabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uFB50abcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uFDFFabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uFE70abcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uFEFEabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u200Fabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u202Babcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u202Eabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\u2067abcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uD802\uDC00abcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uD803\uDFFFabcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uD83A\uDC00abcdefghijklmnop".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi("abcdefghijklmnop\uD83B\uDFFFabcdefghijklmnop".encodeToByteArray()))
    }

    @Test
    fun testIsUtf16Bidi() {
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x0062.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x03B1.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x3041.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD801.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFE00.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x202C.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertFalse(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFEFF.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x0590.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x08FF.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x061C.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFB1D.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFB50.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFDFF.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFE70.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFEFE.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x200F.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x202B.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x202E.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x2067.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD802.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD803.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD83A.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD83B.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertTrue(
            Mem.isUtf16Bidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x0590.toChar(),
                    0x3041.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
    }

    @Test
    fun testCheckStrForLatin1AndBidi() {
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnopaabcdefghijklmnop"))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u03B1abcdefghijklmnop"))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u3041abcdefghijklmnop"))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uD83D\uDCA9abcdefghijklmnop"))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uFE00abcdefghijklmnop"))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u202Cabcdefghijklmnop"))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uFEFFabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u0590abcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u08FFabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u061Cabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uFB50abcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uFDFFabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uFE70abcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uFEFEabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u200Fabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u202Babcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u202Eabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\u2067abcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uD802\uDC00abcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uD803\uDFFFabcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uD83A\uDC00abcdefghijklmnop"))
        assertEquals(Latin1Bidi.Bidi, Mem.checkStrForLatin1AndBidi("abcdefghijklmnop\uD83B\uDFFFabcdefghijklmnop"))
    }

    @Test
    fun testCheckUtf8ForLatin1AndBidi() {
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnopaabcdefghijklmnop".encodeToByteArray()))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u03B1abcdefghijklmnop".encodeToByteArray()))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u3041abcdefghijklmnop".encodeToByteArray()))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uD83D\uDCA9abcdefghijklmnop".encodeToByteArray()))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uFE00abcdefghijklmnop".encodeToByteArray()))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u202Cabcdefghijklmnop".encodeToByteArray()))
        assertNotEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uFEFFabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u0590abcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u08FFabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u061Cabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uFB50abcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uFDFFabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uFE70abcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uFEFEabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u200Fabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u202Babcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u202Eabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\u2067abcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uD802\uDC00abcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uD803\uDFFFabcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uD83A\uDC00abcdefghijklmnop".encodeToByteArray()))
        assertEquals(Latin1Bidi.Bidi, Mem.checkUtf8ForLatin1AndBidi("abcdefghijklmnop\uD83B\uDFFFabcdefghijklmnop".encodeToByteArray()))
    }

    @Test
    fun testCheckUtf16ForLatin1AndBidi() {
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x0062.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x03B1.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x3041.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD801.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFE00.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x202C.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertNotEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFEFF.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x0590.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x08FF.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x061C.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFB1D.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFB50.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFDFF.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFE70.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xFEFE.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x200F.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x202B.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x202E.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x2067.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD802.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD803.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD83A.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0xD83B.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
        assertEquals(
            Latin1Bidi.Bidi,
            Mem.checkUtf16ForLatin1AndBidi(
                charArrayOf(
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                    0x0590.toChar(),
                    0x3041.toChar(),
                    0x62.toChar(),
                    0x63.toChar(),
                    0x64.toChar(),
                    0x65.toChar(),
                    0x66.toChar(),
                    0x67.toChar(),
                    0x68.toChar(),
                    0x69.toChar(),
                ),
            ),
        )
    }

    private fun referenceIsCharBidi(c: Int): Boolean =
        when (c) {
            in 0x0590..0x08FF,
            in 0xFB1D..0xFDFF,
            in 0xFE70..0xFEFE,
            in 0x10800..0x10FFF,
            in 0x1E800..0x1EFFF,
            0x200F,
            0x202B,
            0x202E,
            0x2067,
            -> true
            else -> false
        }

    private fun referenceIsUtf16CodeUnitBidi(u: Int): Boolean =
        when (u) {
            in 0x0590..0x08FF,
            in 0xFB1D..0xFDFF,
            in 0xFE70..0xFEFE,
            0xD802,
            0xD803,
            0xD83A,
            0xD83B,
            0x200F,
            0x202B,
            0x202E,
            0x2067,
            -> true
            else -> false
        }

    @Test
    fun testIsCharBidiThoroughly() {
        for (i in 0 until 0xD800) {
            assertEquals(referenceIsCharBidi(i), Mem.isCharBidi(i))
        }
        for (i in 0xE000 until 0x110000) {
            assertEquals(referenceIsCharBidi(i), Mem.isCharBidi(i))
        }
    }

    @Test
    fun testIsUtf16CodeUnitBidiThoroughly() {
        for (i in 0 until 0x10000) {
            assertEquals(referenceIsUtf16CodeUnitBidi(i), Mem.isUtf16CodeUnitBidi(i))
        }
    }

    @Test
    fun testIsStrBidiThoroughly() {
        for (i in 0 until 0xD800) {
            val s = i.toChar().toString()
            assertEquals(referenceIsCharBidi(i), Mem.isStrBidi(s))
        }
        for (i in 0xE000 until 0x110000) {
            val s =
                if (i <= 0xFFFF) {
                    i.toChar().toString()
                } else {
                    val high = (0xD800 + ((i - 0x10000) shr 10)).toChar()
                    val low = (0xDC00 + ((i - 0x10000) and 0x3FF)).toChar()
                    charArrayOf(high, low).concatToString()
                }
            assertEquals(referenceIsCharBidi(i), Mem.isStrBidi(s))
        }
    }

    @Test
    fun testIsUtf8BidiThoroughly() {
        val buf = ByteArray(8)
        for (i in 0 until 0xD800) {
            val bytes = i.toChar().toString().encodeToByteArray()
            val expect = referenceIsCharBidi(i)
            assertEquals(expect, Mem.isUtf8Bidi(bytes))
            buf.fill(0)
            bytes.copyInto(buf)
            assertEquals(expect, Mem.isUtf8Bidi(buf))
        }
        for (i in 0xE000 until 0x110000) {
            val s =
                if (i <= 0xFFFF) {
                    i.toChar().toString()
                } else {
                    val high = (0xD800 + ((i - 0x10000) shr 10)).toChar()
                    val low = (0xDC00 + ((i - 0x10000) and 0x3FF)).toChar()
                    charArrayOf(high, low).concatToString()
                }
            val bytes = s.encodeToByteArray()
            val expect = referenceIsCharBidi(i)
            assertEquals(expect, Mem.isUtf8Bidi(bytes))
            buf.fill(0)
            bytes.copyInto(buf)
            assertEquals(expect, Mem.isUtf8Bidi(buf))
        }
    }

    @Test
    fun testIsUtf16BidiThoroughly() {
        val buf = CharArray(32)
        for (i in 0 until 0x10000) {
            buf[15] = i.toChar()
            assertEquals(referenceIsUtf16CodeUnitBidi(i), Mem.isUtf16Bidi(buf))
        }
    }

    @Test
    fun testIsUtf8BidiEdgeCases() {
        assertFalse(Mem.isUtf8Bidi(byteArrayOf(0xD5.toByte(), 0xBF.toByte(), 0x61.toByte())))
        assertFalse(Mem.isUtf8Bidi(byteArrayOf(0xD6.toByte(), 0x80.toByte(), 0x61.toByte())))
        assertFalse(Mem.isUtf8Bidi("abc".encodeToByteArray()))
        assertTrue(Mem.isUtf8Bidi(byteArrayOf(0xD5.toByte(), 0xBF.toByte(), 0xC2.toByte())))
        assertTrue(Mem.isUtf8Bidi(byteArrayOf(0xD6.toByte(), 0x80.toByte(), 0xC2.toByte())))
        assertTrue(Mem.isUtf8Bidi(byteArrayOf(0x61.toByte(), 0x62.toByte(), 0xC2.toByte())))
    }

    @Test
    fun testDecodeLatin1() {
        assertEquals("ab", Mem.decodeLatin1(byteArrayOf('a'.code.toByte(), 'b'.code.toByte())))
        assertEquals("a\u00E4", Mem.decodeLatin1(byteArrayOf('a'.code.toByte(), 0xE4.toByte())))
    }

    @Test
    fun testEncodeLatin1Lossy() {
        val ab = Mem.encodeLatin1Lossy("ab")
        assertContentEquals(byteArrayOf('a'.code.toByte(), 'b'.code.toByte()), ab)
        val aUmlaut = Mem.encodeLatin1Lossy("a\u00E4")
        assertContentEquals(byteArrayOf('a'.code.toByte(), 0xE4.toByte()), aUmlaut)
    }

    @Test
    fun testConvertUtf8ToUtf16WithoutReplacement() {
        val buf = CharArray(5)
        val dst2 = CharArray(2)
        assertEquals(2, Mem.convertUtf8ToUtf16WithoutReplacement("ab".encodeToByteArray(), dst2))
        assertEquals('a', dst2[0])
        assertEquals('b', dst2[1])

        val dst3 = CharArray(3)
        assertEquals(2, Mem.convertUtf8ToUtf16WithoutReplacement(byteArrayOf(0xC3.toByte(), 0xA4.toByte(), 'c'.code.toByte()), dst3))
        assertEquals(0x00E4.toChar(), dst3[0])
        assertEquals('c', dst3[1])
        assertEquals(0.toChar(), dst3[2])

        val snowmanBytes = byteArrayOf(0xE2.toByte(), 0x98.toByte(), 0x83.toByte())
        dst3.fill('c')
        assertEquals(1, Mem.convertUtf8ToUtf16WithoutReplacement(snowmanBytes, dst3))
        assertEquals(0x2603.toChar(), dst3[0])
        assertEquals('c', dst3[1])

        val dst4 = CharArray(4)
        dst4.fill('d')
        val snowmanD = byteArrayOf(0xE2.toByte(), 0x98.toByte(), 0x83.toByte(), 'd'.code.toByte())
        assertEquals(2, Mem.convertUtf8ToUtf16WithoutReplacement(snowmanD, dst4))
        assertEquals(0x2603.toChar(), dst4[0])
        assertEquals('d', dst4[1])

        buf.fill(0.toChar())
        val snowmanA = byteArrayOf(0xE2.toByte(), 0x98.toByte(), 0x83.toByte(), 0xC3.toByte(), 0xA4.toByte())
        assertEquals(2, Mem.convertUtf8ToUtf16WithoutReplacement(snowmanA, buf))
        assertEquals(0x2603.toChar(), buf[0])
        assertEquals(0x00E4.toChar(), buf[1])
        assertEquals(0.toChar(), buf[2])

        dst4.fill(0.toChar())
        val emoji = byteArrayOf(0xF0.toByte(), 0x9F.toByte(), 0x93.toByte(), 0x8E.toByte())
        assertEquals(2, Mem.convertUtf8ToUtf16WithoutReplacement(emoji, dst4))
        assertEquals(0xD83D.toChar(), dst4[0])
        assertEquals(0xDCCE.toChar(), dst4[1])
        assertEquals(0.toChar(), dst4[2])

        buf.fill(0.toChar())
        val emojiE = byteArrayOf(0xF0.toByte(), 0x9F.toByte(), 0x93.toByte(), 0x8E.toByte(), 'e'.code.toByte())
        assertEquals(3, Mem.convertUtf8ToUtf16WithoutReplacement(emojiE, buf))
        assertEquals(0xD83D.toChar(), buf[0])
        assertEquals(0xDCCE.toChar(), buf[1])
        assertEquals('e', buf[2])

        buf.fill(0.toChar())
        val invalid = byteArrayOf(0xF0.toByte(), 0x9F.toByte(), 0x93.toByte())
        assertNull(Mem.convertUtf8ToUtf16WithoutReplacement(invalid, buf))
    }

    @Test
    fun testConvertUtf8ToLatin1LossyPanics() {
        val buf = ByteArray(1)
        assertFailsWith<IllegalArgumentException> {
            Mem.convertUtf8ToLatin1Lossy("ab".encodeToByteArray(), buf)
        }
    }

    @Test
    fun testConvertUtf16ToLatin1LossyPanics() {
        val buf = ByteArray(1)
        assertFailsWith<IllegalArgumentException> {
            Mem.convertUtf16ToLatin1Lossy(charArrayOf('b', 'c'), buf)
        }
    }
}
