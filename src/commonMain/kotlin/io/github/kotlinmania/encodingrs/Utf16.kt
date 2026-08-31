// port-lint: source encoding_rs/src/utf_16.rs
package io.github.kotlinmania.encodingrs

/**
 * UTF-16 decoder supporting both Little-Endian and Big-Endian byte orders.
 */
public class Utf16Decoder(
    public val be: Boolean,
) {
    public var leadSurrogate: Int = 0
    public var leadByte: Int? = null
    public var pendingBmp: Boolean = false

    public fun additionalFromState(): Int =
        1 + (if (leadByte != null) 1 else 0) + (if (leadSurrogate == 0) 0 else 2)

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        1 + (byteLength + additionalFromState()) / 2

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        1 + 3 * ((byteLength + additionalFromState()) / 2)

    public fun maxUtf8BufferLength(byteLength: Int): Int? =
        1 + 3 * ((byteLength + additionalFromState()) / 2)

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        if (pendingBmp) {
            if (dstPos >= dst.size) {
                return Triple(DecoderResult.OutputFull, 0, 0)
            }
            dst[dstPos++] = leadSurrogate.toChar()
            pendingBmp = false
            leadSurrogate = 0
        }

        while (srcPos < src.size) {
            val hadLeadByte = leadByte != null
            val unitStart = srcPos
            val lead = leadByte ?: (src[srcPos++].toInt() and 0xFF)
            if (srcPos >= src.size && !hadLeadByte) {
                leadByte = lead
                break
            }
            val b = src[srcPos++].toInt() and 0xFF
            leadByte = null
            val codeUnit =
                if (be) {
                    (lead shl 8) or b
                } else {
                    (b shl 8) or lead
                }
            val highBits = codeUnit and 0xFC00
            if (highBits == 0xD800) {
                // High surrogate
                if (leadSurrogate != 0) {
                    leadSurrogate = codeUnit
                    return Triple(DecoderResult.Malformed(2u, 2u), srcPos, dstPos)
                }
                leadSurrogate = codeUnit
                continue
            }
            if (highBits == 0xDC00) {
                // Low surrogate
                if (leadSurrogate == 0) {
                    return Triple(DecoderResult.Malformed(2u, 0u), srcPos, dstPos)
                }
                if (dstPos + 2 > dst.size) {
                    if (hadLeadByte) {
                        leadByte = lead
                        srcPos = 0
                    } else {
                        srcPos = unitStart
                    }
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = leadSurrogate.toChar()
                dst[dstPos++] = codeUnit.toChar()
                leadSurrogate = 0
                continue
            }
            // BMP
            if (leadSurrogate != 0) {
                leadSurrogate = codeUnit
                pendingBmp = true
                return Triple(DecoderResult.Malformed(2u, 2u), srcPos, dstPos)
            }
            if (dstPos >= dst.size) {
                if (hadLeadByte) {
                    leadByte = lead
                    srcPos = 0
                } else {
                    srcPos = unitStart
                }
                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
            }
            dst[dstPos++] = codeUnit.toChar()
        }

        if (last) {
            if (leadSurrogate != 0 || leadByte != null) {
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                if (leadSurrogate != 0) {
                    leadSurrogate = 0
                    return if (leadByte == null) {
                        Triple(DecoderResult.Malformed(2u, 0u), srcPos, dstPos)
                    } else {
                        leadByte = null
                        Triple(DecoderResult.Malformed(3u, 0u), srcPos, dstPos)
                    }
                }
                leadByte = null
                return Triple(DecoderResult.Malformed(1u, 0u), srcPos, dstPos)
            }
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public fun decodeToUtf8Raw(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        if (pendingBmp) {
            val codeUnit = leadSurrogate
            val needed =
                if (codeUnit < 0x80) {
                    1
                } else if (codeUnit < 0x800) {
                    2
                } else {
                    3
                }
            if (dstPos + needed > dst.size) {
                return Triple(DecoderResult.OutputFull, 0, 0)
            }
            if (codeUnit < 0x80) {
                dst[dstPos++] = codeUnit.toByte()
            } else if (codeUnit < 0x800) {
                dst[dstPos++] = (0xC0 or (codeUnit shr 6)).toByte()
                dst[dstPos++] = (0x80 or (codeUnit and 0x3F)).toByte()
            } else {
                dst[dstPos++] = (0xE0 or (codeUnit shr 12)).toByte()
                dst[dstPos++] = (0x80 or ((codeUnit shr 6) and 0x3F)).toByte()
                dst[dstPos++] = (0x80 or (codeUnit and 0x3F)).toByte()
            }
            pendingBmp = false
            leadSurrogate = 0
        }

        while (srcPos < src.size) {
            val hadLeadByte = leadByte != null
            val unitStart = srcPos
            val lead = leadByte ?: (src[srcPos++].toInt() and 0xFF)
            if (srcPos >= src.size && !hadLeadByte) {
                leadByte = lead
                break
            }
            val b = src[srcPos++].toInt() and 0xFF
            leadByte = null
            val codeUnit =
                if (be) {
                    (lead shl 8) or b
                } else {
                    (b shl 8) or lead
                }
            val highBits = codeUnit and 0xFC00
            if (highBits == 0xD800) {
                // High surrogate
                if (leadSurrogate != 0) {
                    leadSurrogate = codeUnit
                    return Triple(DecoderResult.Malformed(2u, 2u), srcPos, dstPos)
                }
                leadSurrogate = codeUnit
                continue
            }
            if (highBits == 0xDC00) {
                // Low surrogate
                if (leadSurrogate == 0) {
                    return Triple(DecoderResult.Malformed(2u, 0u), srcPos, dstPos)
                }
                if (dstPos + 4 > dst.size) {
                    if (hadLeadByte) {
                        leadByte = lead
                        srcPos = 0
                    } else {
                        srcPos = unitStart
                    }
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                val codePoint = 0x10000 + ((leadSurrogate - 0xD800) shl 10) + (codeUnit - 0xDC00)
                dst[dstPos++] = (0xF0 or (codePoint shr 18)).toByte()
                dst[dstPos++] = (0x80 or ((codePoint shr 12) and 0x3F)).toByte()
                dst[dstPos++] = (0x80 or ((codePoint shr 6) and 0x3F)).toByte()
                dst[dstPos++] = (0x80 or (codePoint and 0x3F)).toByte()
                leadSurrogate = 0
                continue
            }
            // BMP
            if (leadSurrogate != 0) {
                leadSurrogate = codeUnit
                pendingBmp = true
                return Triple(DecoderResult.Malformed(2u, 2u), srcPos, dstPos)
            }
            val needed =
                if (codeUnit < 0x80) {
                    1
                } else if (codeUnit < 0x800) {
                    2
                } else {
                    3
                }
            if (dstPos + needed > dst.size) {
                if (hadLeadByte) {
                    leadByte = lead
                    srcPos = 0
                } else {
                    srcPos = unitStart
                }
                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
            }
            if (codeUnit < 0x80) {
                dst[dstPos++] = codeUnit.toByte()
            } else if (codeUnit < 0x800) {
                dst[dstPos++] = (0xC0 or (codeUnit shr 6)).toByte()
                dst[dstPos++] = (0x80 or (codeUnit and 0x3F)).toByte()
            } else {
                dst[dstPos++] = (0xE0 or (codeUnit shr 12)).toByte()
                dst[dstPos++] = (0x80 or ((codeUnit shr 6) and 0x3F)).toByte()
                dst[dstPos++] = (0x80 or (codeUnit and 0x3F)).toByte()
            }
        }

        if (last) {
            if (leadSurrogate != 0 || leadByte != null) {
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                if (leadSurrogate != 0) {
                    leadSurrogate = 0
                    return if (leadByte == null) {
                        Triple(DecoderResult.Malformed(2u, 0u), srcPos, dstPos)
                    } else {
                        leadByte = null
                        Triple(DecoderResult.Malformed(3u, 0u), srcPos, dstPos)
                    }
                }
                leadByte = null
                return Triple(DecoderResult.Malformed(1u, 0u), srcPos, dstPos)
            }
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(bigEndian: Boolean): Utf16Decoder = Utf16Decoder(bigEndian)
    }
}
