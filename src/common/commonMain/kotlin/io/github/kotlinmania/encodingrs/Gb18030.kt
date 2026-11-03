// port-lint: source gb18030.rs
package io.github.kotlinmania.encodingrs

public sealed class Gb18030Pending {
    public data object None : Gb18030Pending()

    public data class One(
        val b: Int,
    ) : Gb18030Pending()

    public data class Two(
        val b1: Int,
        val b2: Int,
    ) : Gb18030Pending()

    public data class Three(
        val b1: Int,
        val b2: Int,
        val b3: Int,
    ) : Gb18030Pending()

    public fun count(): Int =
        when (this) {
            is None -> 0
            is One -> 1
            is Two -> 2
            is Three -> 3
        }
}

class Gb18030Decoder internal constructor(
    private var first: Int? = null,
    private var second: Int? = null,
    private var third: Int? = null,
    private var pending: Gb18030Pending = Gb18030Pending.None,
    private var pendingAscii: Int? = null,
    private var pendingHighSurrogate: Char? = null,
) {
    public fun isNone(): Boolean = inNeutralState()

    public fun inNeutralState(): Boolean =
        first == null &&
            second == null &&
            third == null &&
            pending is Gb18030Pending.None &&
            pendingAscii == null &&
            pendingHighSurrogate == null

    private fun extraFromState(byteLength: Int): Int {
        var extra = pending.count()
        if (first != null) extra++
        if (second != null) extra++
        if (third != null) extra++
        if (pendingAscii != null) extra++
        return byteLength + extra
    }

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        1 + extraFromState(byteLength)

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        maxUtf8BufferLength(byteLength)

    public fun maxUtf8BufferLength(byteLength: Int): Int? {
        val extra = extraFromState(byteLength)
        if (extra > (Int.MAX_VALUE - 1) / 3) return null
        return 1 + extra * 3
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

        while (srcPos < src.size || pendingAscii != null || pending !is Gb18030Pending.None) {
            if (pendingAscii != null) {
                if (dstPos >= dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = pendingAscii!!.toChar()
                pendingAscii = null
                continue
            }

            when (val p = pending) {
                is Gb18030Pending.One -> {
                    first = p.b
                    pending = Gb18030Pending.None
                }
                is Gb18030Pending.Two -> {
                    first = p.b1
                    second = p.b2
                    pending = Gb18030Pending.None
                }
                is Gb18030Pending.Three -> {
                    first = p.b1
                    second = p.b2
                    third = p.b3
                    pending = Gb18030Pending.None
                }
                is Gb18030Pending.None -> {}
            }

            if (srcPos >= src.size) break

            if (first == null) {
                val b = src[srcPos].toInt() and 0xFF
                if (b < 0x80) {
                    if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = b.toChar()
                    srcPos++
                    continue
                }
                val nonAsciiMinusOffset = b - 0x81
                if (nonAsciiMinusOffset !in 0..(0xFE - 0x81)) {
                    if (b == 0x80) {
                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = 0x20AC.toChar()
                        srcPos++
                        continue
                    }
                    srcPos++
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                first = b
                srcPos++
            } else if (second == null) {
                val b = src[srcPos].toInt() and 0xFF
                if (b in 0x30..0x39) {
                    second = b
                    srcPos++
                    continue
                }

                val firstByte = first!!
                first = null
                val firstMinusOffset = firstByte - 0x81

                if (firstMinusOffset >= 0x20) {
                    val trailMinusOffset = b - 0xA1
                    if (trailMinusOffset in 0..(0xFE - 0xA1)) {
                        val hanziLead = firstMinusOffset - 0x2F
                        if (hanziLead in 0 until (0x77 - 0x2F)) {
                            val hanziPointer = hanziLead * 94 + trailMinusOffset
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = GB2312_HANZI[hanziPointer]
                            srcPos++
                        } else if (firstMinusOffset == 0x20) {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = GB2312_SYMBOLS[trailMinusOffset]
                            srcPos++
                        } else if (firstMinusOffset == 0x25 && (trailMinusOffset - 63) in GB2312_SYMBOLS_AFTER_GREEK.indices) {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = GB2312_SYMBOLS_AFTER_GREEK[trailMinusOffset - 63]
                            srcPos++
                        } else if (firstMinusOffset == 0x27 && trailMinusOffset in GB2312_PINYIN.indices) {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = GB2312_PINYIN[trailMinusOffset]
                            srcPos++
                        } else if (firstMinusOffset > 0x76) {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            val pua = 0xE234 + (firstMinusOffset - 0x77) * 94 + trailMinusOffset
                            dst[dstPos++] = pua.toChar()
                            srcPos++
                        } else {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            val bmp = gb2312OtherDecode((firstMinusOffset - 0x21) * 94 + trailMinusOffset)
                            dst[dstPos++] = bmp.toChar()
                            srcPos++
                        }
                    } else {
                        var trailMinusOffset2 = b - 0x40
                        if (trailMinusOffset2 !in 0..(0x7E - 0x40)) {
                            val trailMinusRangeStart = b - 0x80
                            if (trailMinusRangeStart !in 0..(0xA0 - 0x80)) {
                                if (b < 0x80) {
                                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                                }
                                srcPos++
                                return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                            }
                            trailMinusOffset2 = b - 0x41
                        }
                        val leftLead = firstMinusOffset - 0x20
                        val leftPointer = leftLead * (190 - 94) + trailMinusOffset2
                        val gbkLeftIdeographPointer = leftPointer - (0x29 - 0x20) * (190 - 94)
                        if (gbkLeftIdeographPointer in 0 until (((0x7D - 0x29) * (190 - 94)) - 5)) {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = gbkLeftIdeographDecode(gbkLeftIdeographPointer)
                            srcPos++
                        } else if (leftPointer in 0 until ((0x29 - 0x20) * (190 - 94))) {
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = gbkOtherDecode(leftPointer).toChar()
                            srcPos++
                        } else {
                            val bottomPointer = leftPointer - (((0x7D - 0x20) * (190 - 94)) - 5)
                            if (dstPos >= dst.size) {
                                first = firstByte
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = GBK_BOTTOM[bottomPointer]
                            srcPos++
                        }
                    }
                } else {
                    var trailMinusOffset = b - 0x40
                    if (trailMinusOffset !in 0..(0x7E - 0x40)) {
                        val trailMinusRangeStart = b - 0x80
                        if (trailMinusRangeStart !in 0..(0xFE - 0x80)) {
                            if (b < 0x80) {
                                return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                            }
                            srcPos++
                            return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                        }
                        trailMinusOffset = b - 0x41
                    }
                    val pointer = firstMinusOffset * 190 + trailMinusOffset
                    if (dstPos >= dst.size) {
                        first = firstByte
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = gbkTopIdeographDecode(pointer)
                    srcPos++
                }
            } else if (third == null) {
                val b = src[srcPos].toInt() and 0xFF
                val thirdMinusOffset = b - 0x81
                if (thirdMinusOffset !in 0..(0xFE - 0x81)) {
                    pendingAscii = second
                    first = null
                    second = null
                    return Triple(DecoderResult.Malformed(1, 1), srcPos, dstPos)
                }
                third = b
                srcPos++
            } else {
                val b = src[srcPos].toInt() and 0xFF
                val fourthMinusOffset = b - 0x30
                if (fourthMinusOffset !in 0..9) {
                    pendingAscii = second
                    pending = Gb18030Pending.One(third!!)
                    first = null
                    second = null
                    third = null
                    return Triple(DecoderResult.Malformed(1, 2), srcPos, dstPos)
                }
                val firstMinusOffset = first!! - 0x81
                val secondMinusOffset = second!! - 0x30
                val thirdMinusOffset = third!! - 0x81
                first = null
                second = null
                third = null

                val pointer =
                    (firstMinusOffset * (10 * 126 * 10)) +
                        (secondMinusOffset * (10 * 126)) +
                        (thirdMinusOffset * 10) +
                        fourthMinusOffset

                if (pointer <= 39419) {
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    if (pointer == 7457) {
                        dst[dstPos++] = 0xE7C7.toChar()
                    } else {
                        dst[dstPos++] = gb18030RangeDecode(pointer).toChar()
                    }
                    srcPos++
                } else if (pointer in 189_000..1_237_575) {
                    val astral = pointer - (189_000 - 0x10000)
                    val high = ((astral - 0x10000) ushr 10) + 0xD800
                    val low = ((astral - 0x10000) and 0x3FF) + 0xDC00
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = high.toChar()
                    if (dstPos < dst.size) {
                        dst[dstPos++] = low.toChar()
                    } else {
                        pendingHighSurrogate = low.toChar()
                    }
                    srcPos++
                } else {
                    srcPos++
                    return Triple(DecoderResult.Malformed(4, 0), srcPos, dstPos)
                }
            }
        }

        if (last) {
            val pendingCount = pending.count()
            if (pendingCount > 0) {
                pending = Gb18030Pending.None
                return Triple(DecoderResult.Malformed(pendingCount, 0), srcPos, dstPos)
            }
            if (first != null || second != null || third != null) {
                var count = 0
                if (first != null) count++
                if (second != null) count++
                if (third != null) count++
                first = null
                second = null
                third = null
                return Triple(DecoderResult.Malformed(count, 0), srcPos, dstPos)
            }
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): Gb18030Decoder = Gb18030Decoder()
    }
}

internal fun gbkEncodeNonUnified(bmp: Int): Pair<Int, Int>? {
    if (bmp in 0x2014..0x3017 || bmp in 0xFF04..0xFFE1) {
        val pos = position(GB2312_SYMBOLS, 0, GB2312_SYMBOLS.size, bmp)
        if (pos != null) return Pair(0xA1, pos + 0xA1)
    }
    if (bmp in 0x3400 until 0x4E00) {
        val pos = position(GBK_BOTTOM, 21, 100 - 1, bmp)
        if (pos != null) {
            val trail = (pos - 21) + 16
            val offset = if (trail < 0x3F) 0x40 else 0x41
            return Pair(0xFE, trail + offset)
        }
    }
    if (bmp in 0xF900 until 0xFB00) {
        val pos = position(GBK_BOTTOM, 0, 21 - 1, bmp)
        if (pos != null) {
            return if (pos < 5) {
                Pair(0xFD, pos + (190 - 94 - 5 + 0x41))
            } else {
                Pair(0xFE, pos + (0x40 - 5))
            }
        }
    }
    if (bmp < 0x02CA) {
        if (bmp in 0x00E0 until 0x0262 && bmp != 0x00F7) {
            val pos = position(GB2312_PINYIN, 0, GB2312_PINYIN.size - 1, bmp)
            if (pos != null) return Pair(0xA8, pos + 0xA1)
        } else if (bmp in 0x00A4..0x00F7 || bmp in 0x02C7..0x02C9) {
            val pos = position(GB2312_SYMBOLS, 3, 0xAC - 0x60 - 1, bmp)
            if (pos != null) return Pair(0xA1, pos + 0xA1)
        }
        return null
    }

    if (bmp in 0xE78D..0xE864) {
        val uBmp = bmp.toUShort()
        val pos = GB18030_2022_OVERRIDE_PUA.indexOf(uBmp)
        if (pos >= 0) {
            val pair = GB18030_2022_OVERRIDE_BYTES[pos]
            return Pair(pair[0].toInt(), pair[1].toInt())
        }
    } else if (bmp >= 0xFE17) {
        val pos = position(GB2312_SYMBOLS_AFTER_GREEK, 0, GB2312_SYMBOLS_AFTER_GREEK.size - 1, bmp)
        if (pos != null) return Pair(0xA6, pos + (0x9F - 0x60 + 0xA1))
    } else if (bmp == 0x1E3F) {
        return Pair(0xA8, 0x7B - 0x60 + 0xA1)
    } else if (bmp in 0xA000 until 0xD800) {
        return null
    }

    val otherPointer = gb2312OtherEncode(bmp)
    if (otherPointer != null) {
        val otherLead = otherPointer / 94
        val otherTrail = otherPointer % 94
        return Pair(0xA2 + otherLead, 0xA1 + otherTrail)
    }

    if (bmp in 0x02DA until 0x2010) return null

    val gbkOtherPtr = gbkOtherEncode(bmp)
    if (gbkOtherPtr != null) {
        val otherLead = gbkOtherPtr / (190 - 94)
        val otherTrail = gbkOtherPtr % (190 - 94)
        val offset = if (otherTrail < 0x3F) 0x40 else 0x41
        return Pair(otherLead + (0x81 + 0x20), otherTrail + offset)
    }

    if (bmp in 0x2E81..0x2ECA || bmp in 0x9FB4..0x9FBB || bmp in 0xE816..0xE855) {
        val pos = position(GBK_BOTTOM, 21, GBK_BOTTOM.size - 1, bmp)
        if (pos != null) {
            val trail = (pos - 21) + 16
            val offset = if (trail < 0x3F) 0x40 else 0x41
            return Pair(0xFE, trail + offset)
        }
    }

    val bmpMinusBottomPua = bmp - 0xE234
    if (bmpMinusBottomPua in 0..(0xE4C5 - 0xE234)) {
        val puaLead = bmpMinusBottomPua / 94
        val puaTrail = bmpMinusBottomPua % 94
        return Pair(0x81 + 0x77 + puaLead, 0xA1 + puaTrail)
    }

    val bmpMinusPuaBetween = bmp - 0xE810
    if (bmpMinusPuaBetween in 0 until 5) {
        return Pair(0x81 + 0x56, 0xFF - 5 + bmpMinusPuaBetween)
    }

    return null
}

internal fun encodeGbHanzi(bmp: Int): Pair<Int, Int> {
    val level1 = gb2312Level1HanziEncode(bmp)
    if (level1 != null) return level1
    val level2 = gb2312Level2HanziEncode(bmp)
    if (level2 != null) {
        val hanziLead = (level2 / 94) + 0xD8
        val hanziTrail = (level2 % 94) + 0xA1
        return Pair(hanziLead, hanziTrail)
    }
    val (lead, gbkTrail) =
        if (bmp < 0x72DC) {
            val pointer = gbkTopIdeographEncode(bmp)
            val l = (pointer / 190) + 0x81
            val t = pointer % 190
            Pair(l, t)
        } else {
            val pointer = gbkLeftIdeographEncode(bmp)
            val l = (pointer / (190 - 94)) + (0x81 + 0x29)
            val t = pointer % (190 - 94)
            Pair(l, t)
        }
    val offset = if (gbkTrail < 0x3F) 0x40 else 0x41
    return Pair(lead, gbkTrail + offset)
}

/**
 * Encodes a unified ideograph into GBK bytes.
 */
internal fun encodeHanzi(bmp: Int): Pair<Int, Int> = encodeGbHanzi(bmp)

class Gb18030Encoder internal constructor(
    private val extended: Boolean = true,
) {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        if (extended) {
            if (u16Length > Int.MAX_VALUE / 4) null else u16Length * 4
        } else {
            if (u16Length > Int.MAX_VALUE / 2 - 1) null else 2 + u16Length * 2
        }

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        if (extended) {
            if (byteLength > Int.MAX_VALUE / 2 - 1) null else 2 + byteLength * 2
        } else {
            if (byteLength > Int.MAX_VALUE - 3) null else byteLength + 3
        }

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
                if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                dst[dstPos++] = bmp.toByte()
                srcPos++
                continue
            }

            if (c.isHighSurrogate()) {
                if (srcPos + 1 < src.size) {
                    val next = src[srcPos + 1]
                    if (next.isLowSurrogate()) {
                        val astral = ((bmp - 0xD800) shl 10) + (next.code - 0xDC00) + 0x10000
                        if (!extended) {
                            return Triple(EncoderResult.Unmappable(astral), srcPos + 2, dstPos)
                        }
                        val rangePointer = astral + (189_000 - 0x10000)
                        val first = rangePointer / (10 * 126 * 10)
                        val remFirst = rangePointer % (10 * 126 * 10)
                        val second = remFirst / (10 * 126)
                        val remSecond = remFirst % (10 * 126)
                        val third = remSecond / 10
                        val fourth = remSecond % 10
                        if (dstPos + 4 > dst.size) {
                            return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = (first + 0x81).toByte()
                        dst[dstPos++] = (second + 0x30).toByte()
                        dst[dstPos++] = (third + 0x81).toByte()
                        dst[dstPos++] = (fourth + 0x30).toByte()
                        srcPos += 2
                        continue
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

            val bmpMinusUnifiedStart = bmp - 0x4E00
            if (bmpMinusUnifiedStart in 0 until (0x9FA6 - 0x4E00)) {
                val pair = encodeGbHanzi(bmp)
                if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                dst[dstPos++] = pair.first.toByte()
                dst[dstPos++] = pair.second.toByte()
                srcPos++
                continue
            } else if (bmp == 0xE5E5) {
                return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
            } else if (bmp == 0x20AC && !extended) {
                if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                dst[dstPos++] = 0x80.toByte()
                srcPos++
                continue
            }

            val nonUnified = gbkEncodeNonUnified(bmp)
            if (nonUnified != null) {
                if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                dst[dstPos++] = nonUnified.first.toByte()
                dst[dstPos++] = nonUnified.second.toByte()
                srcPos++
                continue
            }

            if (!extended) {
                return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
            }

            val rangePointer = gb18030RangeEncode(bmp)
            val first = rangePointer / (10 * 126 * 10)
            val remFirst = rangePointer % (10 * 126 * 10)
            val second = remFirst / (10 * 126)
            val remSecond = remFirst % (10 * 126)
            val third = remSecond / 10
            val fourth = remSecond % 10
            if (dstPos + 4 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
            dst[dstPos++] = (first + 0x81).toByte()
            dst[dstPos++] = (second + 0x30).toByte()
            dst[dstPos++] = (third + 0x81).toByte()
            dst[dstPos++] = (fourth + 0x30).toByte()
            srcPos++
        }

        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(encoding: Encoding, extendedRange: Boolean): Encoder =
            Encoder(encoding, VariantEncoder.Gb18030(Gb18030Encoder(extendedRange)))
    }
}
