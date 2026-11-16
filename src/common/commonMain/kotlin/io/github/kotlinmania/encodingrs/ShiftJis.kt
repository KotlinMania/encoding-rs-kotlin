// port-lint: source shift_jis.rs
package io.github.kotlinmania.encodingrs

public class ShiftJisDecoder internal constructor(
    private var lead: Int = 0,
) {
    public fun inNeutralState(): Boolean = lead == 0

    private fun plusOneIfLead(byteLength: Int): Int =
        if (lead == 0) byteLength else byteLength + 1

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        plusOneIfLead(byteLength)

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        maxUtf8BufferLength(byteLength)

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
                var nonAsciiMinusOffset = b - 0x81
                if (nonAsciiMinusOffset < 0 || nonAsciiMinusOffset > (0x9F - 0x81)) {
                    val nonAsciiMinusRangeStart = b - 0xE0
                    if (nonAsciiMinusRangeStart < 0 || nonAsciiMinusRangeStart > (0xFC - 0xE0)) {
                        val nonAsciiMinusHalfWidthKatakanaStart = b - 0xA1
                        if (nonAsciiMinusHalfWidthKatakanaStart < 0 || nonAsciiMinusHalfWidthKatakanaStart > (0xDF - 0xA1)) {
                            if (b == 0x80) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = 0x80.toChar()
                                srcPos++
                                continue
                            }
                            srcPos++
                            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                        }
                        if (dstPos >= dst.size) {
                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = (0xFF61 + nonAsciiMinusHalfWidthKatakanaStart).toChar()
                        srcPos++
                        continue
                    }
                    nonAsciiMinusOffset = b - 0xC1
                }
                lead = b
                srcPos++
            } else {
                val leadByte = lead
                val byte = src[srcPos].toInt() and 0xFF
                val leadMinusOffset = if (leadByte >= 0xE0) leadByte - 0xC1 else leadByte - 0x81

                val trailMinusHiragana = byte - 0x9F
                if (leadMinusOffset == 0x01 && trailMinusHiragana in 0 until 0x53) {
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = (0x3041 + trailMinusHiragana).toChar()
                    lead = 0
                    srcPos++
                } else {
                    var trailMinusOffset = byte - 0x40
                    if (trailMinusOffset < 0 || trailMinusOffset > (0x7E - 0x40)) {
                        val trailMinusRangeStart = byte - 0x80
                        if (trailMinusRangeStart < 0 || trailMinusRangeStart > (0xFC - 0x80)) {
                            lead = 0
                            if (byte < 0x80) {
                                return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                            }
                            srcPos++
                            return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                        }
                        trailMinusOffset = byte - 0x41
                    }

                    if (leadMinusOffset == 0x02 && trailMinusOffset < 0x56) {
                        if (dstPos >= dst.size) {
                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = (0x30A1 + trailMinusOffset).toChar()
                        lead = 0
                        srcPos++
                    } else {
                        val pointer = leadMinusOffset * 188 + trailMinusOffset
                        val level1Pointer = pointer - 1410
                        if (level1Pointer in JIS0208_LEVEL1_KANJI.indices) {
                            if (dstPos >= dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = JIS0208_LEVEL1_KANJI[level1Pointer]
                            lead = 0
                            srcPos++
                        } else {
                            val level2Pointer = pointer - 4418
                            if (level2Pointer in JIS0208_LEVEL2_AND_ADDITIONAL_KANJI.indices) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = JIS0208_LEVEL2_AND_ADDITIONAL_KANJI[level2Pointer]
                                lead = 0
                                srcPos++
                            } else {
                                val upperIbmPointer = pointer - 10744
                                if (upperIbmPointer in IBM_KANJI.indices) {
                                    if (dstPos >= dst.size) {
                                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                    }
                                    dst[dstPos++] = IBM_KANJI[upperIbmPointer]
                                    lead = 0
                                    srcPos++
                                } else {
                                    val lowerIbmPointer = pointer - 8272
                                    if (lowerIbmPointer in IBM_KANJI.indices) {
                                        if (dstPos >= dst.size) {
                                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                        }
                                        dst[dstPos++] = IBM_KANJI[lowerIbmPointer]
                                        lead = 0
                                        srcPos++
                                    } else if (pointer in 8836..10715) {
                                        if (dstPos >= dst.size) {
                                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                        }
                                        dst[dstPos++] = (0xE000 - 8836 + pointer).toChar()
                                        lead = 0
                                        srcPos++
                                    } else {
                                        val sym = jis0208SymbolDecode(pointer) ?: jis0208RangeDecode(pointer)
                                        if (sym != null) {
                                            if (dstPos >= dst.size) {
                                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                            }
                                            dst[dstPos++] = sym.toChar()
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
                                }
                            }
                        }
                    }
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
        public fun new(): ShiftJisDecoder = ShiftJisDecoder()
    }
}

public class ShiftJisEncoder internal constructor() {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        if (u16Length > Int.MAX_VALUE / 2) null else u16Length * 2

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        if (byteLength == Int.MAX_VALUE) null else byteLength + 1

    private fun encodeKanji(bmp: Int): Pair<Int, Int>? {
        val level1 = jis0208Level1KanjiShiftJisEncode(bmp)
        if (level1 != null) return level1
        val pointer =
            if (0x4EDD == bmp) {
                23
            } else {
                val l2 = jis0208Level2AndAdditionalKanjiEncode(bmp)
                if (l2 != null) {
                    4418 + l2
                } else {
                    val pos = position(IBM_KANJI, 0, IBM_KANJI.size, bmp)
                    if (pos != null) {
                        10744 + pos
                    } else {
                        return null
                    }
                }
            }
        val lead = pointer / 188
        val leadOffset = if (lead < 0x1F) 0x81 else 0xC1
        val trail = pointer % 188
        val trailOffset = if (trail < 0x3F) 0x40 else 0x41
        return Pair(lead + leadOffset, trail + trailOffset)
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

            val bmpMinusHiragana = bmp - 0x3041
            if (bmpMinusHiragana in 0 until 0x53) {
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = 0x82.toByte()
                dst[dstPos++] = (0x9F + bmpMinusHiragana).toByte()
                srcPos++
                continue
            }

            if (bmp in 0x4E00..0x9FA0) {
                val kanji = encodeKanji(bmp)
                if (kanji == null) {
                    return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
                }
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = kanji.first.toByte()
                dst[dstPos++] = kanji.second.toByte()
                srcPos++
                continue
            }

            val bmpMinusKatakana = bmp - 0x30A1
            if (bmpMinusKatakana in 0 until 0x56) {
                val trailOffset = if (bmpMinusKatakana < 0x3F) 0x40 else 0x41
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = 0x83.toByte()
                dst[dstPos++] = (trailOffset + bmpMinusKatakana).toByte()
                srcPos++
                continue
            }

            val bmpMinusSpace = bmp - 0x3000
            if (bmpMinusSpace in 0 until 3) {
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = 0x81.toByte()
                dst[dstPos++] = (0x40 + bmpMinusSpace).toByte()
                srcPos++
                continue
            }

            when (bmp) {
                0xA5 -> {
                    if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0x5C.toByte()
                    srcPos++
                    continue
                }
                0x80 -> {
                    if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0x80.toByte()
                    srcPos++
                    continue
                }
                0x203E -> {
                    if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0x7E.toByte()
                    srcPos++
                    continue
                }
                in 0xFF61..0xFF9F -> {
                    if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = (bmp - (0xFF61 - 0xA1)).toByte()
                    srcPos++
                    continue
                }
                0x2212 -> {
                    if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0x81.toByte()
                    dst[dstPos++] = 0x7C.toByte()
                    srcPos++
                    continue
                }
            }

            val bmpMinusRoman = bmp - 0x2170
            val pointer =
                if (bmpMinusRoman in 0..(0x2179 - 0x2170)) {
                    10716 + bmpMinusRoman
                } else {
                    jis0208RangeEncode(bmp)
                        ?: if (bmp in 0xFA0E..0xFA2D || bmp == 0xF929 || bmp == 0xF9DC) {
                            position(IBM_KANJI, 0, IBM_KANJI.size, bmp)?.let { 10744 + it }
                        } else {
                            null
                        }
                        ?: jis0208SymbolEncode(bmp)
                }

            if (pointer == null) {
                return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
            }

            val lead = pointer / 188
            val leadOffset = if (lead < 0x1F) 0x81 else 0xC1
            val trail = pointer % 188
            val trailOffset = if (trail < 0x3F) 0x40 else 0x41

            if (dstPos + 2 > dst.size) {
                return Triple(EncoderResult.OutputFull, srcPos, dstPos)
            }
            dst[dstPos++] = (lead + leadOffset).toByte()
            dst[dstPos++] = (trail + trailOffset).toByte()
            srcPos++
        }

        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(encoding: Encoding): Encoder =
            Encoder(encoding, VariantEncoder.ShiftJis(ShiftJisEncoder()))
    }
}
