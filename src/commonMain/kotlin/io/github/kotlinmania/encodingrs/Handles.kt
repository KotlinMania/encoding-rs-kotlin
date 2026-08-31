// port-lint: source handles.rs
package io.github.kotlinmania.encodingrs

public sealed class Space<out T> {
    public data class Available<T>(
        public val handle: T,
    ) : Space<T>()

    public data class Full(
        public val consumedOrWritten: Int,
    ) : Space<Nothing>()
}

public sealed class CopyAsciiResult<out T, out U> {
    public data class Stop<T>(
        public val value: T,
    ) : CopyAsciiResult<T, Nothing>()

    public data class GoOn<U>(
        public val value: U,
    ) : CopyAsciiResult<Nothing, U>()
}

public sealed class NonAscii {
    public data class BmpExclAscii(
        public val bmp: Char,
    ) : NonAscii()

    public data class Astral(
        public val astral: Char,
    ) : NonAscii()
}

public sealed class Unicode {
    public data class Ascii(
        public val byte: Byte,
    ) : Unicode()

    public data class NonAsciiValue(
        public val nonAscii: NonAscii,
    ) : Unicode()
}

public interface Endian {
    public val oppositeEndian: Boolean
}

public data object BigEndian : Endian {
    override val oppositeEndian: Boolean = false
}

public data object LittleEndian : Endian {
    override val oppositeEndian: Boolean = false
}

public class UnalignedU16Slice(
    public val bytes: ByteArray,
    public var offset: Int,
    public var len: Int,
) {
    public fun trimLast() {
        require(len > 0)
        len -= 1
    }

    public fun at(i: Int): Char {
        require(i < len)
        val byteIndex = offset + i * 2
        val b0 = bytes[byteIndex].toInt() and 0xFF
        val b1 = bytes[byteIndex + 1].toInt() and 0xFF
        return ((b1 shl 8) or b0).toChar()
    }

    public fun tail(from: Int): UnalignedU16Slice {
        require(from <= len)
        return UnalignedU16Slice(bytes, offset + from * 2, len - from)
    }

    public fun simdAt(i: Int): Char = at(i)

    public fun copyBmpTo(
        other: CharArray,
        otherOffset: Int = 0,
        oppositeEndian: Boolean = false,
    ): Pair<Char, Int>? {
        require(len <= other.size - otherOffset)
        for (i in 0 until len) {
            var unit = at(i)
            if (oppositeEndian) {
                val code = unit.code
                unit = (((code and 0xFF) shl 8) or (code ushr 8)).toChar()
            }
            other[otherOffset + i] = unit
            if (unit.code in 0xD800..0xDFFF) {
                return Pair(unit, i)
            }
        }
        return null
    }

    public fun copyUnalignedBasicLatinToAsciiAlu(dst: ByteArray, dstOffset: Int = 0): Pair<Char, Int>? = null

    public fun swapIfOppositeEndian(unit: Char, oppositeEndian: Boolean): Char {
        if (oppositeEndian) {
            val code = unit.code
            return (((code and 0xFF) shl 8) or (code ushr 8)).toChar()
        }
        return unit
    }

    public fun copyUnalignedBasicLatinToAscii(dst: ByteArray, dstOffset: Int = 0): Pair<Char, Int>? = null

    public fun convertUnalignedUtf16ToUtf8(dst: ByteArray, dstOffset: Int = 0): Pair<Int, Int> = Pair(0, 0)

    public fun copyUtf16From(src: CharArray, srcOffset: Int = 0): Int = 0

    public companion object {
        public fun new(bytes: ByteArray, offset: Int = 0, len: Int = bytes.size / 2): UnalignedU16Slice =
            UnalignedU16Slice(bytes, offset, len)
    }
}

public class ByteSource(
    public val slice: ByteArray,
    public var pos: Int = 0,
) {
    public fun checkAvailable(): Space<ByteReadHandle> =
        if (pos < slice.size) Space.Available(ByteReadHandle(this)) else Space.Full(consumed())

    internal fun read(): Byte {
        val ret = slice[pos]
        pos += 1
        return ret
    }

    internal fun unread(): Int {
        pos -= 1
        return pos
    }

    public fun consumed(): Int = pos
}

public class ByteReadHandle(
    private val source: ByteSource,
) {
    public fun read(): Pair<Byte, ByteUnreadHandle> {
        val byte = source.read()
        return Pair(byte, ByteUnreadHandle(source))
    }

    public fun consumed(): Int = source.consumed()
}

public class ByteUnreadHandle(
    private val source: ByteSource,
) {
    public fun unread(): Int = source.unread()

    public fun consumed(): Int = source.consumed()

    public fun commit(): ByteSource = source
}

public class Utf16BmpHandle(
    private val dest: Utf16Destination,
) {
    public fun written(): Int = dest.written()

    public fun writeAscii(ascii: Byte): Utf16Destination {
        dest.writeAscii(ascii)
        return dest
    }

    public fun writeBmp(bmp: Char): Utf16Destination {
        dest.writeBmp(bmp)
        return dest
    }

    public fun writeBmpExclAscii(bmp: Char): Utf16Destination {
        dest.writeBmpExclAscii(bmp)
        return dest
    }

    public fun writeMidBmp(bmp: Char): Utf16Destination {
        dest.writeMidBmp(bmp)
        return dest
    }

    public fun writeUpperBmp(bmp: Char): Utf16Destination {
        dest.writeUpperBmp(bmp)
        return dest
    }

    public fun commit(): Utf16Destination = dest
}

public class Utf16AstralHandle(
    private val dest: Utf16Destination,
) {
    public fun written(): Int = dest.written()

    public fun writeAscii(ascii: Byte): Utf16Destination {
        dest.writeAscii(ascii)
        return dest
    }

    public fun writeBmp(bmp: Char): Utf16Destination {
        dest.writeBmp(bmp)
        return dest
    }

    public fun writeBmpExclAscii(bmp: Char): Utf16Destination {
        dest.writeBmpExclAscii(bmp)
        return dest
    }

    public fun writeUpperBmp(bmp: Char): Utf16Destination {
        dest.writeUpperBmp(bmp)
        return dest
    }

    public fun writeAstral(astral: Int): Utf16Destination {
        dest.writeAstral(astral)
        return dest
    }

    public fun writeSurrogatePair(high: Char, low: Char): Utf16Destination {
        dest.writeSurrogatePair(high, low)
        return dest
    }

    public fun writeBig5Combination(combined: Char, combining: Char): Utf16Destination {
        dest.writeBig5Combination(combined, combining)
        return dest
    }

    public fun commit(): Utf16Destination = dest
}

public class Utf16Destination(
    public val slice: CharArray,
    public var pos: Int = 0,
) {
    public fun checkSpaceBmp(): Space<Utf16BmpHandle> =
        if (pos < slice.size) Space.Available(Utf16BmpHandle(this)) else Space.Full(written())

    public fun checkSpaceAstral(): Space<Utf16AstralHandle> =
        if (pos + 1 < slice.size) Space.Available(Utf16AstralHandle(this)) else Space.Full(written())

    public fun written(): Int = pos

    internal fun writeCodeUnit(u: Char) {
        slice[pos] = u
        pos += 1
    }

    internal fun writeAscii(ascii: Byte) {
        writeCodeUnit((ascii.toInt() and 0xFF).toChar())
    }

    internal fun writeBmp(bmp: Char) {
        writeCodeUnit(bmp)
    }

    internal fun writeBmpExclAscii(bmp: Char) {
        writeCodeUnit(bmp)
    }

    internal fun writeMidBmp(bmp: Char) {
        writeCodeUnit(bmp)
    }

    internal fun writeUpperBmp(bmp: Char) {
        writeCodeUnit(bmp)
    }

    internal fun writeAstral(astral: Int) {
        writeCodeUnit((0xD7C0 + (astral shr 10)).toChar())
        writeCodeUnit((0xDC00 + (astral and 0x3FF)).toChar())
    }

    internal fun writeSurrogatePair(high: Char, low: Char) {
        writeCodeUnit(high)
        writeCodeUnit(low)
    }

    internal fun writeBig5Combination(combined: Char, combining: Char) {
        writeBmpExclAscii(combined)
        writeBmpExclAscii(combining)
    }

    public fun copyAsciiFromCheckSpaceBmp(
        source: ByteSource,
    ): CopyAsciiResult<Triple<DecoderResult, Int, Int>, Pair<Byte, Utf16BmpHandle>> {
        val srcRem = source.slice.size - source.pos
        val dstRem = slice.size - pos
        val pending = if (dstRem < srcRem) DecoderResult.OutputFull else DecoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = source.slice[source.pos + i]
            if ((b.toInt() and 0xFF) >= 0x80) {
                source.pos += i + 1
                pos += i
                return CopyAsciiResult.GoOn(Pair(b, Utf16BmpHandle(this)))
            }
            slice[pos + i] = (b.toInt() and 0xFF).toChar()
        }
        source.pos += length
        pos += length
        return CopyAsciiResult.Stop(Triple(pending, source.pos, pos))
    }

    public fun copyAsciiFromCheckSpaceAstral(
        source: ByteSource,
    ): CopyAsciiResult<Triple<DecoderResult, Int, Int>, Pair<Byte, Utf16AstralHandle>> {
        val srcRem = source.slice.size - source.pos
        val dstRem = slice.size - pos
        val pending = if (dstRem < srcRem) DecoderResult.OutputFull else DecoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = source.slice[source.pos + i]
            if ((b.toInt() and 0xFF) >= 0x80) {
                source.pos += i
                pos += i
                if (pos + 1 < slice.size) {
                    source.pos += 1
                    return CopyAsciiResult.GoOn(Pair(b, Utf16AstralHandle(this)))
                } else {
                    return CopyAsciiResult.Stop(Triple(DecoderResult.OutputFull, source.pos, pos))
                }
            }
            slice[pos + i] = (b.toInt() and 0xFF).toChar()
        }
        source.pos += length
        pos += length
        return CopyAsciiResult.Stop(Triple(pending, source.pos, pos))
    }

    public fun copyUtf8UpToInvalidFrom(source: ByteSource) {
        val srcRemaining = source.slice.copyOfRange(source.pos, source.slice.size)
        val dstRemaining = CharArray(slice.size - pos)
        val (read, written) = Utf8.convertUtf8ToUtf16UpToInvalid(srcRemaining, dstRemaining)
        dstRemaining.copyInto(slice, pos, 0, written)
        source.pos += read
        pos += written
    }
}

public class Utf8BmpHandle(
    private val dest: Utf8Destination,
) {
    public fun written(): Int = dest.written()

    public fun writeAscii(ascii: Byte): Utf8Destination {
        dest.writeAscii(ascii)
        return dest
    }

    public fun writeBmp(bmp: Char): Utf8Destination {
        dest.writeBmp(bmp)
        return dest
    }

    public fun writeBmpExclAscii(bmp: Char): Utf8Destination {
        dest.writeBmpExclAscii(bmp)
        return dest
    }

    public fun writeMidBmp(bmp: Char): Utf8Destination {
        dest.writeMidBmp(bmp)
        return dest
    }

    public fun writeUpperBmp(bmp: Char): Utf8Destination {
        dest.writeUpperBmp(bmp)
        return dest
    }

    public fun commit(): Utf8Destination = dest
}

public class Utf8AstralHandle(
    private val dest: Utf8Destination,
) {
    public fun written(): Int = dest.written()

    public fun writeAscii(ascii: Byte): Utf8Destination {
        dest.writeAscii(ascii)
        return dest
    }

    public fun writeBmp(bmp: Char): Utf8Destination {
        dest.writeBmp(bmp)
        return dest
    }

    public fun writeBmpExclAscii(bmp: Char): Utf8Destination {
        dest.writeBmpExclAscii(bmp)
        return dest
    }

    public fun writeUpperBmp(bmp: Char): Utf8Destination {
        dest.writeUpperBmp(bmp)
        return dest
    }

    public fun writeAstral(astral: Int): Utf8Destination {
        dest.writeAstral(astral)
        return dest
    }

    public fun writeSurrogatePair(high: Char, low: Char): Utf8Destination {
        dest.writeSurrogatePair(high, low)
        return dest
    }

    public fun writeBig5Combination(combined: Char, combining: Char): Utf8Destination {
        dest.writeBig5Combination(combined, combining)
        return dest
    }

    public fun commit(): Utf8Destination = dest
}

public class Utf8Destination(
    public val slice: ByteArray,
    public var pos: Int = 0,
) {
    public fun checkSpaceBmp(): Space<Utf8BmpHandle> =
        if (pos + 2 < slice.size) Space.Available(Utf8BmpHandle(this)) else Space.Full(written())

    public fun checkSpaceAstral(): Space<Utf8AstralHandle> =
        if (pos + 3 < slice.size) Space.Available(Utf8AstralHandle(this)) else Space.Full(written())

    public fun written(): Int = pos

    internal fun writeCodeUnit(u: Byte) {
        slice[pos] = u
        pos += 1
    }

    internal fun writeAscii(ascii: Byte) {
        writeCodeUnit(ascii)
    }

    internal fun writeBmp(bmp: Char) {
        val code = bmp.code
        if (code < 0x80) {
            writeAscii(code.toByte())
        } else if (code < 0x800) {
            writeMidBmp(bmp)
        } else {
            writeUpperBmp(bmp)
        }
    }

    internal fun writeMidBmp(midBmp: Char) {
        val code = midBmp.code
        writeCodeUnit(((code shr 6) or 0xC0).toByte())
        writeCodeUnit(((code and 0x3F) or 0x80).toByte())
    }

    internal fun writeUpperBmp(upperBmp: Char) {
        val code = upperBmp.code
        writeCodeUnit(((code shr 12) or 0xE0).toByte())
        writeCodeUnit((((code and 0xFC0) shr 6) or 0x80).toByte())
        writeCodeUnit(((code and 0x3F) or 0x80).toByte())
    }

    internal fun writeBmpExclAscii(bmp: Char) {
        if (bmp.code < 0x800) {
            writeMidBmp(bmp)
        } else {
            writeUpperBmp(bmp)
        }
    }

    internal fun writeAstral(astral: Int) {
        writeCodeUnit(((astral shr 18) or 0xF0).toByte())
        writeCodeUnit((((astral and 0x3F000) shr 12) or 0x80).toByte())
        writeCodeUnit((((astral and 0xFC0) shr 6) or 0x80).toByte())
        writeCodeUnit(((astral and 0x3F) or 0x80).toByte())
    }

    public fun writeSurrogatePair(high: Char, low: Char) {
        val codePoint = 0x10000 + ((high.code - 0xD800) shl 10) + (low.code - 0xDC00)
        writeAstral(codePoint)
    }

    internal fun writeBig5Combination(combined: Char, combining: Char) {
        writeMidBmp(combined)
        writeMidBmp(combining)
    }

    public fun copyAsciiFromCheckSpaceBmp(
        source: ByteSource,
    ): CopyAsciiResult<Triple<DecoderResult, Int, Int>, Pair<Byte, Utf8BmpHandle>> {
        val srcRem = source.slice.size - source.pos
        val dstRem = slice.size - pos
        val pending = if (dstRem < srcRem) DecoderResult.OutputFull else DecoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = source.slice[source.pos + i]
            if ((b.toInt() and 0xFF) >= 0x80) {
                source.pos += i
                pos += i
                if (pos + 2 < slice.size) {
                    source.pos += 1
                    return CopyAsciiResult.GoOn(Pair(b, Utf8BmpHandle(this)))
                } else {
                    return CopyAsciiResult.Stop(Triple(DecoderResult.OutputFull, source.pos, pos))
                }
            }
            slice[pos + i] = b
        }
        source.pos += length
        pos += length
        return CopyAsciiResult.Stop(Triple(pending, source.pos, pos))
    }

    public fun copyAsciiFromCheckSpaceAstral(
        source: ByteSource,
    ): CopyAsciiResult<Triple<DecoderResult, Int, Int>, Pair<Byte, Utf8AstralHandle>> {
        val srcRem = source.slice.size - source.pos
        val dstRem = slice.size - pos
        val pending = if (dstRem < srcRem) DecoderResult.OutputFull else DecoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = source.slice[source.pos + i]
            if ((b.toInt() and 0xFF) >= 0x80) {
                source.pos += i
                pos += i
                if (pos + 3 < slice.size) {
                    source.pos += 1
                    return CopyAsciiResult.GoOn(Pair(b, Utf8AstralHandle(this)))
                } else {
                    return CopyAsciiResult.Stop(Triple(DecoderResult.OutputFull, source.pos, pos))
                }
            }
            slice[pos + i] = b
        }
        source.pos += length
        pos += length
        return CopyAsciiResult.Stop(Triple(pending, source.pos, pos))
    }

    public fun copyUtf8UpToInvalidFrom(source: ByteSource) {
        val minLen = minOf(source.slice.size - source.pos, slice.size - pos)
        val validLen = Utf8.utf8ValidUpTo(source.slice.copyOfRange(source.pos, source.pos + minLen))
        source.slice.copyInto(slice, pos, source.pos, source.pos + validLen)
        source.pos += validLen
        pos += validLen
    }
}

public class Utf16Source(
    public val slice: CharArray,
    public var pos: Int = 0,
    public var oldPos: Int = 0,
) {
    public fun checkAvailable(): Space<Utf16ReadHandle> =
        if (pos < slice.size) Space.Available(Utf16ReadHandle(this)) else Space.Full(consumed())

    internal fun read(): Char {
        oldPos = pos
        val unit = slice[pos++]
        if (unit.isHighSurrogate()) {
            if (pos < slice.size) {
                val second = slice[pos]
                if (second.isLowSurrogate()) {
                    pos += 1
                    return unit
                }
            }
        }
        return unit
    }

    internal fun readEnum(): Unicode {
        oldPos = pos
        val unit = slice[pos++]
        val code = unit.code
        if (code < 0x80) {
            return Unicode.Ascii(code.toByte())
        }
        if (unit.isHighSurrogate()) {
            if (pos < slice.size) {
                val second = slice[pos]
                if (second.isLowSurrogate()) {
                    pos += 1
                    return Unicode.NonAsciiValue(NonAscii.Astral(second))
                }
            }
            return Unicode.NonAsciiValue(NonAscii.BmpExclAscii('\uFFFD'))
        }
        if (unit.isLowSurrogate()) {
            return Unicode.NonAsciiValue(NonAscii.BmpExclAscii('\uFFFD'))
        }
        return Unicode.NonAsciiValue(NonAscii.BmpExclAscii(unit))
    }

    internal fun unread(): Int {
        pos = oldPos
        return pos
    }

    public fun consumed(): Int = pos

    public fun copyAsciiToCheckSpaceTwo(
        dest: ByteDestination,
    ): CopyAsciiResult<Triple<EncoderResult, Int, Int>, Pair<NonAscii, ByteTwoHandle>> {
        val srcRem = slice.size - pos
        val dstRem = dest.slice.size - dest.pos
        val pending = if (dstRem < srcRem) EncoderResult.OutputFull else EncoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val c = slice[pos + i]
            if (c.code > 0x7F) {
                pos += i
                dest.pos += i
                if (dest.pos + 1 < dest.slice.size) {
                    pos += 1
                    val nonAscii =
                        if (c.isHighSurrogate()) {
                            if (pos < slice.size && slice[pos].isLowSurrogate()) {
                                val second = slice[pos++]
                                NonAscii.Astral(second)
                            } else {
                                NonAscii.BmpExclAscii('\uFFFD')
                            }
                        } else if (c.isLowSurrogate()) {
                            NonAscii.BmpExclAscii('\uFFFD')
                        } else {
                            NonAscii.BmpExclAscii(c)
                        }
                    return CopyAsciiResult.GoOn(Pair(nonAscii, ByteTwoHandle(dest)))
                } else {
                    return CopyAsciiResult.Stop(Triple(EncoderResult.OutputFull, pos, dest.pos))
                }
            }
            dest.slice[dest.pos + i] = c.code.toByte()
        }
        pos += length
        dest.pos += length
        return CopyAsciiResult.Stop(Triple(pending, pos, dest.pos))
    }

    public fun copyAsciiToCheckSpaceFour(
        dest: ByteDestination,
    ): CopyAsciiResult<Triple<EncoderResult, Int, Int>, Pair<NonAscii, ByteFourHandle>> {
        val srcRem = slice.size - pos
        val dstRem = dest.slice.size - dest.pos
        val pending = if (dstRem < srcRem) EncoderResult.OutputFull else EncoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val c = slice[pos + i]
            if (c.code > 0x7F) {
                pos += i
                dest.pos += i
                if (dest.pos + 3 < dest.slice.size) {
                    pos += 1
                    val nonAscii =
                        if (c.isHighSurrogate()) {
                            if (pos < slice.size && slice[pos].isLowSurrogate()) {
                                val second = slice[pos++]
                                NonAscii.Astral(second)
                            } else {
                                NonAscii.BmpExclAscii('\uFFFD')
                            }
                        } else if (c.isLowSurrogate()) {
                            NonAscii.BmpExclAscii('\uFFFD')
                        } else {
                            NonAscii.BmpExclAscii(c)
                        }
                    return CopyAsciiResult.GoOn(Pair(nonAscii, ByteFourHandle(dest)))
                } else {
                    return CopyAsciiResult.Stop(Triple(EncoderResult.OutputFull, pos, dest.pos))
                }
            }
            dest.slice[dest.pos + i] = c.code.toByte()
        }
        pos += length
        dest.pos += length
        return CopyAsciiResult.Stop(Triple(pending, pos, dest.pos))
    }
}

public class Utf16ReadHandle(
    private val source: Utf16Source,
) {
    public fun read(): Pair<Char, Utf16UnreadHandle> = Pair(source.read(), Utf16UnreadHandle(source))

    public fun readEnum(): Pair<Unicode, Utf16UnreadHandle> = Pair(source.readEnum(), Utf16UnreadHandle(source))

    public fun consumed(): Int = source.consumed()
}

public class Utf16UnreadHandle(
    private val source: Utf16Source,
) {
    public fun unread(): Int = source.unread()

    public fun consumed(): Int = source.consumed()

    public fun commit(): Utf16Source = source
}

public class Utf8Source(
    public val slice: ByteArray,
    public var pos: Int = 0,
    public var oldPos: Int = 0,
) {
    public constructor(src: String) : this(src.encodeToByteArray(), 0, 0)

    public fun checkAvailable(): Space<Utf8ReadHandle> =
        if (pos < slice.size) Space.Available(Utf8ReadHandle(this)) else Space.Full(consumed())

    internal fun read(): Char {
        oldPos = pos
        val unit = slice[pos++].toInt() and 0xFF
        if (unit < 0x80) {
            return unit.toChar()
        }
        if (unit < 0xE0) {
            val point = ((unit and 0x1F) shl 6) or (slice[pos++].toInt() and 0x3F)
            return point.toChar()
        }
        if (unit < 0xF0) {
            val point =
                ((unit and 0xF) shl 12) or
                    ((slice[pos++].toInt() and 0x3F) shl 6) or
                    (slice[pos++].toInt() and 0x3F)
            return point.toChar()
        }
        val point =
            ((unit and 0x7) shl 18) or
                ((slice[pos++].toInt() and 0x3F) shl 12) or
                ((slice[pos++].toInt() and 0x3F) shl 6) or
                (slice[pos++].toInt() and 0x3F)
        return point.toChar()
    }

    internal fun readEnum(): Unicode {
        oldPos = pos
        val unit = slice[pos++]
        val b = unit.toInt() and 0xFF
        if (b < 0x80) {
            return Unicode.Ascii(unit)
        }
        if (b < 0xE0) {
            val point = ((b and 0x1F) shl 6) or (slice[pos++].toInt() and 0x3F)
            return Unicode.NonAsciiValue(NonAscii.BmpExclAscii(point.toChar()))
        }
        if (b < 0xF0) {
            val point =
                ((b and 0xF) shl 12) or
                    ((slice[pos++].toInt() and 0x3F) shl 6) or
                    (slice[pos++].toInt() and 0x3F)
            return Unicode.NonAsciiValue(NonAscii.BmpExclAscii(point.toChar()))
        }
        val point =
            ((b and 0x7) shl 18) or
                ((slice[pos++].toInt() and 0x3F) shl 12) or
                ((slice[pos++].toInt() and 0x3F) shl 6) or
                (slice[pos++].toInt() and 0x3F)
        return Unicode.NonAsciiValue(NonAscii.Astral(point.toChar()))
    }

    internal fun unread(): Int {
        pos = oldPos
        return pos
    }

    public fun consumed(): Int = pos

    public fun copyAsciiToCheckSpaceOne(
        dest: ByteDestination,
    ): CopyAsciiResult<Triple<EncoderResult, Int, Int>, Pair<NonAscii, ByteOneHandle>> {
        val srcRem = slice.size - pos
        val dstRem = dest.slice.size - dest.pos
        val pending = if (dstRem < srcRem) EncoderResult.OutputFull else EncoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = slice[pos + i].toInt() and 0xFF
            if (b >= 0x80) {
                pos += i
                dest.pos += i
                val nonAscii =
                    if (b < 0xE0) {
                        val point = ((b and 0x1F) shl 6) or (slice[pos + 1].toInt() and 0x3F)
                        pos += 2
                        NonAscii.BmpExclAscii(point.toChar())
                    } else if (b < 0xF0) {
                        val point =
                            ((b and 0xF) shl 12) or
                                ((slice[pos + 1].toInt() and 0x3F) shl 6) or
                                (slice[pos + 2].toInt() and 0x3F)
                        pos += 3
                        NonAscii.BmpExclAscii(point.toChar())
                    } else {
                        val point =
                            ((b and 0x7) shl 18) or
                                ((slice[pos + 1].toInt() and 0x3F) shl 12) or
                                ((slice[pos + 2].toInt() and 0x3F) shl 6) or
                                (slice[pos + 3].toInt() and 0x3F)
                        pos += 4
                        NonAscii.Astral(point.toChar())
                    }
                return CopyAsciiResult.GoOn(Pair(nonAscii, ByteOneHandle(dest)))
            }
            dest.slice[dest.pos + i] = b.toByte()
        }
        pos += length
        dest.pos += length
        return CopyAsciiResult.Stop(Triple(pending, pos, dest.pos))
    }

    public fun copyAsciiToCheckSpaceTwo(
        dest: ByteDestination,
    ): CopyAsciiResult<Triple<EncoderResult, Int, Int>, Pair<NonAscii, ByteTwoHandle>> {
        val srcRem = slice.size - pos
        val dstRem = dest.slice.size - dest.pos
        val pending = if (dstRem < srcRem) EncoderResult.OutputFull else EncoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = slice[pos + i].toInt() and 0xFF
            if (b >= 0x80) {
                pos += i
                dest.pos += i
                if (dest.pos + 1 < dest.slice.size) {
                    val nonAscii =
                        if (b < 0xE0) {
                            val point = ((b and 0x1F) shl 6) or (slice[pos + 1].toInt() and 0x3F)
                            pos += 2
                            NonAscii.BmpExclAscii(point.toChar())
                        } else if (b < 0xF0) {
                            val point =
                                ((b and 0xF) shl 12) or
                                    ((slice[pos + 1].toInt() and 0x3F) shl 6) or
                                    (slice[pos + 2].toInt() and 0x3F)
                            pos += 3
                            NonAscii.BmpExclAscii(point.toChar())
                        } else {
                            val point =
                                ((b and 0x7) shl 18) or
                                    ((slice[pos + 1].toInt() and 0x3F) shl 12) or
                                    ((slice[pos + 2].toInt() and 0x3F) shl 6) or
                                    (slice[pos + 3].toInt() and 0x3F)
                            pos += 4
                            NonAscii.Astral(point.toChar())
                        }
                    return CopyAsciiResult.GoOn(Pair(nonAscii, ByteTwoHandle(dest)))
                } else {
                    return CopyAsciiResult.Stop(Triple(EncoderResult.OutputFull, pos, dest.pos))
                }
            }
            dest.slice[dest.pos + i] = b.toByte()
        }
        pos += length
        dest.pos += length
        return CopyAsciiResult.Stop(Triple(pending, pos, dest.pos))
    }

    public fun copyAsciiToCheckSpaceFour(
        dest: ByteDestination,
    ): CopyAsciiResult<Triple<EncoderResult, Int, Int>, Pair<NonAscii, ByteFourHandle>> {
        val srcRem = slice.size - pos
        val dstRem = dest.slice.size - dest.pos
        val pending = if (dstRem < srcRem) EncoderResult.OutputFull else EncoderResult.InputEmpty
        val length = minOf(srcRem, dstRem)
        for (i in 0 until length) {
            val b = slice[pos + i].toInt() and 0xFF
            if (b >= 0x80) {
                pos += i
                dest.pos += i
                if (dest.pos + 3 < dest.slice.size) {
                    val nonAscii =
                        if (b < 0xE0) {
                            val point = ((b and 0x1F) shl 6) or (slice[pos + 1].toInt() and 0x3F)
                            pos += 2
                            NonAscii.BmpExclAscii(point.toChar())
                        } else if (b < 0xF0) {
                            val point =
                                ((b and 0xF) shl 12) or
                                    ((slice[pos + 1].toInt() and 0x3F) shl 6) or
                                    (slice[pos + 2].toInt() and 0x3F)
                            pos += 3
                            NonAscii.BmpExclAscii(point.toChar())
                        } else {
                            val point =
                                ((b and 0x7) shl 18) or
                                    ((slice[pos + 1].toInt() and 0x3F) shl 12) or
                                    ((slice[pos + 2].toInt() and 0x3F) shl 6) or
                                    (slice[pos + 3].toInt() and 0x3F)
                            pos += 4
                            NonAscii.Astral(point.toChar())
                        }
                    return CopyAsciiResult.GoOn(Pair(nonAscii, ByteFourHandle(dest)))
                } else {
                    return CopyAsciiResult.Stop(Triple(EncoderResult.OutputFull, pos, dest.pos))
                }
            }
            dest.slice[dest.pos + i] = b.toByte()
        }
        pos += length
        dest.pos += length
        return CopyAsciiResult.Stop(Triple(pending, pos, dest.pos))
    }
}

public class Utf8ReadHandle(
    private val source: Utf8Source,
) {
    public fun read(): Pair<Char, Utf8UnreadHandle> = Pair(source.read(), Utf8UnreadHandle(source))

    public fun readEnum(): Pair<Unicode, Utf8UnreadHandle> = Pair(source.readEnum(), Utf8UnreadHandle(source))

    public fun consumed(): Int = source.consumed()
}

public class Utf8UnreadHandle(
    private val source: Utf8Source,
) {
    public fun unread(): Int = source.unread()

    public fun consumed(): Int = source.consumed()

    public fun commit(): Utf8Source = source
}

public class ByteOneHandle(
    private val dest: ByteDestination,
) {
    public fun written(): Int = dest.written()

    public fun writeOne(first: Byte): ByteDestination {
        dest.writeOne(first)
        return dest
    }
}

public class ByteTwoHandle(
    private val dest: ByteDestination,
) {
    public fun written(): Int = dest.written()

    public fun writeOne(first: Byte): ByteDestination {
        dest.writeOne(first)
        return dest
    }

    public fun writeTwo(first: Byte, second: Byte): ByteDestination {
        dest.writeTwo(first, second)
        return dest
    }
}

public class ByteThreeHandle(
    private val dest: ByteDestination,
) {
    public fun written(): Int = dest.written()

    public fun writeOne(first: Byte): ByteDestination {
        dest.writeOne(first)
        return dest
    }

    public fun writeTwo(first: Byte, second: Byte): ByteDestination {
        dest.writeTwo(first, second)
        return dest
    }

    public fun writeThree(first: Byte, second: Byte, third: Byte): ByteDestination {
        dest.writeThree(first, second, third)
        return dest
    }

    public fun writeThreeReturnWritten(first: Byte, second: Byte, third: Byte): Int {
        dest.writeThree(first, second, third)
        return dest.written()
    }
}

public class ByteFourHandle(
    private val dest: ByteDestination,
) {
    public fun written(): Int = dest.written()

    public fun writeOne(first: Byte): ByteDestination {
        dest.writeOne(first)
        return dest
    }

    public fun writeTwo(first: Byte, second: Byte): ByteDestination {
        dest.writeTwo(first, second)
        return dest
    }

    public fun writeFour(first: Byte, second: Byte, third: Byte, fourth: Byte): ByteDestination {
        dest.writeFour(first, second, third, fourth)
        return dest
    }
}

public class ByteDestination(
    public val slice: ByteArray,
    public var pos: Int = 0,
) {
    public fun checkSpaceOne(): Space<ByteOneHandle> =
        if (pos < slice.size) Space.Available(ByteOneHandle(this)) else Space.Full(written())

    public fun checkSpaceTwo(): Space<ByteTwoHandle> =
        if (pos + 1 < slice.size) Space.Available(ByteTwoHandle(this)) else Space.Full(written())

    public fun checkSpaceThree(): Space<ByteThreeHandle> =
        if (pos + 2 < slice.size) Space.Available(ByteThreeHandle(this)) else Space.Full(written())

    public fun checkSpaceFour(): Space<ByteFourHandle> =
        if (pos + 3 < slice.size) Space.Available(ByteFourHandle(this)) else Space.Full(written())

    public fun written(): Int = pos

    internal fun writeOne(first: Byte) {
        slice[pos] = first
        pos += 1
    }

    internal fun writeTwo(first: Byte, second: Byte) {
        slice[pos] = first
        slice[pos + 1] = second
        pos += 2
    }

    internal fun writeThree(first: Byte, second: Byte, third: Byte) {
        slice[pos] = first
        slice[pos + 1] = second
        slice[pos + 2] = third
        pos += 3
    }

    internal fun writeFour(first: Byte, second: Byte, third: Byte, fourth: Byte) {
        slice[pos] = first
        slice[pos + 1] = second
        slice[pos + 2] = third
        slice[pos + 3] = fourth
        pos += 4
    }
}
