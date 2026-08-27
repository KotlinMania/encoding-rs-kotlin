// port-lint: source encoding_rs/src/replacement.rs
package io.github.kotlinmania.encodingrs

public class ReplacementDecoder internal constructor(
    private var emitted: Boolean = false,
) {
    public fun maxUtf16BufferLength(u16Length: Int): Int? = 1

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? = 3

    public fun maxUtf8BufferLength(byteLength: Int): Int? = 3

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        if (last) Unit
        if (emitted || src.isEmpty()) {
            return Triple(DecoderResult.InputEmpty, src.size, 0)
        } else if (dst.isEmpty()) {
            return Triple(DecoderResult.OutputFull, 0, 0)
        } else {
            emitted = true
            return Triple(DecoderResult.Malformed(1, 0), 1, 0)
        }
    }

    public fun decodeToUtf8Raw(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        if (last) Unit
        if (emitted || src.isEmpty()) {
            return Triple(DecoderResult.InputEmpty, src.size, 0)
        } else if (dst.size < 3) {
            return Triple(DecoderResult.OutputFull, 0, 0)
        } else {
            emitted = true
            return Triple(DecoderResult.Malformed(1, 0), 1, 0)
        }
    }

    public companion object {
        public fun new(): ReplacementDecoder = ReplacementDecoder(emitted = false)
    }
}
