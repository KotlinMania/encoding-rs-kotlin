// port-lint: source encoding_rs/src/ascii.rs
package io.github.kotlinmania.encodingrs

/**
 * ASCII and Basic Latin conversion and validation helpers.
 */
public object Ascii {
    /**
     * Bitmask where the most significant bit of each byte in a word is set.
     */
    public const val ASCII_MASK: Long = -0x7f7f7f7f7f7f7f80L // 0x8080_8080_8080_8080L

    /**
     * Bitmask for basic Latin characters in UTF-16 code units.
     */
    public const val BASIC_LATIN_MASK: Long = -0x7f007f007f0080L // 0xFF80_FF80_FF80_FF80L

    /**
     * Copies ASCII bytes from `src` to `dst`.
     *
     * Returns `Pair(nonAsciiByte, offset)` if non-ASCII byte is found, or `null` if all bytes are ASCII.
     */
    public fun asciiToAscii(
        src: ByteArray,
        dst: ByteArray,
        len: Int = minOf(src.size, dst.size),
    ): Pair<Byte, Int>? {
        for (i in 0 until len) {
            val codeUnit = src[i]
            if ((codeUnit.toInt() and 0xFF) > 127) {
                return Pair(codeUnit, i)
            }
            dst[i] = codeUnit
        }
        return null
    }

    /**
     * Converts ASCII bytes from `src` to Basic Latin UTF-16 characters in `dst`.
     *
     * Returns `Pair(nonAsciiByte, offset)` if non-ASCII byte is found, or `null` if all bytes are ASCII.
     */
    public fun asciiToBasicLatin(
        src: ByteArray,
        dst: CharArray,
        len: Int = minOf(src.size, dst.size),
    ): Pair<Byte, Int>? {
        for (i in 0 until len) {
            val codeUnit = src[i]
            if ((codeUnit.toInt() and 0xFF) > 127) {
                return Pair(codeUnit, i)
            }
            dst[i] = (codeUnit.toInt() and 0xFF).toChar()
        }
        return null
    }

    /**
     * Converts Basic Latin UTF-16 characters from `src` to ASCII bytes in `dst`.
     *
     * Returns `Pair(nonAsciiChar, offset)` if non-ASCII character is found, or `null` if all characters are ASCII.
     */
    public fun basicLatinToAscii(
        src: CharArray,
        dst: ByteArray,
        len: Int = minOf(src.size, dst.size),
    ): Pair<Char, Int>? {
        for (i in 0 until len) {
            val codeUnit = src[i]
            if (codeUnit.code > 127) {
                return Pair(codeUnit, i)
            }
            dst[i] = codeUnit.code.toByte()
        }
        return null
    }

    /**
     * Unpacks Latin-1 bytes into UTF-16 characters without validation.
     */
    public fun unpackLatin1(
        src: ByteArray,
        dst: CharArray,
        len: Int = minOf(src.size, dst.size),
    ) {
        for (i in 0 until len) {
            dst[i] = (src[i].toInt() and 0xFF).toChar()
        }
    }

    /**
     * Packs UTF-16 characters into Latin-1 bytes.
     *
     * Returns `Pair(nonLatin1Char, offset)` if a character above U+00FF is encountered, or `null`.
     */
    public fun packLatin1(
        src: CharArray,
        dst: ByteArray,
        len: Int = minOf(src.size, dst.size),
    ): Pair<Char, Int>? {
        for (i in 0 until len) {
            val codeUnit = src[i]
            if (codeUnit.code > 255) {
                return Pair(codeUnit, i)
            }
            dst[i] = (codeUnit.code and 0xFF).toByte()
        }
        return null
    }

    /**
     * Validates that the byte slice contains only ASCII bytes.
     *
     * Returns `Pair(firstNonAsciiByte, offset)` if non-ASCII is found, or `null`.
     */
    public fun validateAscii(slice: ByteArray): Pair<Byte, Int>? {
        for (i in slice.indices) {
            val byte = slice[i]
            if ((byte.toInt() and 0xFF) >= 0x80) {
                return Pair(byte, i)
            }
        }
        return null
    }

    /**
     * Returns the index of the first non-ASCII byte, or `bytes.size` if all are ASCII.
     */
    public fun asciiValidUpTo(bytes: ByteArray): Int {
        val invalid = validateAscii(bytes)
        return invalid?.second ?: bytes.size
    }

    /**
     * Returns the index of the first byte that is not valid ASCII in ISO-2022-JP, or `bytes.size`.
     */
    public fun iso2022JpAsciiValidUpTo(bytes: ByteArray): Int {
        for (i in bytes.indices) {
            val b = bytes[i].toInt() and 0xFF
            if (b >= 0x80 || b == 0x1B || b == 0x0E || b == 0x0F) {
                return i
            }
        }
        return bytes.size
    }
}
