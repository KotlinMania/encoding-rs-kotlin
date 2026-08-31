// port-lint: source encoding_rs/src/variant.rs
package io.github.kotlinmania.encodingrs

internal sealed class VariantEncoding {
    data class SingleByte(
        val table: CharArray,
        val runBmpOffset: Int,
        val runByteOffset: Int,
        val runLength: Int,
    ) : VariantEncoding()

    data object Utf8 : VariantEncoding()

    data object Gb18030 : VariantEncoding()

    data object Gbk : VariantEncoding()

    data object Big5 : VariantEncoding()

    data object EucJp : VariantEncoding()

    data object Iso2022Jp : VariantEncoding()

    data object ShiftJis : VariantEncoding()

    data object EucKr : VariantEncoding()

    data object UserDefined : VariantEncoding()

    data object Replacement : VariantEncoding()

    data object Utf16Be : VariantEncoding()

    data object Utf16Le : VariantEncoding()

    fun newVariantDecoder(): VariantDecoder =
        when (this) {
            is SingleByte -> VariantDecoder.SingleByte(SingleByteDecoder.new(table))
            Utf8 -> VariantDecoder.Utf8(Utf8Decoder.new())
            Gb18030, Gbk -> VariantDecoder.Gb18030(Gb18030Decoder.new())
            Big5 -> VariantDecoder.Big5(Big5Decoder.new())
            EucJp -> VariantDecoder.EucJp(EucJpDecoder.new())
            Iso2022Jp -> VariantDecoder.Iso2022Jp(Iso2022JpDecoder.new())
            ShiftJis -> VariantDecoder.ShiftJis(ShiftJisDecoder.new())
            EucKr -> VariantDecoder.EucKr(EucKrDecoder.new())
            UserDefined -> VariantDecoder.UserDefined(UserDefinedDecoder.new())
            Replacement -> VariantDecoder.Replacement(ReplacementDecoder.new())
            Utf16Be -> VariantDecoder.Utf16Be(Utf16Decoder.new(false))
            Utf16Le -> VariantDecoder.Utf16Le(Utf16Decoder.new(true))
        }

    fun newEncoder(encoding: Encoding): Encoder =
        when (this) {
            is SingleByte -> Encoder(encoding, VariantEncoder.SingleByte(SingleByteEncoder.new(table, runBmpOffset, runByteOffset, runLength)))
            Utf8 -> Encoder(encoding, VariantEncoder.Utf8)
            Gb18030 -> Encoder(encoding, VariantEncoder.Gb18030(Gb18030Encoder(extended = true)))
            Gbk -> Encoder(encoding, VariantEncoder.Gbk(Gb18030Encoder(extended = false)))
            Big5 -> Encoder(encoding, VariantEncoder.Big5(Big5Encoder()))
            EucJp -> Encoder(encoding, VariantEncoder.EucJp(EucJpEncoder()))
            Iso2022Jp -> Encoder(encoding, VariantEncoder.Iso2022Jp(Iso2022JpEncoder()))
            ShiftJis -> Encoder(encoding, VariantEncoder.ShiftJis(ShiftJisEncoder()))
            EucKr -> Encoder(encoding, VariantEncoder.EucKr(EucKrEncoder()))
            UserDefined -> Encoder(encoding, VariantEncoder.UserDefined(UserDefinedEncoder()))
            Replacement -> Encoder(encoding, VariantEncoder.Replacement)
            Utf16Be -> Encoder(encoding, VariantEncoder.Utf16Be)
            Utf16Le -> Encoder(encoding, VariantEncoder.Utf16Le)
        }

    fun newVariantEncoder(encoding: Encoding): Encoder = newEncoder(encoding)

    fun isSingleByte(): Boolean =
        when (this) {
            is SingleByte, UserDefined -> true
            else -> false
        }
}

internal sealed class VariantDecoder {
    data class SingleByte(
        val decoder: SingleByteDecoder,
    ) : VariantDecoder()

    data class Utf8(
        val decoder: Utf8Decoder,
    ) : VariantDecoder()

    data class Gb18030(
        val decoder: Gb18030Decoder,
    ) : VariantDecoder()

    data class Big5(
        val decoder: Big5Decoder,
    ) : VariantDecoder()

    data class EucJp(
        val decoder: EucJpDecoder,
    ) : VariantDecoder()

    data class Iso2022Jp(
        val decoder: Iso2022JpDecoder,
    ) : VariantDecoder()

    data class ShiftJis(
        val decoder: ShiftJisDecoder,
    ) : VariantDecoder()

    data class EucKr(
        val decoder: EucKrDecoder,
    ) : VariantDecoder()

    data class UserDefined(
        val decoder: UserDefinedDecoder,
    ) : VariantDecoder()

    data class Replacement(
        val decoder: ReplacementDecoder,
    ) : VariantDecoder()

    data class Utf16Be(
        val decoder: Utf16Decoder,
    ) : VariantDecoder()

    data class Utf16Le(
        val decoder: Utf16Decoder,
    ) : VariantDecoder()

    fun maxUtf16BufferLength(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> decoder.maxUtf16BufferLength(byteLength)
            is Utf8 -> decoder.maxUtf16BufferLength(byteLength)
            is Gb18030 -> decoder.maxUtf16BufferLength(byteLength)
            is Big5 -> decoder.maxUtf16BufferLength(byteLength)
            is EucJp -> decoder.maxUtf16BufferLength(byteLength)
            is Iso2022Jp -> decoder.maxUtf16BufferLength(byteLength)
            is ShiftJis -> decoder.maxUtf16BufferLength(byteLength)
            is EucKr -> decoder.maxUtf16BufferLength(byteLength)
            is UserDefined -> decoder.maxUtf16BufferLength(byteLength)
            is Replacement -> decoder.maxUtf16BufferLength(byteLength)
            is Utf16Be -> decoder.maxUtf16BufferLength(byteLength)
            is Utf16Le -> decoder.maxUtf16BufferLength(byteLength)
        }

    fun maxUtf8BufferLength(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> decoder.maxUtf8BufferLength(byteLength)
            is Utf8 -> decoder.maxUtf8BufferLength(byteLength)
            is Gb18030 -> decoder.maxUtf8BufferLength(byteLength)
            is Big5 -> decoder.maxUtf8BufferLength(byteLength)
            is EucJp -> decoder.maxUtf8BufferLength(byteLength)
            is Iso2022Jp -> decoder.maxUtf8BufferLength(byteLength)
            is ShiftJis -> decoder.maxUtf8BufferLength(byteLength)
            is EucKr -> decoder.maxUtf8BufferLength(byteLength)
            is UserDefined -> decoder.maxUtf8BufferLength(byteLength)
            is Replacement -> decoder.maxUtf8BufferLength(byteLength)
            is Utf16Be -> decoder.maxUtf8BufferLength(byteLength)
            is Utf16Le -> decoder.maxUtf8BufferLength(byteLength)
        }

    fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Utf8 -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Gb18030 -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Big5 -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is EucJp -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Iso2022Jp -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is ShiftJis -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is EucKr -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is UserDefined -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Replacement -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Utf16Be -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Utf16Le -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
        }

    fun decodeToUtf16Raw(src: ByteArray, dst: CharArray, last: Boolean): Triple<DecoderResult, Int, Int> =
        when (this) {
            is SingleByte -> decoder.decodeToUtf16Raw(src, dst, last)
            is Utf8 -> decoder.decodeToUtf16Raw(src, dst, last)
            is Gb18030 -> decoder.decodeToUtf16Raw(src, dst, last)
            is Big5 -> decoder.decodeToUtf16Raw(src, dst, last)
            is EucJp -> decoder.decodeToUtf16Raw(src, dst, last)
            is Iso2022Jp -> decoder.decodeToUtf16Raw(src, dst, last)
            is ShiftJis -> decoder.decodeToUtf16Raw(src, dst, last)
            is EucKr -> decoder.decodeToUtf16Raw(src, dst, last)
            is UserDefined -> decoder.decodeToUtf16Raw(src, dst, last)
            is Replacement -> decoder.decodeToUtf16Raw(src, dst, last)
            is Utf16Be -> decoder.decodeToUtf16Raw(src, dst, last)
            is Utf16Le -> decoder.decodeToUtf16Raw(src, dst, last)
        }

    fun decodeToUtf8Raw(src: ByteArray, dst: ByteArray, last: Boolean): Triple<DecoderResult, Int, Int> =
        when (this) {
            is SingleByte -> decoder.decodeToUtf8Raw(src, dst, last)
            is Utf8 -> decoder.decodeToUtf8Raw(src, dst, last)
            is Replacement -> decoder.decodeToUtf8Raw(src, dst, last)
            is UserDefined -> decoder.decodeToUtf8Raw(src, dst, last)
            is Utf16Be -> decoder.decodeToUtf8Raw(src, dst, last)
            is Utf16Le -> decoder.decodeToUtf8Raw(src, dst, last)
            else -> {
                val tempDst = CharArray(dst.size)
                val (result, read, written) = decodeToUtf16Raw(src, tempDst, last)
                val (u16Read, u8Written) = Mem.convertUtf16ToUtf8Partial(tempDst.copyOfRange(0, written), dst)
                if (u16Read < written) {
                    Triple(DecoderResult.OutputFull, read, u8Written)
                } else {
                    Triple(result, read, u8Written)
                }
            }
        }

    fun latin1ByteCompatibleUpTo(buffer: ByteArray): Int? =
        when (this) {
            is SingleByte -> decoder.latin1ByteCompatibleUpTo(buffer)
            is Utf8 -> if (decoder.inNeutralState()) Encoding.asciiValidUpTo(buffer) else null
            is Gb18030 -> if (decoder.inNeutralState()) Encoding.asciiValidUpTo(buffer) else null
            is Big5 -> if (decoder.inNeutralState()) Encoding.asciiValidUpTo(buffer) else null
            is EucJp -> if (decoder.inNeutralState()) Encoding.asciiValidUpTo(buffer) else null
            is Iso2022Jp -> if (decoder.inNeutralState()) Encoding.iso2022JpAsciiValidUpTo(buffer) else null
            is ShiftJis -> if (decoder.inNeutralState()) Encoding.asciiValidUpTo(buffer) else null
            is EucKr -> if (decoder.inNeutralState()) Encoding.asciiValidUpTo(buffer) else null
            is UserDefined -> Encoding.asciiValidUpTo(buffer)
            is Replacement, is Utf16Be, is Utf16Le -> null
        }
}

internal sealed class VariantEncoder {
    data class SingleByte(
        val encoder: SingleByteEncoder,
    ) : VariantEncoder()

    data object Utf8 : VariantEncoder()

    data class Gb18030(
        val encoder: Gb18030Encoder,
    ) : VariantEncoder()

    data class Gbk(
        val encoder: Gb18030Encoder,
    ) : VariantEncoder()

    data class Big5(
        val encoder: Big5Encoder,
    ) : VariantEncoder()

    data class EucJp(
        val encoder: EucJpEncoder,
    ) : VariantEncoder()

    data class Iso2022Jp(
        val encoder: Iso2022JpEncoder,
    ) : VariantEncoder()

    data class ShiftJis(
        val encoder: ShiftJisEncoder,
    ) : VariantEncoder()

    data class EucKr(
        val encoder: EucKrEncoder,
    ) : VariantEncoder()

    data class UserDefined(
        val encoder: UserDefinedEncoder,
    ) : VariantEncoder()

    data object Replacement : VariantEncoder()

    data object Utf16Be : VariantEncoder()

    data object Utf16Le : VariantEncoder()

    fun hasPendingState(): Boolean =
        when (this) {
            is Iso2022Jp -> encoder.hasPendingState()
            else -> false
        }

    fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        when (this) {
            is SingleByte -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is UserDefined -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            Utf8 -> if (u16Length > Int.MAX_VALUE / 3) null else u16Length * 3
            is Gb18030 -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is Gbk -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is Big5 -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is EucJp -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is Iso2022Jp -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is ShiftJis -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is EucKr -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            Utf16Be, Utf16Le -> if (u16Length > Int.MAX_VALUE / 2) null else u16Length * 2
            Replacement -> u16Length
        }

    fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is UserDefined -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            Utf8 -> if (byteLength > Int.MAX_VALUE) null else byteLength
            is Gb18030 -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is Gbk -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is Big5 -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is EucJp -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is Iso2022Jp -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is ShiftJis -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            is EucKr -> encoder.maxBufferLengthFromUtf8WithoutReplacement(byteLength)
            Utf16Be, Utf16Le -> if (byteLength > (Int.MAX_VALUE / 2) - 1) null else (byteLength + 1) * 2
            Replacement -> byteLength
        }

    fun encodeFromUtf16Raw(src: CharArray, dst: ByteArray, last: Boolean): Triple<EncoderResult, Int, Int> =
        when (this) {
            is SingleByte -> encoder.encodeFromUtf16Raw(src, dst, last)
            is UserDefined -> encoder.encodeFromUtf16Raw(src, dst, last)
            Utf8 -> Utf8Encoder().encodeFromUtf16Raw(src, dst, last)
            is Gb18030 -> encoder.encodeFromUtf16Raw(src, dst, last)
            is Gbk -> encoder.encodeFromUtf16Raw(src, dst, last)
            is Big5 -> encoder.encodeFromUtf16Raw(src, dst, last)
            is EucJp -> encoder.encodeFromUtf16Raw(src, dst, last)
            is Iso2022Jp -> encoder.encodeFromUtf16Raw(src, dst, last)
            is ShiftJis -> encoder.encodeFromUtf16Raw(src, dst, last)
            is EucKr -> encoder.encodeFromUtf16Raw(src, dst, last)
            Utf16Be -> {
                var s = 0
                var d = 0
                while (s < src.size) {
                    if (d + 2 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                    val code = src[s++].code
                    dst[d++] = (code shr 8).toByte()
                    dst[d++] = (code and 0xFF).toByte()
                }
                Triple(EncoderResult.InputEmpty, s, d)
            }
            Utf16Le -> {
                var s = 0
                var d = 0
                while (s < src.size) {
                    if (d + 2 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                    val code = src[s++].code
                    dst[d++] = (code and 0xFF).toByte()
                    dst[d++] = (code shr 8).toByte()
                }
                Triple(EncoderResult.InputEmpty, s, d)
            }
            Replacement -> {
                var s = 0
                var d = 0
                while (s < src.size && d < dst.size) {
                    dst[d++] = src[s++].code.toByte()
                }
                Triple(EncoderResult.InputEmpty, s, d)
            }
        }

    fun encodeFromUtf8Raw(src: String, dst: ByteArray, last: Boolean): Triple<EncoderResult, Int, Int> =
        when (this) {
            Utf8 -> Utf8Encoder().encodeFromUtf8Raw(src, dst, last)
            else -> encodeFromUtf16Raw(src.toCharArray(), dst, last)
        }
}
