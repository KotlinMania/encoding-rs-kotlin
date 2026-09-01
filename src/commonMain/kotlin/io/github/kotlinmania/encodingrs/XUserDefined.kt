// port-lint: source x_user_defined.rs
package io.github.kotlinmania.encodingrs

public class UserDefinedDecoder {
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
        val (pending, length) =
            if (dst.size < src.size) {
                Pair(DecoderResult.OutputFull, dst.size)
            } else {
                Pair(DecoderResult.InputEmpty, src.size)
            }
        for (i in 0 until length) {
            val unit = src[i].toInt() and 0xFF
            dst[i] =
                if (unit < 0x80) {
                    unit.toChar()
                } else {
                    (unit + 0xF700).toChar()
                }
        }
        return Triple(pending, length, length)
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
                if (dstPos + 3 > dst.size) {
                    return Triple(DecoderResult.OutputFull, srcPos, dstPos)
                }
                val codePoint = b + 0xF700
                dst[dstPos++] = (0xE0 or (codePoint shr 12)).toByte()
                dst[dstPos++] = (0x80 or ((codePoint shr 6) and 0x3F)).toByte()
                dst[dstPos++] = (0x80 or (codePoint and 0x3F)).toByte()
                srcPos++
            }
        }
        return Triple(DecoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): UserDefinedDecoder = UserDefinedDecoder()
    }
}

public class UserDefinedEncoder {
    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? = u16Length

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? = byteLength

    public fun encodeFromUtf16Raw(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> {
        if (last) Unit
        var srcPos = 0
        var dstPos = 0
        while (srcPos < src.size) {
            val c = src[srcPos]
            if (c <= '\u007F') {
                if (dstPos >= dst.size) {
                    return Triple(EncoderResult.OutputFull, srcPos, dstPos)
                }
                dst[dstPos++] = c.code.toByte()
                srcPos++
                continue
            }
            if (c < '\uF780' || c > '\uF7FF') {
                return Triple(EncoderResult.Unmappable(c), srcPos, dstPos)
            }
            if (dstPos >= dst.size) {
                return Triple(EncoderResult.OutputFull, srcPos, dstPos)
            }
            dst[dstPos++] = (c.code - 0xF700).toByte()
            srcPos++
        }
        return Triple(EncoderResult.InputEmpty, srcPos, dstPos)
    }

    public companion object {
        public fun new(): UserDefinedEncoder = UserDefinedEncoder()
    }
}
