// port-lint: source encoding_rs/src/iso_2022_jp.rs
package io.github.kotlinmania.encodingrs

private enum class Iso2022JpDecoderState {
    Ascii,
    Roman,
    Katakana,
    LeadByte,
    TrailByte,
    EscapeStart,
    Escape,
}

public class Iso2022JpDecoder internal constructor() {
    private var decoderState: Iso2022JpDecoderState = Iso2022JpDecoderState.Ascii
    private var outputState: Iso2022JpDecoderState = Iso2022JpDecoderState.Ascii
    private var lead: Int = 0
    private var outputFlag: Boolean = false
    private var pendingPrepended: Boolean = false

    public fun inNeutralState(): Boolean =
        decoderState == Iso2022JpDecoderState.Ascii &&
            outputState == Iso2022JpDecoderState.Ascii &&
            lead == 0 &&
            !outputFlag &&
            !pendingPrepended

    private fun extraToInputFromState(byteLength: Int): Int {
        val leadVal = if (lead == 0 || pendingPrepended) 0 else 1
        val escVal =
            when (decoderState) {
                Iso2022JpDecoderState.Escape, Iso2022JpDecoderState.EscapeStart -> 1
                else -> 0
            }
        return byteLength + leadVal + escVal
    }

    private fun extraToOutputFromState(): Int =
        if (lead != 0 && pendingPrepended) {
            1 + if (outputFlag) 1 else 0
        } else {
            if (outputFlag) 1 else 0
        }

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        extraToOutputFromState() + extraToInputFromState(byteLength)

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        maxUtf8BufferLength(byteLength)

    public fun maxUtf8BufferLength(byteLength: Int): Int? {
        val total = extraToOutputFromState() + extraToInputFromState(byteLength)
        if (total > Int.MAX_VALUE / 3) return null
        return total * 3
    }

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var srcPos = 0
        var dstPos = 0

        if (pendingPrepended) {
            if (dstPos >= dst.size) {
                return Triple(DecoderResult.OutputFull, srcPos, dstPos)
            }
            pendingPrepended = false
            outputFlag = false
            when (decoderState) {
                Iso2022JpDecoderState.Ascii, Iso2022JpDecoderState.Roman -> {
                    dst[dstPos++] = lead.toChar()
                    lead = 0
                }
                Iso2022JpDecoderState.Katakana -> {
                    dst[dstPos++] = (lead - 0x21 + 0xFF61).toChar()
                    lead = 0
                }
                Iso2022JpDecoderState.LeadByte -> {
                    decoderState = Iso2022JpDecoderState.TrailByte
                }
                else -> {}
            }
        }

        while (srcPos < src.size) {
            val b = src[srcPos].toInt() and 0xFF

            when (decoderState) {
                Iso2022JpDecoderState.Ascii -> {
                    if (b == 0x1B) {
                        decoderState = Iso2022JpDecoderState.EscapeStart
                        srcPos++
                        continue
                    }
                    outputFlag = false
                    if (b > 0x7F || b == 0x0E || b == 0x0F) {
                        srcPos++
                        return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                    }
                    if (dstPos >= dst.size) {
                        return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    }
                    dst[dstPos++] = b.toChar()
                    srcPos++
                }
                Iso2022JpDecoderState.Roman -> {
                    if (b == 0x1B) {
                        decoderState = Iso2022JpDecoderState.EscapeStart
                        srcPos++
                        continue
                    }
                    outputFlag = false
                    if (b == 0x5C) {
                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = 0x00A5.toChar()
                        srcPos++
                        continue
                    }
                    if (b == 0x7E) {
                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = 0x203E.toChar()
                        srcPos++
                        continue
                    }
                    if (b > 0x7F || b == 0x0E || b == 0x0F) {
                        srcPos++
                        return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                    }
                    if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                    dst[dstPos++] = b.toChar()
                    srcPos++
                }
                Iso2022JpDecoderState.Katakana -> {
                    if (b == 0x1B) {
                        decoderState = Iso2022JpDecoderState.EscapeStart
                        srcPos++
                        continue
                    }
                    outputFlag = false
                    if (b in 0x21..0x5F) {
                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = (b - 0x21 + 0xFF61).toChar()
                        srcPos++
                        continue
                    }
                    srcPos++
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                Iso2022JpDecoderState.LeadByte -> {
                    if (b == 0x1B) {
                        decoderState = Iso2022JpDecoderState.EscapeStart
                        srcPos++
                        continue
                    }
                    outputFlag = false
                    if (b in 0x21..0x7E) {
                        lead = b
                        decoderState = Iso2022JpDecoderState.TrailByte
                        srcPos++
                        continue
                    }
                    srcPos++
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                Iso2022JpDecoderState.TrailByte -> {
                    if (b == 0x1B) {
                        decoderState = Iso2022JpDecoderState.EscapeStart
                        srcPos++
                        return Triple(DecoderResult.Malformed(1, 1), srcPos, dstPos)
                    }
                    decoderState = Iso2022JpDecoderState.LeadByte
                    val jis0208LeadMinusOffset = lead - 0x21
                    val trailMinusOffset = b - 0x21

                    if (jis0208LeadMinusOffset == 0x03 && trailMinusOffset in 0 until 0x53) {
                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = (0x3041 + trailMinusOffset).toChar()
                        srcPos++
                        continue
                    } else if (jis0208LeadMinusOffset == 0x04 && trailMinusOffset in 0 until 0x56) {
                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = (0x30A1 + trailMinusOffset).toChar()
                        srcPos++
                        continue
                    } else if (trailMinusOffset !in 0..(0xFE - 0xA1)) {
                        srcPos++
                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                    } else {
                        val pointer = jis0208LeadMinusOffset * 94 + trailMinusOffset
                        val level1Pointer = pointer - 1410
                        if (level1Pointer in JIS0208_LEVEL1_KANJI.indices) {
                            if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                            dst[dstPos++] = JIS0208_LEVEL1_KANJI[level1Pointer]
                            srcPos++
                            continue
                        } else {
                            val level2Pointer = pointer - 4418
                            if (level2Pointer in JIS0208_LEVEL2_AND_ADDITIONAL_KANJI.indices) {
                                if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                dst[dstPos++] = JIS0208_LEVEL2_AND_ADDITIONAL_KANJI[level2Pointer]
                                srcPos++
                                continue
                            } else {
                                val ibmPointer = pointer - 8272
                                if (ibmPointer in IBM_KANJI.indices) {
                                    if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                    dst[dstPos++] = IBM_KANJI[ibmPointer]
                                    srcPos++
                                    continue
                                } else {
                                    val sym = jis0208SymbolDecode(pointer) ?: jis0208RangeDecode(pointer)
                                    if (sym != null) {
                                        if (dstPos >= dst.size) return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                                        dst[dstPos++] = sym.toChar()
                                        srcPos++
                                        continue
                                    } else {
                                        srcPos++
                                        return Triple(DecoderResult.Malformed(2, 0), srcPos, dstPos)
                                    }
                                }
                            }
                        }
                    }
                }
                Iso2022JpDecoderState.EscapeStart -> {
                    if (b == 0x24 || b == 0x28) {
                        lead = b
                        decoderState = Iso2022JpDecoderState.Escape
                        srcPos++
                        continue
                    }
                    outputFlag = false
                    decoderState = outputState
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                Iso2022JpDecoderState.Escape -> {
                    var state: Iso2022JpDecoderState? = null
                    if (lead == 0x28 && b == 0x42) {
                        state = Iso2022JpDecoderState.Ascii
                    } else if (lead == 0x28 && b == 0x4A) {
                        state = Iso2022JpDecoderState.Roman
                    } else if (lead == 0x28 && b == 0x49) {
                        state = Iso2022JpDecoderState.Katakana
                    } else if (lead == 0x24 && (b == 0x40 || b == 0x42)) {
                        state = Iso2022JpDecoderState.LeadByte
                    }

                    if (state != null) {
                        lead = 0
                        decoderState = state
                        outputState = state
                        val flag = outputFlag
                        outputFlag = true
                        srcPos++
                        if (flag) {
                            return Triple(DecoderResult.Malformed(3, 3), srcPos, dstPos)
                        }
                        continue
                    } else {
                        pendingPrepended = true
                        outputFlag = false
                        decoderState = outputState
                        return Triple(DecoderResult.Malformed(1, 1), srcPos, dstPos)
                    }
                }
            }
        }

        if (last) {
            when (decoderState) {
                Iso2022JpDecoderState.TrailByte, Iso2022JpDecoderState.EscapeStart -> {
                    decoderState = outputState
                    return Triple(DecoderResult.Malformed(1, 0), srcPos, dstPos)
                }
                Iso2022JpDecoderState.Escape -> {
                    pendingPrepended = true
                    decoderState = outputState
                    return Triple(DecoderResult.Malformed(1, 1), srcPos, dstPos)
                }
                else -> {}
            }
        }

        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): Iso2022JpDecoder = Iso2022JpDecoder()
    }
}

private enum class Iso2022JpEncoderState {
    Ascii,
    Roman,
    Jis0208,
}

public class Iso2022JpEncoder internal constructor() {
    private var state: Iso2022JpEncoderState = Iso2022JpEncoderState.Ascii

    public fun hasPendingState(): Boolean = state != Iso2022JpEncoderState.Ascii

    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? {
        if (u16Length > Int.MAX_VALUE / 5) return null
        return 3 + u16Length * 4 + (u16Length + 1) / 2
    }

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? {
        if (byteLength > Int.MAX_VALUE / 3 - 1) return null
        return 3 + byteLength * 3
    }

    private fun isKanjiMapped(bmp: Int): Boolean {
        if (0x4EDD == bmp) return true
        if (jis0208Level1KanjiShiftJisEncode(bmp) != null) return true
        if (jis0208Level2AndAdditionalKanjiEncode(bmp) != null) return true
        if (position(IBM_KANJI, 0, IBM_KANJI.size, bmp) != null) return true
        return false
    }

    private fun isMappedForTwoByteEncode(bmp: Int): Boolean {
        val bmpMinusHiragana = bmp - 0x3041
        if (bmpMinusHiragana in 0 until 0x53) return true
        if (bmp in 0x4E00..0x9FA0) return isKanjiMapped(bmp)
        val bmpMinusKatakana = bmp - 0x30A1
        if (bmpMinusKatakana in 0 until 0x56) return true
        val bmpMinusSpace = bmp - 0x3000
        if (bmpMinusSpace in 0 until 3) return true
        if (bmp in 0xFF61..0xFF9F) return true
        if (bmp == 0x2212) return true
        if (jis0208RangeEncode(bmp) != null) return true
        if (bmp in 0xFA0E..0xFA2D || bmp == 0xF929 || bmp == 0xF9DC) return true
        if (ibmSymbolEncode(bmp) != null) return true
        if (jis0208SymbolEncode(bmp) != null) return true
        return false
    }

    private fun encodeKanji(bmp: Int): Pair<Int, Int>? {
        if (0x4EDD == bmp) return Pair(0x21, 0xB8 - 0x80)
        val l1 = jis0208Level1KanjiIso2022JpEncode(bmp)
        if (l1 != null) return l1
        val posL2 = jis0208Level2AndAdditionalKanjiEncode(bmp)
        if (posL2 != null) {
            val lead = (posL2 / 94) + (0xD0 - 0x80)
            val trail = (posL2 % 94) + 0x21
            return Pair(lead, trail)
        }
        val posIbm = position(IBM_KANJI, 0, IBM_KANJI.size, bmp)
        if (posIbm != null) {
            val lead = (posIbm / 94) + (0xF9 - 0x80)
            val trail = (posIbm % 94) + 0x21
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

            when (state) {
                Iso2022JpEncoderState.Ascii -> {
                    if (c == '\u000E' || c == '\u000F' || c == '\u001B') {
                        return Triple(EncoderResult.Unmappable(0xFFFD), srcPos + 1, dstPos)
                    }
                    if (bmp <= 0x7F) {
                        if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = bmp.toByte()
                        srcPos++
                        continue
                    }
                    if (c == '\u00A5' || c == '\u203E') {
                        if (dstPos + 3 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        state = Iso2022JpEncoderState.Roman
                        dst[dstPos++] = 0x1B.toByte()
                        dst[dstPos++] = 0x28.toByte()
                        dst[dstPos++] = 0x4A.toByte()
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
                    if (isMappedForTwoByteEncode(bmp)) {
                        if (dstPos + 3 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        state = Iso2022JpEncoderState.Jis0208
                        dst[dstPos++] = 0x1B.toByte()
                        dst[dstPos++] = 0x24.toByte()
                        dst[dstPos++] = 0x42.toByte()
                        continue
                    }
                    return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
                }
                Iso2022JpEncoderState.Roman -> {
                    if (c == '\u000E' || c == '\u000F' || c == '\u001B') {
                        return Triple(EncoderResult.Unmappable(0xFFFD), srcPos + 1, dstPos)
                    }
                    if (c == '\\' || c == '~') {
                        if (dstPos + 3 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        state = Iso2022JpEncoderState.Ascii
                        dst[dstPos++] = 0x1B.toByte()
                        dst[dstPos++] = 0x28.toByte()
                        dst[dstPos++] = 0x42.toByte()
                        continue
                    }
                    if (bmp <= 0x7F) {
                        if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = bmp.toByte()
                        srcPos++
                        continue
                    }
                    if (c == '\u00A5') {
                        if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = 0x5C.toByte()
                        srcPos++
                        continue
                    }
                    if (c == '\u203E') {
                        if (dstPos >= dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = 0x7E.toByte()
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
                    if (isMappedForTwoByteEncode(bmp)) {
                        if (dstPos + 3 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        state = Iso2022JpEncoderState.Jis0208
                        dst[dstPos++] = 0x1B.toByte()
                        dst[dstPos++] = 0x24.toByte()
                        dst[dstPos++] = 0x42.toByte()
                        continue
                    }
                    return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
                }
                Iso2022JpEncoderState.Jis0208 -> {
                    if (bmp <= 0x7F) {
                        if (dstPos + 3 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        state = Iso2022JpEncoderState.Ascii
                        dst[dstPos++] = 0x1B.toByte()
                        dst[dstPos++] = 0x28.toByte()
                        dst[dstPos++] = 0x42.toByte()
                        continue
                    }
                    if (c == '\u00A5' || c == '\u203E') {
                        if (dstPos + 3 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        state = Iso2022JpEncoderState.Roman
                        dst[dstPos++] = 0x1B.toByte()
                        dst[dstPos++] = 0x28.toByte()
                        dst[dstPos++] = 0x4A.toByte()
                        continue
                    }
                    if (c.isHighSurrogate()) {
                        state = Iso2022JpEncoderState.Ascii
                        if (dstPos + 3 <= dst.size) {
                            dst[dstPos++] = 0x1B.toByte()
                            dst[dstPos++] = 0x28.toByte()
                            dst[dstPos++] = 0x42.toByte()
                        }
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
                        state = Iso2022JpEncoderState.Ascii
                        if (dstPos + 3 <= dst.size) {
                            dst[dstPos++] = 0x1B.toByte()
                            dst[dstPos++] = 0x28.toByte()
                            dst[dstPos++] = 0x42.toByte()
                        }
                        return Triple(EncoderResult.Unmappable(0xFFFD), srcPos + 1, dstPos)
                    }

                    val bmpMinusHiragana = bmp - 0x3041
                    if (bmpMinusHiragana in 0 until 0x53) {
                        if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                        dst[dstPos++] = 0x24.toByte()
                        dst[dstPos++] = (0x21 + bmpMinusHiragana).toByte()
                        srcPos++
                        continue
                    } else if (bmp in 0x4E00..0x9FA0) {
                        val kanji = encodeKanji(bmp)
                        if (kanji != null) {
                            if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                            dst[dstPos++] = kanji.first.toByte()
                            dst[dstPos++] = kanji.second.toByte()
                            srcPos++
                            continue
                        } else {
                            state = Iso2022JpEncoderState.Ascii
                            if (dstPos + 3 <= dst.size) {
                                dst[dstPos++] = 0x1B.toByte()
                                dst[dstPos++] = 0x28.toByte()
                                dst[dstPos++] = 0x42.toByte()
                            }
                            return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
                        }
                    } else {
                        val bmpMinusKatakana = bmp - 0x30A1
                        if (bmpMinusKatakana in 0 until 0x56) {
                            if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                            dst[dstPos++] = 0x25.toByte()
                            dst[dstPos++] = (0x21 + bmpMinusKatakana).toByte()
                            srcPos++
                            continue
                        } else {
                            val bmpMinusSpace = bmp - 0x3000
                            if (bmpMinusSpace in 0 until 3) {
                                if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                dst[dstPos++] = 0x21.toByte()
                                dst[dstPos++] = (0x21 + bmpMinusSpace).toByte()
                                srcPos++
                                continue
                            }
                            val bmpMinusHalfWidth = bmp - 0xFF61
                            if (bmpMinusHalfWidth in 0..(0xFF9F - 0xFF61)) {
                                val lead = if (bmp != 0xFF70 && bmp in 0xFF66..0xFF9D) 0x25 else 0x21
                                val trail = ISO_2022_JP_HALF_WIDTH_TRAIL[bmpMinusHalfWidth].toInt() and 0xFF
                                if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                dst[dstPos++] = lead.toByte()
                                dst[dstPos++] = trail.toByte()
                                srcPos++
                                continue
                            } else if (bmp == 0x2212) {
                                if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                dst[dstPos++] = 0x21.toByte()
                                dst[dstPos++] = 0x5D.toByte()
                                srcPos++
                                continue
                            } else {
                                val rPointer = jis0208RangeEncode(bmp)
                                if (rPointer != null) {
                                    val lead = (rPointer / 94) + 0x21
                                    val trail = (rPointer % 94) + 0x21
                                    if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                    dst[dstPos++] = lead.toByte()
                                    dst[dstPos++] = trail.toByte()
                                    srcPos++
                                    continue
                                }
                                if (bmp in 0xFA0E..0xFA2D || bmp == 0xF929 || bmp == 0xF9DC) {
                                    val pos = position(IBM_KANJI, 0, IBM_KANJI.size, bmp)
                                    if (pos != null) {
                                        val lead = (pos / 94) + (0xF9 - 0x80)
                                        val trail = (pos % 94) + 0x21
                                        if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                        dst[dstPos++] = lead.toByte()
                                        dst[dstPos++] = trail.toByte()
                                        srcPos++
                                        continue
                                    }
                                }
                                val symPointer = ibmSymbolEncode(bmp) ?: jis0208SymbolEncode(bmp)
                                if (symPointer != null) {
                                    val lead = (symPointer / 94) + 0x21
                                    val trail = (symPointer % 94) + 0x21
                                    if (dstPos + 2 > dst.size) return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                                    dst[dstPos++] = lead.toByte()
                                    dst[dstPos++] = trail.toByte()
                                    srcPos++
                                    continue
                                }

                                state = Iso2022JpEncoderState.Ascii
                                if (dstPos + 3 <= dst.size) {
                                    dst[dstPos++] = 0x1B.toByte()
                                    dst[dstPos++] = 0x28.toByte()
                                    dst[dstPos++] = 0x42.toByte()
                                }
                                return Triple(EncoderResult.Unmappable(bmp), srcPos + 1, dstPos)
                            }
                        }
                    }
                }
            }
        }

        if (last && state != Iso2022JpEncoderState.Ascii) {
            if (dstPos + 3 > dst.size) {
                return Triple(EncoderResult.OutputFull, srcPos, dstPos)
            }
            state = Iso2022JpEncoderState.Ascii
            dst[dstPos++] = 0x1B.toByte()
            dst[dstPos++] = 0x28.toByte()
            dst[dstPos++] = 0x42.toByte()
        }

        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(encoding: Encoding): Encoder =
            Encoder(encoding, VariantEncoder.Iso2022Jp(Iso2022JpEncoder()))
    }
}
