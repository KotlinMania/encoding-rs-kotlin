// port-lint: tests encoding_rs/src/handles.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HandlesTest {
    @Test
    fun testUnalignedU16Slice() {
        val bytes = byteArrayOf(0x41, 0x00, 0x42, 0x00, 0x43, 0x00)
        val slice = UnalignedU16Slice(bytes, 0, 3)
        assertEquals(3, slice.len)
        assertEquals('A', slice.at(0))
        assertEquals('B', slice.at(1))
        assertEquals('C', slice.at(2))

        val dst = CharArray(3)
        val nonBmp = slice.copyBmpTo(dst)
        assertNull(nonBmp)
        assertEquals('A', dst[0])
        assertEquals('B', dst[1])
        assertEquals('C', dst[2])

        val tail = slice.tail(1)
        assertEquals(2, tail.len)
        assertEquals('B', tail.at(0))
        assertEquals('C', tail.at(1))
    }

    @Test
    fun testByteSourceAndDestination() {
        val bytes = byteArrayOf(1, 2, 3, 4, 5)
        val source = ByteSource(bytes)
        val handle1 = source.checkAvailable()
        assertTrue(handle1 is Space.Available)
        val (b1, unread1) = handle1.handle.read()
        assertEquals(1.toByte(), b1)
        assertEquals(1, source.consumed())
        unread1.unread()
        assertEquals(0, source.consumed())

        val dstBytes = ByteArray(10)
        val dest = ByteDestination(dstBytes)
        val destHandle = dest.checkSpaceThree()
        assertTrue(destHandle is Space.Available)
        destHandle.handle.writeThree(10.toByte(), 20.toByte(), 30.toByte())
        assertEquals(3, dest.written())
        assertEquals(10.toByte(), dstBytes[0])
        assertEquals(20.toByte(), dstBytes[1])
        assertEquals(30.toByte(), dstBytes[2])
    }

    @Test
    fun testUtf16DestinationAsciiCopy() {
        val srcBytes = "Hello, World!".encodeToByteArray() + byteArrayOf(0x80.toByte(), 'T'.code.toByte())
        val source = ByteSource(srcBytes)
        val dstChars = CharArray(32)
        val dest = Utf16Destination(dstChars)

        val result = dest.copyAsciiFromCheckSpaceBmp(source)
        assertTrue(result is CopyAsciiResult.GoOn)
        val (nonAsciiByte, _) = result.value
        assertEquals(0x80.toByte(), nonAsciiByte)
        assertEquals("Hello, World!", dstChars.copyOfRange(0, 13).concatToString())
    }

    @Test
    fun testUtf8DestinationAsciiCopy() {
        val srcBytes = "Hello, World!".encodeToByteArray() + byteArrayOf(0x80.toByte(), 'T'.code.toByte())
        val source = ByteSource(srcBytes)
        val dstBytes = ByteArray(32)
        val dest = Utf8Destination(dstBytes)

        val result = dest.copyAsciiFromCheckSpaceBmp(source)
        assertTrue(result is CopyAsciiResult.GoOn)
        val (nonAsciiByte, _) = result.value
        assertEquals(0x80.toByte(), nonAsciiByte)
        assertEquals("Hello, World!", dstBytes.copyOfRange(0, 13).decodeToString())
    }

    @Test
    fun testUtf16SourceAndDest() {
        val srcChars = "Hello\uD83D\uDCA9World".toCharArray()
        val source = Utf16Source(srcChars)
        val dstBytes = ByteArray(32)
        val dest = ByteDestination(dstBytes)

        val result = source.copyAsciiToCheckSpaceFour(dest)
        assertTrue(result is CopyAsciiResult.GoOn)
        val (nonAscii, _) = result.value
        assertTrue(nonAscii is NonAscii.Astral)
        assertEquals("Hello", dstBytes.copyOfRange(0, 5).decodeToString())
    }
}
