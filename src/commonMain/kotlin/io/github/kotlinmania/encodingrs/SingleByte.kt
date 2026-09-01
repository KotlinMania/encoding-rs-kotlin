// port-lint: source single_byte.rs
package io.github.kotlinmania.encodingrs

public class SingleByteDecoder(
    public val table: CharArray,
) {
    public fun maxUtf16BufferLength(byteLength: Int): Int? = byteLength

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        if (byteLength > Int.MAX_VALUE / 3) null else byteLength * 3

    public fun maxUtf8BufferLength(byteLength: Int): Int? =
        if (byteLength > Int.MAX_VALUE / 3) null else byteLength * 3

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        if (last) Unit
        var srcPos = 0
        var dstPos = 0
        while (srcPos < src.size) {
            val b = src[srcPos].toInt() and 0xFF
            if (b < 0x80) {
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = b.toChar()
                srcPos++
            } else {
                val mapped = table[b - 0x80]
                if (mapped == '\u0000') {
                    return Triple(DecoderResult.Malformed(1, 0), srcPos + 1, dstPos)
                }
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = mapped
                srcPos++
            }
        }
        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public fun decodeToUtf8Raw(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        if (last) Unit
        var srcPos = 0
        var dstPos = 0
        while (srcPos < src.size) {
            val b = src[srcPos].toInt() and 0xFF
            if (b < 0x80) {
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = b.toByte()
                srcPos++
            } else {
                val mapped = table[b - 0x80]
                if (mapped == '\u0000') {
                    return Triple(DecoderResult.Malformed(1, 0), srcPos + 1, dstPos)
                }
                val code = mapped.code
                if (code <= 0x07FF) {
                    if (dstPos + 2 > dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = (0xC0 or (code shr 6)).toByte()
                    dst[dstPos++] = (0x80 or (code and 0x3F)).toByte()
                } else {
                    if (dstPos + 3 > dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = (0xE0 or (code shr 12)).toByte()
                    dst[dstPos++] = (0x80 or ((code shr 6) and 0x3F)).toByte()
                    dst[dstPos++] = (0x80 or (code and 0x3F)).toByte()
                }
                srcPos++
            }
        }
        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public fun latin1ByteCompatibleUpTo(buffer: ByteArray): Int {
        for (i in buffer.indices) {
            val b = buffer[i].toInt() and 0xFF
            if (b >= 0x80) {
                val mapped = table[b - 0x80]
                if (mapped.code != b) {
                    return i
                }
            }
        }
        return buffer.size
    }

    public companion object {
        public fun new(data: CharArray): SingleByteDecoder = SingleByteDecoder(data)
    }
}

public class SingleByteEncoder(
    public val table: CharArray,
    public val runBmpOffset: Int,
    public val runByteOffset: Int,
    public val runLength: Int,
) {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? = u16Length

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? = byteLength

    public fun encodeU16(codeUnit: Int): Byte? {
        if (codeUnit <= 0x7F) {
            return codeUnit.toByte()
        }
        val offset = codeUnit - runBmpOffset
        if (offset in 0 until runLength) {
            return (128 + runByteOffset + offset).toByte()
        }
        val tailStart = runByteOffset + runLength
        for (i in tailStart until 128) {
            if (table[i].code == codeUnit) {
                return (128 + i).toByte()
            }
        }
        if (runByteOffset >= 64) {
            for (i in 64 until runByteOffset) {
                if (table[i].code == codeUnit) {
                    return (128 + i).toByte()
                }
            }
            for (i in 32 until 64) {
                if (table[i].code == codeUnit) {
                    return (128 + i).toByte()
                }
            }
        } else {
            for (i in 32 until runByteOffset) {
                if (table[i].code == codeUnit) {
                    return (128 + i).toByte()
                }
            }
        }
        for (i in 0 until 32) {
            if (table[i].code == codeUnit) {
                return (128 + i).toByte()
            }
        }
        return null
    }

    public fun encodeU16(codeUnit: UShort): Byte? = encodeU16(codeUnit.toInt())

    public fun encodeChar(codeUnit: Char): Byte? = encodeU16(codeUnit.code)

    public fun encodeFromUtf16Raw(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0
        while (srcPos < src.size) {
            val c = src[srcPos]
            if (c.code <= 0x7F) {
                if (dstPos >= dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = c.code.toByte()
                srcPos++
                continue
            }
            val byte = encodeChar(c)
            if (byte != null) {
                if (dstPos >= dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = byte
                srcPos++
            } else {
                if (c.isHighSurrogate()) {
                    if (srcPos + 1 < src.size) {
                        val next = src[srcPos + 1]
                        if (next.isLowSurrogate()) {
                            val astral = ((c.code - 0xD800) shl 10) + (next.code - 0xDC00) + 0x10000
                            return Triple(EncoderResult.Unmappable(astral), srcPos + 2, dstPos)
                        } else {
                            return Triple(EncoderResult.Unmappable(0xFFFD), srcPos + 1, dstPos)
                        }
                    } else if (!last) {
                        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
                    } else {
                        return Triple(EncoderResult.Unmappable(0xFFFD), srcPos + 1, dstPos)
                    }
                } else if (c.isLowSurrogate()) {
                    return Triple(EncoderResult.Unmappable(0xFFFD), srcPos + 1, dstPos)
                }
                return Triple(EncoderResult.Unmappable(c.code), srcPos + 1, dstPos)
            }
        }
        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public fun encodeFromUtf8Raw(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0
        while (srcPos < src.size) {
            val b0 = src[srcPos].toInt() and 0xFF
            if (b0 < 0x80) {
                if (dstPos >= dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = b0.toByte()
                srcPos++
                continue
            }
            val needed: Int
            val codePoint: Int
            if (b0 in 0xC2..0xDF) {
                needed = 2
                if (srcPos + 2 > src.size) {
                    return if (last) Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos) else Triple(EncoderResult.InputEmpty, srcPos, dstPos)
                }
                val b1 = src[srcPos + 1].toInt() and 0xFF
                if (b1 !in 0x80..0xBF) {
                    return Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos)
                }
                codePoint = ((b0 and 0x1F) shl 6) or (b1 and 0x3F)
            } else if (b0 in 0xE0..0xEF) {
                needed = 3
                if (srcPos + 3 > src.size) {
                    return if (last) Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos) else Triple(EncoderResult.InputEmpty, srcPos, dstPos)
                }
                val b1 = src[srcPos + 1].toInt() and 0xFF
                val b2 = src[srcPos + 2].toInt() and 0xFF
                if (b1 !in 0x80..0xBF || b2 !in 0x80..0xBF) {
                    return Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos)
                }
                codePoint = ((b0 and 0x0F) shl 12) or ((b1 and 0x3F) shl 6) or (b2 and 0x3F)
            } else if (b0 in 0xF0..0xF4) {
                needed = 4
                if (srcPos + 4 > src.size) {
                    return if (last) Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos) else Triple(EncoderResult.InputEmpty, srcPos, dstPos)
                }
                val b1 = src[srcPos + 1].toInt() and 0xFF
                val b2 = src[srcPos + 2].toInt() and 0xFF
                val b3 = src[srcPos + 3].toInt() and 0xFF
                if (b1 !in 0x80..0xBF || b2 !in 0x80..0xBF || b3 !in 0x80..0xBF) {
                    return Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos)
                }
                codePoint = ((b0 and 0x07) shl 18) or ((b1 and 0x3F) shl 12) or ((b2 and 0x3F) shl 6) or (b3 and 0x3F)
            } else {
                return Triple(EncoderResult.Unmappable(0xFFFD), srcPos, dstPos)
            }

            if (codePoint > 0xFFFF) {
                return Triple(EncoderResult.Unmappable(codePoint), srcPos + needed, dstPos)
            }
            val byte = encodeChar(codePoint.toChar())
            if (byte != null) {
                if (dstPos >= dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = byte
                srcPos += needed
            } else {
                return Triple(EncoderResult.Unmappable(codePoint), srcPos + needed, dstPos)
            }
        }
        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(
            table: CharArray,
            runBmpOffset: Int,
            runByteOffset: Int,
            runLength: Int,
        ): SingleByteEncoder = SingleByteEncoder(table, runBmpOffset, runByteOffset, runLength)
    }
}
