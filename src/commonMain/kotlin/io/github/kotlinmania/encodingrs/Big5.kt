// port-lint: source encoding_rs/src/big5.rs
package io.github.kotlinmania.encodingrs

class Big5Decoder internal constructor(
    public var lead: Byte? = null,
    private var pendingHighSurrogate: Char? = null,
) {
    public fun inNeutralState(): Boolean = lead == null && pendingHighSurrogate == null

    private fun plusOneIfLead(byteLength: Int): Int =
        if (lead == null) byteLength else byteLength + 1

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        plusOneIfLead(byteLength) + 1

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? {
        val len = plusOneIfLead(byteLength)
        if (len > Int.MAX_VALUE / 2 - 1) return null
        return len * 2 + 2
    }

    public fun maxUtf8BufferLength(byteLength: Int): Int? {
        val len = plusOneIfLead(byteLength)
        if (len > Int.MAX_VALUE / 3 - 1) return null
        return len * 3 + 3
    }

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        if (pendingHighSurrogate != null) {
            if (dstPos >= dst.size) {
                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
            }
            dst[dstPos++] = pendingHighSurrogate!!
            pendingHighSurrogate = null
        }

        while (srcPos < src.size) {
            if (lead == null) {
                val b = src[srcPos].toInt() and 0xFF
                if (b < 0x80) {
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = b.toChar()
                    srcPos++
                    continue
                }
                val nonAsciiMinusOffset = b - 0x81
                if (nonAsciiMinusOffset < 0 || nonAsciiMinusOffset > (0xFE - 0x81)) {
                    srcPos++
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                lead = b.toByte()
                srcPos++
            } else {
                val leadByte = lead!!.toInt() and 0xFF
                val byte = src[srcPos].toInt() and 0xFF
                val leadMinusOffset = leadByte - 0x81

                var trailMinusOffset = byte - 0x40
                if (trailMinusOffset < 0 || trailMinusOffset > (0x7E - 0x40)) {
                    val trailMinusRangeStart = byte - 0xA1
                    if (trailMinusRangeStart < 0 || trailMinusRangeStart > (0xFE - 0xA1)) {
                        lead = null
                        if (byte < 0x80) {
                            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                        }
                        srcPos++
                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                    }
                    trailMinusOffset = byte - 0x62
                }

                val pointer = leadMinusOffset * 157 + trailMinusOffset
                val rebasedPointer = pointer - 942
                val lowBits = if (rebasedPointer >= 0) big5LowBits(rebasedPointer) else 0

                if (lowBits == 0) {
                    when (pointer) {
                        1133 -> {
                            if (dstPos + 2 > dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = 0x00CA.toChar()
                            dst[dstPos++] = 0x0304.toChar()
                            lead = null
                            srcPos++
                        }
                        1135 -> {
                            if (dstPos + 2 > dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = 0x00CA.toChar()
                            dst[dstPos++] = 0x030C.toChar()
                            lead = null
                            srcPos++
                        }
                        1164 -> {
                            if (dstPos + 2 > dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = 0x00EA.toChar()
                            dst[dstPos++] = 0x0304.toChar()
                            lead = null
                            srcPos++
                        }
                        1166 -> {
                            if (dstPos + 2 > dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = 0x00EA.toChar()
                            dst[dstPos++] = 0x030C.toChar()
                            lead = null
                            srcPos++
                        }
                        else -> {
                            lead = null
                            if (byte < 0x80) {
                                return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                            }
                            srcPos++
                            return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                        }
                    }
                } else if (rebasedPointer >= 0 && big5IsAstral(rebasedPointer)) {
                    val codePoint = lowBits or 0x20000
                    val high = ((codePoint - 0x10000) ushr 10) + 0xD800
                    val low = ((codePoint - 0x10000) and 0x3FF) + 0xDC00
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = high.toChar()
                    if (dstPos < dst.size) {
                        dst[dstPos++] = low.toChar()
                    } else {
                        pendingHighSurrogate = low.toChar()
                    }
                    lead = null
                    srcPos++
                } else {
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = lowBits.toChar()
                    lead = null
                    srcPos++
                }
            }
        }

        if (last && lead != null) {
            lead = null
            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): Big5Decoder = Big5Decoder()
    }
}

class Big5Encoder internal constructor() {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        if (u16Length > Int.MAX_VALUE / 2) null else u16Length * 2

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        if (byteLength == Int.MAX_VALUE) null else byteLength + 1

    public fun encodeFromUtf16Raw(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        while (srcPos < src.size) {
            val c = src[srcPos]
            val bmp = c.code

            if (bmp <= 0x7F) {
                if (dstPos >= dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = bmp.toByte()
                srcPos++
                continue
            }

            if (c.isHighSurrogate()) {
                if (srcPos + 1 < src.size) {
                    val next = src[srcPos + 1]
                    if (next.isLowSurrogate()) {
                        val astral = ((bmp - 0xD800) shl 10) + (next.code - 0xDC00) + 0x10000
                        if (astral in 0x2008A..0x2F8A6) {
                            val rebasedPointer = big5AstralEncode(astral and 0xFFFF)
                            if (rebasedPointer != null) {
                                val lead = rebasedPointer / 157 + 0x87
                                val remainder = rebasedPointer % 157
                                val trail = if (remainder < 0x3F) remainder + 0x40 else remainder + 0x62
                                if (dstPos + 2 > dst.size) {
                                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = lead.toByte()
                                dst[dstPos++] = trail.toByte()
                                srcPos += 2
                                continue
                            }
                        }
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

            val level1 = big5Level1HanziEncode(bmp)
            if (level1 != null) {
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = level1.first.toByte()
                dst[dstPos++] = level1.second.toByte()
                srcPos++
                continue
            }

            val pointer = big5BoxEncode(bmp) ?: big5OtherEncode(bmp)
            if (pointer == null) {
                return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
            }

            val lead = pointer / 157 + 0x81
            val remainder = pointer % 157
            val trail = if (remainder < 0x3F) remainder + 0x40 else remainder + 0x62
            if (dstPos + 2 > dst.size) {
                return Triple(EncoderResult.OutputFull, srcPos, dstPos)
            }
            dst[dstPos++] = lead.toByte()
            dst[dstPos++] = trail.toByte()
            srcPos++
        }

        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(encoding: Encoding): Encoder =
            Encoder(encoding, VariantEncoder.Big5(Big5Encoder()))
    }
}
