// port-lint: source encoding_rs/src/euc_kr.rs
package io.github.kotlinmania.encodingrs

public class EucKrDecoder internal constructor(
    private var lead: Int = 0,
) {
    public fun inNeutralState(): Boolean = lead == 0

    private fun plusOneIfLead(byteLength: Int): Int =
        if (lead == 0) byteLength else byteLength + 1

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        plusOneIfLead(byteLength)

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? {
        val len = plusOneIfLead(byteLength)
        return len + (len + 1) / 2 + 2
    }

    public fun maxUtf8BufferLength(byteLength: Int): Int? =
        if (byteLength > Int.MAX_VALUE / 3 - 1) null else 3 * plusOneIfLead(byteLength)

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        while (srcPos < src.size) {
            if (lead == 0) {
                val b = src[srcPos].toInt() and 0xFF
                if (b < 0x80) {
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = b.toChar()
                    srcPos++
                    continue
                }
                val leadMinusOffset = b - 0x81
                if (leadMinusOffset < 0 || leadMinusOffset > (0xFE - 0x81)) {
                    srcPos++
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                lead = b
                srcPos++
            } else {
                val leadByte = lead
                val byte = src[srcPos].toInt() and 0xFF
                val leadMinusOffset = leadByte - 0x81

                if (leadMinusOffset >= 0x20) {
                    val trailMinusOffset = byte - 0xA1
                    if (trailMinusOffset in 0..(0xFE - 0xA1)) {
                        val ksxPointer = (leadMinusOffset - 0x20) * 94 + trailMinusOffset
                        val hangulPointer = ksxPointer - (0x2F - 0x20) * 94
                        if (hangulPointer in KSX1001_HANGUL.indices) {
                            if (dstPos >= dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = KSX1001_HANGUL[hangulPointer]
                            lead = 0
                            srcPos++
                        } else if (ksxPointer < KSX1001_SYMBOLS.size) {
                            if (dstPos >= dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = KSX1001_SYMBOLS[ksxPointer]
                            lead = 0
                            srcPos++
                        } else {
                            val hanjaPointer = ksxPointer - (0x49 - 0x20) * 94
                            if (hanjaPointer in KSX1001_HANJA.indices) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = KSX1001_HANJA[hanjaPointer]
                                lead = 0
                                srcPos++
                            } else if (leadMinusOffset == 0x27 && trailMinusOffset < KSX1001_UPPERCASE.size) {
                                val midBmp = KSX1001_UPPERCASE[trailMinusOffset]
                                if (midBmp.code == 0) {
                                    lead = 0
                                    srcPos++
                                    return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                                }
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = midBmp
                                lead = 0
                                srcPos++
                            } else if (leadMinusOffset == 0x28 && trailMinusOffset < KSX1001_LOWERCASE.size) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = KSX1001_LOWERCASE[trailMinusOffset]
                                lead = 0
                                srcPos++
                            } else if (leadMinusOffset == 0x25 && trailMinusOffset < KSX1001_BOX.size) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = KSX1001_BOX[trailMinusOffset]
                                lead = 0
                                srcPos++
                            } else {
                                val otherPointer = ksxPointer - 2 * 94
                                if (otherPointer in 0 until 0x039F) {
                                    val bmp = ksx1001OtherDecode(otherPointer)
                                    if (bmp < 0x80) {
                                        lead = 0
                                        srcPos++
                                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                                    }
                                    if (dstPos >= dst.size) {
                                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                    }
                                    dst[dstPos++] = bmp.toChar()
                                    lead = 0
                                    srcPos++
                                } else {
                                    lead = 0
                                    srcPos++
                                    return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                                }
                            }
                        }
                    } else {
                        val leftLead = leadMinusOffset - 0x20
                        val leftTrail =
                            if (((byte - (0x40 + 0x41)) and 0xFFFF) < (0x60 - 0x40)) {
                                byte - (12 + 0x41)
                            } else if (((byte - (0x20 + 0x41)) and 0xFFFF) < (0x3A - 0x20)) {
                                byte - (6 + 0x41)
                            } else if (((byte - 0x41) and 0xFFFF) < 0x1A) {
                                byte - 0x41
                            } else {
                                lead = 0
                                if (byte < 0x80) {
                                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                                }
                                srcPos++
                                return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                            }
                        val leftPointer = leftLead * (190 - 94 - 12) + leftTrail
                        if (leftPointer < (0x45 - 0x20) * (190 - 94 - 12) + 0x12) {
                            val upperBmp = cp949LeftHangulDecode(leftPointer)
                            if (dstPos >= dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = upperBmp
                            lead = 0
                            srcPos++
                        } else {
                            lead = 0
                            if (byte < 0x80) {
                                return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                            }
                            srcPos++
                            return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                        }
                    }
                } else {
                    val topTrail =
                        if (((byte - (0x40 + 0x41)) and 0xFFFF) < (0xBE - 0x40)) {
                            byte - (12 + 0x41)
                        } else if (((byte - (0x20 + 0x41)) and 0xFFFF) < (0x3A - 0x20)) {
                            byte - (6 + 0x41)
                        } else if (((byte - 0x41) and 0xFFFF) < 0x1A) {
                            byte - 0x41
                        } else {
                            lead = 0
                            if (byte < 0x80) {
                                return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                            }
                            srcPos++
                            return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                        }
                    val topPointer = leadMinusOffset * (190 - 12) + topTrail
                    val upperBmp = cp949TopHangulDecode(topPointer)
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = upperBmp
                    lead = 0
                    srcPos++
                }
            }
        }

        if (last && lead != 0) {
            lead = 0
            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): EucKrDecoder = EucKrDecoder()
    }
}

public class EucKrEncoder internal constructor() {
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

            val bmpMinusHangulStart = (bmp - 0xAC00) and 0xFFFF
            val leadTrail: Pair<Int, Int>? =
                if (bmpMinusHangulStart < (0xD7A4 - 0xAC00)) {
                    ksx1001EncodeHangul(bmp)
                } else if (bmp in 0x33DE until 0xFF01) {
                    if (bmp in 0x4E00 until 0x9F9D || bmp in 0xF900 until 0xFA0C) {
                        ksx1001EncodeHanja(bmp)
                    } else {
                        null
                    }
                } else {
                    ksx1001EncodeMisc(bmp)
                }

            if (leadTrail == null) {
                return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
            }

            if (dstPos + 2 > dst.size) {
                return Triple(EncoderResult.OutputFull, srcPos, dstPos)
            }
            dst[dstPos++] = leadTrail.first.toByte()
            dst[dstPos++] = leadTrail.second.toByte()
            srcPos++
        }

        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(encoding: Encoding): Encoder =
            Encoder(encoding, VariantEncoder.EucKr(EucKrEncoder()))
    }
}

internal fun ksx1001EncodeMisc(bmp: Int): Pair<Int, Int>? {
    if (bmp in 0x3000..0x3015) {
        val pos = position(KSX1001_SYMBOLS, 0, 0xAB - 0x60, bmp)
        if (pos != null) {
            return Pair(0xA1, pos + 0xA1)
        }
    }
    val otherPointer = ksx1001OtherEncode(bmp)
    if (otherPointer != null) {
        val otherLead = (otherPointer / 94) + (0x81 + 0x22)
        val otherTrail = (otherPointer % 94) + 0xA1
        return Pair(otherLead, otherTrail)
    }
    if (bmp in 0x00AA until 0x0168) {
        val posLower = position(KSX1001_LOWERCASE, 0, KSX1001_LOWERCASE.size, bmp)
        if (posLower != null) {
            return Pair(0x81 + 0x28, 0xA1 + posLower)
        }
        val posUpper = position(KSX1001_UPPERCASE, 0, KSX1001_UPPERCASE.size, bmp)
        if (posUpper != null) {
            return Pair(0x81 + 0x27, 0xA1 + posUpper)
        }
    } else if (bmp in 0x2500 until 0x254C) {
        val pos = position(KSX1001_BOX, 0, KSX1001_BOX.size, bmp)
        if (pos != null) {
            return Pair(0x81 + 0x25, 0xA1 + pos)
        }
    }
    if (bmp in 0x2015..0x266D ||
        bmp in 0x321C..0x33D8 ||
        bmp in 0xFF3C..0xFFE5 ||
        bmp in 0x00A1..0x00F7 ||
        bmp in 0x02C7..0x02DD
    ) {
        val pos = position(KSX1001_SYMBOLS, 3, KSX1001_SYMBOLS.size, bmp)
        if (pos != null) {
            if (pos < (94 - 3)) {
                return Pair(0xA1, pos + 0xA1 + 3)
            }
            return Pair(0xA2, pos - (94 - 3) + 0xA1)
        }
    }
    return null
}

internal fun ksx1001EncodeHangul(bmp: Int): Pair<Int, Int> {
    val idx = KSX1001_HANGUL.binarySearch(bmp.toChar())
    if (idx >= 0) {
        val ksxHangulLead = (idx / 94) + (0x81 + 0x2F)
        val ksxHangulTrail = (idx % 94) + 0xA1
        return Pair(ksxHangulLead, ksxHangulTrail)
    }
    val (lead, cp949Trail) =
        if (bmp < 0xC8A5) {
            val topPointer = cp949TopHangulEncode(bmp)
            val topLead = (topPointer / (190 - 12)) + 0x81
            val topTrail = topPointer % (190 - 12)
            Pair(topLead, topTrail)
        } else {
            val leftPointer = cp949LeftHangulEncode(bmp)
            val leftLead = (leftPointer / (190 - 94 - 12)) + (0x81 + 0x20)
            val leftTrail = leftPointer % (190 - 94 - 12)
            Pair(leftLead, leftTrail)
        }
    val offset =
        if (cp949Trail >= (0x40 - 12)) {
            0x41 + 12
        } else if (cp949Trail >= (0x20 - 6)) {
            0x41 + 6
        } else {
            0x41
        }
    return Pair(lead, cp949Trail + offset)
}

internal fun ksx1001EncodeHanja(bmp: Int): Pair<Int, Int>? {
    val pos = position(KSX1001_HANJA, 0, KSX1001_HANJA.size, bmp)
    return if (pos != null) {
        val hanjaLead = (pos / 94) + (0x81 + 0x49)
        val hanjaTrail = (pos % 94) + 0xA1
        Pair(hanjaLead, hanjaTrail)
    } else {
        null
    }
}
