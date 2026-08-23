package io.github.kotlinmania.encodingrs

/**
 * Result of a (potentially partial) decode or encode operation with replacement.
 */
public enum class CoderResult {
    /**
     * The input was exhausted.
     */
    InputEmpty,

    /**
     * The converter cannot produce another unit of output, because the output
     * buffer does not have enough space left.
     */
    OutputFull,
}

/**
 * Result of a (potentially partial) decode operation without replacement.
 */
public sealed class DecoderResult {
    /**
     * The input was exhausted.
     */
    public data object InputEmpty : DecoderResult()

    /**
     * The decoder cannot produce another unit of output, because the output
     * buffer does not have enough space left.
     */
    public data object OutputFull : DecoderResult()

    /**
     * The decoder encountered a malformed byte sequence.
     *
     * The first integer indicates the length of the malformed byte sequence.
     * The second integer indicates the number of bytes that were consumed after the malformed sequence.
     */
    public data class Malformed(public val length: UByte, public val consumedAfter: UByte) : DecoderResult() {
        public constructor(length: Int, consumedAfter: Int) : this(length.toUByte(), consumedAfter.toUByte())
    }
}

/**
 * Result of a (potentially partial) encode operation without replacement.
 */
public sealed class EncoderResult {
    /**
     * The input was exhausted.
     */
    public data object InputEmpty : EncoderResult()

    /**
     * The encoder cannot produce another unit of output, because the output
     * buffer does not have enough space left.
     */
    public data object OutputFull : EncoderResult()

    /**
     * The encoder encountered an unmappable character.
     */
    public data class Unmappable(public val character: Char) : EncoderResult() {
        public constructor(codePoint: Int) : this(codePoint.toChar())
    }

    public companion object {
        public fun unmappableFromBmp(bmp: UShort): EncoderResult =
            Unmappable(bmp.toInt().toChar())
    }
}
