// port-lint: source euc_jp.rs
package io.github.kotlinmania.encodingrs

private sealed class EucJpPending {
    data object None : EucJpPending()

    data class Jis0208Lead(
        val lead: Int,
    ) : EucJpPending()

    data object Jis0212Shift : EucJpPending()

    data class Jis0212Lead(
        val lead: Int,
    ) : EucJpPending()

    data object HalfWidthKatakana : EucJpPending()

    fun count(): Int =
        when (this) {
            is None -> 0
            is Jis0208Lead, is Jis0212Shift, is HalfWidthKatakana -> 1
            is Jis0212Lead -> 2
        }
}

class EucJpDecoder internal constructor() {
    private var pending: EucJpPending = EucJpPending.None

    public fun isNone(): Boolean = inNeutralState()

    public fun inNeutralState(): Boolean = pending is EucJpPending.None

    private fun plusOneIfLead(byteLength: Int): Int =
        if (pending is EucJpPending.None) byteLength else byteLength + 1

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
            when (val currentPending = pending) {
                is EucJpPending.None -> {
                    val b = src[srcPos].toInt() and 0xFF
                    if (b < 0x80) {
                        if (dstPos >= dst.size) {
                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = b.toChar()
                        srcPos++
                        continue
                    }
                    when (b) {
                        0x8E -> {
                            pending = EucJpPending.HalfWidthKatakana
                            srcPos++
                        }
                        0x8F -> {
                            pending = EucJpPending.Jis0212Shift
                            srcPos++
                        }
                        in 0xA1..0xFE -> {
                            pending = EucJpPending.Jis0208Lead(b)
                            srcPos++
                        }
                        else -> {
                            srcPos++
                            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                        }
                    }
                }
                is EucJpPending.Jis0208Lead -> {
                    val lead = currentPending.lead
                    val byte = src[srcPos].toInt() and 0xFF
                    val leadMinusOffset = lead - 0xA1
                    val trailMinusOffset = byte - 0xA1

                    if (leadMinusOffset == 0x03 && trailMinusOffset in 0 until 0x53) {
                        if (dstPos >= dst.size) {
                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = (0x3041 + trailMinusOffset).toChar()
                        pending = EucJpPending.None
                        srcPos++
                    } else if (leadMinusOffset == 0x04 && trailMinusOffset in 0 until 0x56) {
                        if (dstPos >= dst.size) {
                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = (0x30A1 + trailMinusOffset).toChar()
                        pending = EucJpPending.None
                        srcPos++
                    } else if (trailMinusOffset !in 0..(0xFE - 0xA1)) {
                        pending = EucJpPending.None
                        if (byte < 0x80) {
                            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                        }
                        srcPos++
                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                    } else {
                        val pointer = leadMinusOffset * 94 + trailMinusOffset
                        val level1Pointer = pointer - 1410
                        if (level1Pointer in JIS0208_LEVEL1_KANJI.indices) {
                            if (dstPos >= dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = JIS0208_LEVEL1_KANJI[level1Pointer]
                            pending = EucJpPending.None
                            srcPos++
                        } else {
                            val level2Pointer = pointer - 4418
                            if (level2Pointer in JIS0208_LEVEL2_AND_ADDITIONAL_KANJI.indices) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = JIS0208_LEVEL2_AND_ADDITIONAL_KANJI[level2Pointer]
                                pending = EucJpPending.None
                                srcPos++
                            } else {
                                val ibmPointer = pointer - 8272
                                if (ibmPointer in IBM_KANJI.indices) {
                                    if (dstPos >= dst.size) {
                                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                    }
                                    dst[dstPos++] = IBM_KANJI[ibmPointer]
                                    pending = EucJpPending.None
                                    srcPos++
                                } else {
                                    val sym = jis0208SymbolDecode(pointer) ?: jis0208RangeDecode(pointer)
                                    if (sym != null) {
                                        if (dstPos >= dst.size) {
                                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                        }
                                        dst[dstPos++] = sym.toChar()
                                        pending = EucJpPending.None
                                        srcPos++
                                    } else {
                                        pending = EucJpPending.None
                                        srcPos++
                                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                                    }
                                }
                            }
                        }
                    }
                }
                is EucJpPending.Jis0212Shift -> {
                    val lead = src[srcPos].toInt() and 0xFF
                    val leadMinusOffset = lead - 0xA1
                    if (leadMinusOffset !in 0..(0xFE - 0xA1)) {
                        pending = EucJpPending.None
                        if (lead < 0x80) {
                            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                        }
                        srcPos++
                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                    }
                    pending = EucJpPending.Jis0212Lead(lead)
                    srcPos++
                }
                is EucJpPending.Jis0212Lead -> {
                    val lead = currentPending.lead
                    val byte = src[srcPos].toInt() and 0xFF
                    val leadMinusOffset = lead - 0xA1
                    val trailMinusOffset = byte - 0xA1

                    if (trailMinusOffset !in 0..(0xFE - 0xA1)) {
                        pending = EucJpPending.None
                        if (byte < 0x80) {
                            return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                        }
                        srcPos++
                        return Triple(DecoderResult.Malformed(3, 0), srcPos, dstPos)
                    }

                    val pointer = leadMinusOffset * 94 + trailMinusOffset
                    val pointerMinusKanji = pointer - 1410
                    if (pointerMinusKanji in JIS0212_KANJI.indices) {
                        if (dstPos >= dst.size) {
                            return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        }
                        dst[dstPos++] = JIS0212_KANJI[pointerMinusKanji]
                        pending = EucJpPending.None
                        srcPos++
                    } else {
                        val accented = jis0212AccentedDecode(pointer)
                        if (accented != null) {
                            if (dstPos >= dst.size) {
                                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            }
                            dst[dstPos++] = accented.toChar()
                            pending = EucJpPending.None
                            srcPos++
                        } else {
                            val pointerMinusUpperCyrillic = pointer - 597
                            if (pointerMinusUpperCyrillic in 0..(607 - 597)) {
                                if (dstPos >= dst.size) {
                                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                }
                                dst[dstPos++] = (0x0402 + pointerMinusUpperCyrillic).toChar()
                                pending = EucJpPending.None
                                srcPos++
                            } else {
                                val pointerMinusLowerCyrillic = pointer - 645
                                if (pointerMinusLowerCyrillic in 0..(655 - 645)) {
                                    if (dstPos >= dst.size) {
                                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                    }
                                    dst[dstPos++] = (0x0452 + pointerMinusLowerCyrillic).toChar()
                                    pending = EucJpPending.None
                                    srcPos++
                                } else {
                                    pending = EucJpPending.None
                                    srcPos++
                                    return Triple(DecoderResult.Malformed(3, 0), srcPos, dstPos)
                                }
                            }
                        }
                    }
                }
                is EucJpPending.HalfWidthKatakana -> {
                    val byte = src[srcPos].toInt() and 0xFF
                    val trailMinusOffset = byte - 0xA1
                    if (trailMinusOffset !in 0..(0xDF - 0xA1)) {
                        pending = EucJpPending.None
                        if (byte < 0x80) {
                            return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                        }
                        srcPos++
                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                    }
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = (0xFF61 + trailMinusOffset).toChar()
                    pending = EucJpPending.None
                    srcPos++
                }
            }
        }

        if (last && pending !is EucJpPending.None) {
            val count = pending.count()
            pending = EucJpPending.None
            return Triple(DecoderResult.Malformed(count, 0), srcPos, dstPos)
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): EucJpDecoder = EucJpDecoder()
    }
}

class EucJpEncoder internal constructor() {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        if (u16Length > Int.MAX_VALUE / 2) null else u16Length * 2

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        if (byteLength == Int.MAX_VALUE) null else byteLength + 1

    private fun encodeKanji(bmp: Int): Pair<Int, Int>? {
        if (0x4EDD == bmp) {
            return Pair(0xA1, 0xB8)
        }
        val level1 = jis0208Level1KanjiEucJpEncode(bmp)
        if (level1 != null) return level1
        val posL2 = jis0208Level2AndAdditionalKanjiEncode(bmp)
        if (posL2 != null) {
            val lead = (posL2 / 94) + 0xD0
            val trail = (posL2 % 94) + 0xA1
            return Pair(lead, trail)
        }
        val posIbm = position(IBM_KANJI, 0, IBM_KANJI.size, bmp)
        if (posIbm != null) {
            val lead = (posIbm / 94) + 0xF9
            val trail = (posIbm % 94) + 0xA1
            return Pair(lead, trail)
        }
        return null
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
                dst[dstPos++] = 0xA4.toByte()
                dst[dstPos++] = (0xA1 + bmpMinusHiragana).toByte()
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
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = 0xA5.toByte()
                dst[dstPos++] = (0xA1 + bmpMinusKatakana).toByte()
                srcPos++
                continue
            }

            val bmpMinusSpace = bmp - 0x3000
            if (bmpMinusSpace in 0 until 3) {
                if (dstPos + 2 > dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = 0xA1.toByte()
                dst[dstPos++] = (0xA1 + bmpMinusSpace).toByte()
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
                0x203E -> {
                    if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0x7E.toByte()
                    srcPos++
                    continue
                }
                in 0xFF61..0xFF9F -> {
                    if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0x8E.toByte()
                    dst[dstPos++] = (bmp - (0xFF61 - 0xA1)).toByte()
                    srcPos++
                    continue
                }
                0x2212 -> {
                    if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = 0xA1.toByte()
                    dst[dstPos++] = 0xDD.toByte()
                    srcPos++
                    continue
                }
            }

            val pointer =
                jis0208RangeEncode(bmp)
                    ?: if (bmp in 0xFA0E..0xFA2D || bmp == 0xF929 || bmp == 0xF9DC) {
                        position(IBM_KANJI, 0, IBM_KANJI.size, bmp)?.let {
                            val lead = (it / 94) + 0xF9
                            val trail = (it % 94) + 0xA1
                            if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                            dst[dstPos++] = lead.toByte()
                            dst[dstPos++] = trail.toByte()
                            srcPos++
                            null
                        }
                    } else {
                        null
                    }
                    ?: ibmSymbolEncode(bmp)
                    ?: jis0208SymbolEncode(bmp)

            if (pointer == null) {
                if (bmp !in 0xFA0E..0xFA2D && bmp != 0xF929 && bmp != 0xF9DC) {
                    return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
                }
                continue
            }

            val lead = (pointer / 94) + 0xA1
            val trail = (pointer % 94) + 0xA1
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
            Encoder(encoding, VariantEncoder.EucJp(EucJpEncoder()))
    }
}
