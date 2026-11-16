// port-lint: source utf_8.rs
package io.github.kotlinmania.encodingrs

public object Utf8 {
    private val UTF8_DATA_TABLE: IntArray =
        intArrayOf(
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            84,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            148,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            164,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            252,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            16,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            8,
            32,
            8,
            8,
            64,
            8,
            8,
            8,
            128,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
            4,
        )

    public fun utf8ValidUpTo(src: ByteArray): Int {
        var read = 0
        while (read < src.size) {
            val b = src[read].toInt() and 0xFF
            if (b < 0x80) {
                read++
                continue
            }
            if (b in 0xC2..0xDF) {
                if (read + 1 >= src.size) break
                val second = src[read + 1].toInt() and 0xFF
                if (second !in 0x80..0xBF) break
                read += 2
                continue
            }
            if (b < 0xF0) {
                if (read + 2 >= src.size) break
                val second = src[read + 1].toInt() and 0xFF
                val third = src[read + 2].toInt() and 0xFF
                val tableVal = UTF8_DATA_TABLE[second] and UTF8_DATA_TABLE[b + 0x80]
                if ((tableVal or (third shr 6)) != 2) break
                read += 3
                continue
            }
            if (b in 0xF0..0xF4) {
                if (read + 3 >= src.size) break
                val second = src[read + 1].toInt() and 0xFF
                val third = src[read + 2].toInt() and 0xFF
                val fourth = src[read + 3].toInt() and 0xFF
                val tableVal = UTF8_DATA_TABLE[second] and UTF8_DATA_TABLE[b + 0x80]
                val check = tableVal or (third shr 6) or ((fourth and 0xC0) shl 2)
                if (check != 0x202) break
                read += 4
                continue
            }
            break
        }
        return read
    }

    public fun convertUtf8ToUtf16UpToInvalid(src: ByteArray, dst: CharArray): Pair<Int, Int> {
        var read = 0
        var written = 0
        while (read < src.size && written < dst.size) {
            val b = src[read].toInt() and 0xFF
            if (b < 0x80) {
                dst[written++] = b.toChar()
                read++
                continue
            }
            if (b in 0xC2..0xDF) {
                if (read + 1 >= src.size) break
                val second = src[read + 1].toInt() and 0xFF
                if (second !in 0x80..0xBF) break
                val codeUnit = ((b and 0x1F) shl 6) or (second and 0x3F)
                dst[written++] = codeUnit.toChar()
                read += 2
                continue
            }
            if (b < 0xF0) {
                if (read + 2 >= src.size) break
                val second = src[read + 1].toInt() and 0xFF
                val third = src[read + 2].toInt() and 0xFF
                val tableVal = UTF8_DATA_TABLE[second] and UTF8_DATA_TABLE[b + 0x80]
                if ((tableVal or (third shr 6)) != 2) break
                val point = ((b and 0xF) shl 12) or ((second and 0x3F) shl 6) or (third and 0x3F)
                dst[written++] = point.toChar()
                read += 3
                continue
            }
            if (b in 0xF0..0xF4) {
                if (read + 3 >= src.size || written + 1 >= dst.size) break
                val second = src[read + 1].toInt() and 0xFF
                val third = src[read + 2].toInt() and 0xFF
                val fourth = src[read + 3].toInt() and 0xFF
                val tableVal = UTF8_DATA_TABLE[second] and UTF8_DATA_TABLE[b + 0x80]
                val check = tableVal or (third shr 6) or ((fourth and 0xC0) shl 2)
                if (check != 0x202) break
                val point = ((b and 0x7) shl 18) or ((second and 0x3F) shl 12) or ((third and 0x3F) shl 6) or (fourth and 0x3F)
                dst[written++] = (0xD7C0 + (point shr 10)).toChar()
                dst[written++] = (0xDC00 + (point and 0x3FF)).toChar()
                read += 4
                continue
            }
            break
        }
        return Pair(read, written)
    }

    public fun decodeUtf8ToUtf16(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        val decoder = Utf8Decoder()
        return decoder.decodeToUtf16Raw(src, dst, last)
    }
}

class Utf8Decoder {
    private var codePoint: Int = 0
    private var bytesSeen: Int = 0
    private var bytesNeeded: Int = 0
    private var lowerBoundary: Int = 0x80
    private var upperBoundary: Int = 0xBF

    public fun inNeutralState(): Boolean = bytesNeeded == 0

    private fun extraFromState(byteLength: Int): Int =
        byteLength + (if (bytesNeeded == 0) 0 else bytesSeen + 1)

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        1 + extraFromState(byteLength)

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        3 + extraFromState(byteLength)

    public fun maxUtf8BufferLength(byteLength: Int): Int? =
        3 + 3 * extraFromState(byteLength)

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        if (bytesNeeded == 0) {
            val (fastRead, fastWritten) = Utf8.convertUtf8ToUtf16UpToInvalid(src, dst)
            srcPos += fastRead
            dstPos += fastWritten
        }

        while (srcPos < src.size) {
            val b = src[srcPos++].toInt() and 0xFF
            if (bytesNeeded == 0) {
                if (b < 0x80) {
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos - 1, dstPos)
                    }
                    dst[dstPos++] = b.toChar()
                    continue
                }
                if (b < 0xC2) {
                    return Triple(DecoderResult.Malformed(1u, 0u), srcPos, dstPos)
                }
                if (b < 0xE0) {
                    bytesNeeded = 1
                    codePoint = b and 0x1F
                    continue
                }
                if (b < 0xF0) {
                    if (b == 0xE0) {
                        lowerBoundary = 0xA0
                    } else if (b == 0xED) {
                        upperBoundary = 0x9F
                    }
                    bytesNeeded = 2
                    codePoint = b and 0xF
                    continue
                }
                if (b < 0xF5) {
                    if (b == 0xF0) {
                        lowerBoundary = 0x90
                    } else if (b == 0xF4) {
                        upperBoundary = 0x8F
                    }
                    bytesNeeded = 3
                    codePoint = b and 0x7
                    continue
                }
                return Triple(DecoderResult.Malformed(1u, 0u), srcPos, dstPos)
            }

            if (b !in lowerBoundary..upperBoundary) {
                val badBytes = (bytesSeen + 1).toUByte()
                codePoint = 0
                bytesNeeded = 0
                bytesSeen = 0
                lowerBoundary = 0x80
                upperBoundary = 0xBF
                return Triple(DecoderResult.Malformed(badBytes, 0u), srcPos - 1, dstPos)
            }

            lowerBoundary = 0x80
            upperBoundary = 0xBF
            codePoint = (codePoint shl 6) or (b and 0x3F)
            bytesSeen++

            if (bytesSeen != bytesNeeded) {
                continue
            }

            if (bytesNeeded == 3) {
                if (dstPos + 2 > dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos - 4, dstPos)
                }
                dst[dstPos++] = (0xD7C0 + (codePoint shr 10)).toChar()
                dst[dstPos++] = (0xDC00 + (codePoint and 0x3FF)).toChar()
            } else {
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos - (bytesNeeded + 1), dstPos)
                }
                dst[dstPos++] = codePoint.toChar()
            }

            codePoint = 0
            bytesNeeded = 0
            bytesSeen = 0
        }

        if (last && bytesNeeded != 0) {
            val badBytes = (bytesSeen + 1).toUByte()
            codePoint = 0
            bytesNeeded = 0
            bytesSeen = 0
            return Triple(DecoderResult.Malformed(badBytes, 0u), srcPos, dstPos)
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public fun decodeToUtf8Raw(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> = decodeToUtf8ViaUtf16(::decodeToUtf16Raw, src, dst, last)

    public companion object {
        public fun new(): Utf8Decoder = Utf8Decoder()

        internal fun newInner(): Utf8Decoder = Utf8Decoder()
    }
}

public class Utf8Data(
    public val table: IntArray,
)

/**
 * Converts a slice of UTF-16 code units to UTF-8 without astral splitting.
 */
internal fun convertUtf16ToUtf8PartialInner(src: CharArray, dst: ByteArray): Pair<Int, Int> =
    Mem.convertUtf16ToUtf8Partial(src, dst)

/**
 * Converts the tail of a slice of UTF-16 code units to UTF-8.
 */
internal fun convertUtf16ToUtf8PartialTail(src: CharArray, dst: ByteArray): Pair<Int, Int> =
    Mem.convertUtf16ToUtf8Partial(src, dst)

class Utf8Encoder {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        if (u16Length > Int.MAX_VALUE / 3) null else u16Length * 3

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        byteLength

    public fun encodeFromUtf16Raw(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> {
        if (last) Unit
        var s = 0
        var d = 0
        while (s < src.size) {
            val c = src[s]
            val code = c.code
            if (code <= 0x7F) {
                if (d >= dst.size) return Triple(EncoderResult.OutputFull, s, d)
                dst[d++] = code.toByte()
                s++
            } else if (code <= 0x07FF) {
                if (d + 2 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                dst[d++] = (0xC0 or (code shr 6)).toByte()
                dst[d++] = (0x80 or (code and 0x3F)).toByte()
                s++
            } else if (c.isHighSurrogate()) {
                if (s + 1 < src.size) {
                    val next = src[s + 1]
                    if (next.isLowSurrogate()) {
                        val cp = 0x10000 + ((code - 0xD800) shl 10) + (next.code - 0xDC00)
                        if (d + 4 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                        dst[d++] = (0xF0 or (cp shr 18)).toByte()
                        dst[d++] = (0x80 or ((cp shr 12) and 0x3F)).toByte()
                        dst[d++] = (0x80 or ((cp shr 6) and 0x3F)).toByte()
                        dst[d++] = (0x80 or (cp and 0x3F)).toByte()
                        s += 2
                    } else {
                        if (d + 3 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                        dst[d++] = 0xEF.toByte()
                        dst[d++] = 0xBF.toByte()
                        dst[d++] = 0xBD.toByte()
                        s++
                    }
                } else {
                    if (d + 3 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                    dst[d++] = 0xEF.toByte()
                    dst[d++] = 0xBF.toByte()
                    dst[d++] = 0xBD.toByte()
                    s++
                }
            } else if (c.isLowSurrogate()) {
                if (d + 3 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                dst[d++] = 0xEF.toByte()
                dst[d++] = 0xBF.toByte()
                dst[d++] = 0xBD.toByte()
                s++
            } else {
                if (d + 3 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                dst[d++] = (0xE0 or (code shr 12)).toByte()
                dst[d++] = (0x80 or ((code shr 6) and 0x3F)).toByte()
                dst[d++] = (0x80 or (code and 0x3F)).toByte()
                s++
            }
        }
        return Triple(EncoderResult.InputEmpty, s, d)
    }

    public fun encodeFromUtf8Raw(
        src: String,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> {
        if (last) Unit
        val bytes = src.encodeToByteArray()
        var toWrite = bytes.size
        if (toWrite <= dst.size) {
            bytes.copyInto(dst, 0, 0, toWrite)
            return Triple(EncoderResult.InputEmpty, toWrite, toWrite)
        }
        toWrite = dst.size
        while (toWrite > 0 && (bytes[toWrite].toInt() and 0xC0) == 0x80) {
            toWrite--
        }
        bytes.copyInto(dst, 0, 0, toWrite)
        return Triple(EncoderResult.OutputFull, toWrite, toWrite)
    }

    public companion object {
        public fun new(encoding: Encoding): Encoder =
            Encoder(encoding, VariantEncoder.Utf8)
    }
}
