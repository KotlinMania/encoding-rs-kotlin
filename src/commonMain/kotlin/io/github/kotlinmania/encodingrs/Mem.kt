// port-lint: source encoding_rs/src/mem.rs
package io.github.kotlinmania.encodingrs

/**
 * Classification of text as Latin1 (all code points are below U+0100),
 * left-to-right with some non-Latin1 characters or as containing at least
 * some right-to-left characters.
 */
public enum class Latin1Bidi {
    /**
     * Every character is below U+0100.
     */
    Latin1,

    /**
     * There is at least one character that's U+0100 or higher, but there
     * are no right-to-left characters.
     */
    LeftToRight,

    /**
     * There is at least one right-to-left character.
     */
    Bidi,
}

/**
 * Functions for converting between different in-RAM representations of text
 * and for quickly checking if the Unicode Bidirectional Algorithm can be
 * avoided.
 */
public object Mem {
    /**
     * Checks whether the buffer is all-ASCII.
     */
    public fun isAscii(buffer: ByteArray): Boolean =
        buffer.all { (it.toInt() and 0xFF) <= 0x7F }

    /**
     * Checks whether the buffer is all-Basic Latin (i.e. UTF-16 representing
     * only ASCII characters).
     */
    public fun isBasicLatin(buffer: CharArray): Boolean =
        buffer.all { it.code <= 0x7F }

    /**
     * Checks whether the buffer is valid UTF-8 representing only code points
     * less than or equal to U+00FF.
     */
    public fun isUtf8Latin1(buffer: ByteArray): Boolean =
        utf8Latin1UpTo(buffer) == buffer.size

    internal fun isUtf8Latin1Impl(buffer: ByteArray): Boolean =
        isUtf8Latin1(buffer)

    /**
     * Checks whether the buffer represents only code points less than or equal
     * to U+00FF.
     */
    public fun isStrLatin1(buffer: String): Boolean =
        strLatin1UpTo(buffer) == buffer.length

    /**
     * Checks whether the buffer represents only code point less than or equal
     * to U+00FF.
     */
    public fun isUtf16Latin1(buffer: CharArray): Boolean =
        buffer.all { it.code <= 0xFF }

    /**
     * Checks whether a scalar value triggers right-to-left processing.
     */
    public fun isCharBidi(codePoint: Int): Boolean {
        if (codePoint < 0x0590) {
            return false
        }
        if (codePoint in 0x0900 until 0xFB1D) {
            if (codePoint in 0x200F..0x2067) {
                return codePoint == 0x200F ||
                    codePoint == 0x202B ||
                    codePoint == 0x202E ||
                    codePoint == 0x2067
            }
            return false
        }
        if (codePoint > 0x1EFFF) {
            return false
        }
        if (codePoint in 0x11000 until 0x1E800) {
            return false
        }
        if (codePoint in 0xFEFF until 0x10800) {
            return false
        }
        if (codePoint in 0xFE00 until 0xFE70) {
            return false
        }
        return true
    }

    /**
     * Checks whether a character triggers right-to-left processing.
     */
    public fun isCharBidi(c: Char): Boolean = isCharBidi(c.code)

    /**
     * Checks whether a UTF-16 code unit triggers right-to-left processing.
     */
    public fun isUtf16CodeUnitBidi(u: Int): Boolean {
        if (u < 0x0590) {
            return false
        }
        if (u in 0x0900 until 0xD802) {
            if (u in 0x200F..0x2067) {
                return u == 0x200F || u == 0x202B || u == 0x202E || u == 0x2067
            }
            return false
        }
        if (u in 0xD83C until 0xFB1D) {
            return false
        }
        if (u in 0xD804 until 0xD83A) {
            return false
        }
        if (u > 0xFEFE) {
            return false
        }
        if (u in 0xFE00 until 0xFE70) {
            return false
        }
        return true
    }

    /**
     * Checks whether a UTF-16 code unit triggers right-to-left processing.
     */
    public fun isUtf16CodeUnitBidi(u: Char): Boolean = isUtf16CodeUnitBidi(u.code)

    /**
     * Checks whether a UTF-16 buffer contains code units that trigger right-to-left processing.
     */
    public fun isUtf16Bidi(buffer: CharArray): Boolean =
        buffer.any { isUtf16CodeUnitBidi(it) }

    /**
     * Checks whether a string contains characters that trigger right-to-left processing.
     */
    public fun isStrBidi(buffer: String): Boolean {
        var i = 0
        while (i < buffer.length) {
            val c = buffer[i]
            if (c.isHighSurrogate() && i + 1 < buffer.length && buffer[i + 1].isLowSurrogate()) {
                val cp = 0x10000 + ((c.code - 0xD800) shl 10) + (buffer[i + 1].code - 0xDC00)
                if (isCharBidi(cp)) return true
                i += 2
            } else {
                if (isCharBidi(c)) return true
                i++
            }
        }
        return false
    }

    /**
     * Checks whether a potentially-invalid UTF-8 buffer contains code points
     * that trigger right-to-left processing.
     */
    public fun isUtf8Bidi(buffer: ByteArray): Boolean {
        var i = 0
        while (i < buffer.size) {
            val b = buffer[i].toInt() and 0xFF
            if (b < 0x80) {
                i++
                continue
            }
            if (b in 0xC2..0xDF) {
                if (i + 1 >= buffer.size) return true
                val b2 = buffer[i + 1].toInt() and 0xFF
                if (b2 !in 0x80..0xBF) return true
                val cp = ((b and 0x1F) shl 6) or (b2 and 0x3F)
                if (isCharBidi(cp)) return true
                i += 2
                continue
            }
            if (b in 0xE0..0xEF) {
                if (i + 2 >= buffer.size) return true
                val b2 = buffer[i + 1].toInt() and 0xFF
                val b3 = buffer[i + 2].toInt() and 0xFF
                if (b2 !in 0x80..0xBF || b3 !in 0x80..0xBF) return true
                val cp = ((b and 0x0F) shl 12) or ((b2 and 0x3F) shl 6) or (b3 and 0x3F)
                if (isCharBidi(cp)) return true
                i += 3
                continue
            }
            if (b in 0xF0..0xF4) {
                if (i + 3 >= buffer.size) return true
                val b2 = buffer[i + 1].toInt() and 0xFF
                val b3 = buffer[i + 2].toInt() and 0xFF
                val b4 = buffer[i + 3].toInt() and 0xFF
                if (b2 !in 0x80..0xBF || b3 !in 0x80..0xBF || b4 !in 0x80..0xBF) return true
                val cp =
                    ((b and 0x07) shl 18) or
                        ((b2 and 0x3F) shl 12) or
                        ((b3 and 0x3F) shl 6) or
                        (b4 and 0x3F)
                if (isCharBidi(cp)) return true
                i += 4
                continue
            }
            return true
        }
        return false
    }

    /**
     * Checks whether a potentially invalid UTF-8 buffer contains code points
     * that trigger right-to-left processing or is all-Latin1.
     */
    public fun checkUtf8ForLatin1AndBidi(buffer: ByteArray): Latin1Bidi {
        val latin1UpTo = utf8Latin1UpTo(buffer)
        if (latin1UpTo == buffer.size) {
            return Latin1Bidi.Latin1
        }
        return if (isUtf8Bidi(buffer.copyOfRange(latin1UpTo, buffer.size))) {
            Latin1Bidi.Bidi
        } else {
            Latin1Bidi.LeftToRight
        }
    }

    /**
     * Checks whether a valid UTF-8 buffer contains code points
     * that trigger right-to-left processing or is all-Latin1.
     */
    public fun checkStrForLatin1AndBidi(buffer: String): Latin1Bidi {
        val latin1UpTo = strLatin1UpTo(buffer)
        if (latin1UpTo == buffer.length) {
            return Latin1Bidi.Latin1
        }
        return if (isStrBidi(buffer.substring(latin1UpTo))) {
            Latin1Bidi.Bidi
        } else {
            Latin1Bidi.LeftToRight
        }
    }

    /**
     * Checks whether a potentially invalid UTF-16 buffer contains code points
     * that trigger right-to-left processing or is all-Latin1.
     */
    public fun checkUtf16ForLatin1AndBidi(buffer: CharArray): Latin1Bidi {
        var hasNonLatin1 = false
        for (c in buffer) {
            if (c.code > 0xFF) {
                hasNonLatin1 = true
                if (isUtf16CodeUnitBidi(c)) {
                    return Latin1Bidi.Bidi
                }
            }
        }
        return if (hasNonLatin1) Latin1Bidi.LeftToRight else Latin1Bidi.Latin1
    }

    /**
     * Converts potentially-invalid UTF-8 to valid UTF-16 with errors replaced
     * with the REPLACEMENT CHARACTER.
     */
    public fun convertUtf8ToUtf16(src: ByteArray, dst: CharArray): Int {
        require(dst.size > src.size) { "Destination buffer must be at least source length plus one" }
        val decoder = Utf8Decoder()
        var totalRead = 0
        var totalWritten = 0
        while (totalRead < src.size) {
            val srcSlice = src.copyOfRange(totalRead, src.size)
            val dstSlice = CharArray(dst.size - totalWritten)
            val (result, read, written) = decoder.decodeToUtf16Raw(srcSlice, dstSlice, last = true)
            dstSlice.copyInto(dst, totalWritten, 0, written)
            totalRead += read
            totalWritten += written
            when (result) {
                is DecoderResult.InputEmpty -> return totalWritten
                is DecoderResult.OutputFull -> return totalWritten
                is DecoderResult.Malformed -> {
                    if (totalWritten < dst.size) {
                        dst[totalWritten++] = '\uFFFD'
                    }
                }
            }
        }
        return totalWritten
    }

    /**
     * Converts valid UTF-8 to valid UTF-16.
     */
    public fun convertStrToUtf16(src: String, dst: CharArray): Int {
        require(dst.size >= src.length) { "Destination must not be shorter than the source." }
        for (i in src.indices) {
            dst[i] = src[i]
        }
        return src.length
    }

    /**
     * Converts potentially-invalid UTF-8 to valid UTF-16 signaling on error.
     */
    public fun convertUtf8ToUtf16WithoutReplacement(src: ByteArray, dst: CharArray): Int? {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        val (read, written) = Utf8.convertUtf8ToUtf16UpToInvalid(src, dst)
        return if (read == src.size) written else null
    }

    /**
     * Converts potentially-invalid UTF-16 to valid UTF-8 with errors replaced
     * with the REPLACEMENT CHARACTER with potentially insufficient output
     * space.
     */
    public fun convertUtf16ToUtf8Partial(src: CharArray, dst: ByteArray): Pair<Int, Int> {
        var read = 0
        var written = 0
        while (read < src.size) {
            val c = src[read]
            val code = c.code
            if (code <= 0x7F) {
                if (written >= dst.size) break
                dst[written++] = code.toByte()
                read++
            } else if (code <= 0x7FF) {
                if (written + 2 > dst.size) break
                dst[written++] = (0xC0 or (code shr 6)).toByte()
                dst[written++] = (0x80 or (code and 0x3F)).toByte()
                read++
            } else if (c.isHighSurrogate()) {
                if (read + 1 < src.size && src[read + 1].isLowSurrogate()) {
                    val next = src[read + 1]
                    val cp = 0x10000 + ((code - 0xD800) shl 10) + (next.code - 0xDC00)
                    if (written + 4 > dst.size) break
                    dst[written++] = (0xF0 or (cp shr 18)).toByte()
                    dst[written++] = (0x80 or ((cp shr 12) and 0x3F)).toByte()
                    dst[written++] = (0x80 or ((cp shr 6) and 0x3F)).toByte()
                    dst[written++] = (0x80 or (cp and 0x3F)).toByte()
                    read += 2
                } else {
                    if (written + 3 > dst.size) break
                    dst[written++] = 0xEF.toByte()
                    dst[written++] = 0xBF.toByte()
                    dst[written++] = 0xBD.toByte()
                    read++
                }
            } else if (c.isLowSurrogate()) {
                if (written + 3 > dst.size) break
                dst[written++] = 0xEF.toByte()
                dst[written++] = 0xBF.toByte()
                dst[written++] = 0xBD.toByte()
                read++
            } else {
                if (written + 3 > dst.size) break
                dst[written++] = (0xE0 or (code shr 12)).toByte()
                dst[written++] = (0x80 or ((code shr 6) and 0x3F)).toByte()
                dst[written++] = (0x80 or (code and 0x3F)).toByte()
                read++
            }
        }
        return Pair(read, written)
    }

    /**
     * Converts potentially-invalid UTF-16 to valid UTF-8 with errors replaced
     * with the REPLACEMENT CHARACTER.
     */
    public fun convertUtf16ToUtf8(src: CharArray, dst: ByteArray): Int {
        require(dst.size >= src.size * 3) { "Destination buffer must be at least source length * 3" }
        val (read, written) = convertUtf16ToUtf8Partial(src, dst)
        require(read == src.size)
        return written
    }

    /**
     * Converts potentially-invalid UTF-16 to valid UTF-8 with errors replaced
     * with the REPLACEMENT CHARACTER.
     */
    public fun convertUtf16ToStrPartial(src: CharArray, dst: ByteArray): Pair<Int, Int> =
        convertUtf16ToUtf8Partial(src, dst)

    /**
     * Converts potentially-invalid UTF-16 to valid UTF-8 with errors replaced
     * with the REPLACEMENT CHARACTER.
     */
    public fun convertUtf16ToStr(src: CharArray, dst: ByteArray): Int =
        convertUtf16ToUtf8(src, dst)

    /**
     * Converts bytes whose unsigned value is interpreted as Unicode code point
     * (i.e. U+0000 to U+00FF, inclusive) to UTF-16.
     */
    public fun convertLatin1ToUtf16(src: ByteArray, dst: CharArray) {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        Ascii.unpackLatin1(src, dst, src.size)
    }

    /**
     * Converts bytes whose unsigned value is interpreted as Unicode code point
     * (i.e. U+0000 to U+00FF, inclusive) to UTF-8 with potentially insufficient
     * output space.
     */
    public fun convertLatin1ToUtf8Partial(src: ByteArray, dst: ByteArray): Pair<Int, Int> {
        var read = 0
        var written = 0
        while (read < src.size) {
            val b = src[read].toInt() and 0xFF
            if (b < 0x80) {
                if (written >= dst.size) break
                dst[written++] = b.toByte()
                read++
            } else {
                if (written + 2 > dst.size) break
                dst[written++] = ((b shr 6) or 0xC0).toByte()
                dst[written++] = ((b and 0x3F) or 0x80).toByte()
                read++
            }
        }
        return Pair(read, written)
    }

    /**
     * Converts bytes whose unsigned value is interpreted as Unicode code point
     * (i.e. U+0000 to U+00FF, inclusive) to UTF-8.
     */
    public fun convertLatin1ToUtf8(src: ByteArray, dst: ByteArray): Int {
        require(dst.size >= src.size * 2) { "Destination must not be shorter than the source times two." }
        val (read, written) = convertLatin1ToUtf8Partial(src, dst)
        require(read == src.size)
        return written
    }

    /**
     * Converts bytes whose unsigned value is interpreted as Unicode code point
     * (i.e. U+0000 to U+00FF, inclusive) to UTF-8.
     */
    public fun convertLatin1ToStrPartial(src: ByteArray, dst: ByteArray): Pair<Int, Int> =
        convertLatin1ToUtf8Partial(src, dst)

    /**
     * Converts bytes whose unsigned value is interpreted as Unicode code point
     * (i.e. U+0000 to U+00FF, inclusive) to UTF-8.
     */
    public fun convertLatin1ToStr(src: ByteArray, dst: ByteArray): Int =
        convertLatin1ToUtf8(src, dst)

    /**
     * Converts UTF-8 to Latin-1 lossy.
     */
    public fun convertUtf8ToLatin1Lossy(src: ByteArray, dst: ByteArray): Int {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        var read = 0
        var written = 0
        while (read < src.size) {
            val b = src[read].toInt() and 0xFF
            if (b < 0x80) {
                dst[written++] = b.toByte()
                read++
            } else if (b in 0xC2..0xC3) {
                if (read + 1 >= src.size) {
                    dst[written++] = b.toByte()
                    read++
                } else {
                    val trail = src[read + 1].toInt() and 0xFF
                    dst[written++] = (((b and 0x1F) shl 6) or (trail and 0x3F)).toByte()
                    read += 2
                }
            } else {
                dst[written++] = b.toByte()
                read++
            }
        }
        return written
    }

    /**
     * Converts UTF-16 to Latin-1 lossy.
     */
    public fun convertUtf16ToLatin1Lossy(src: CharArray, dst: ByteArray) {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        Ascii.packLatin1(src, dst, src.size)
    }

    /**
     * Decodes Latin-1 bytes to String.
     */
    public fun decodeLatin1(bytes: ByteArray): String {
        val upTo = Ascii.asciiValidUpTo(bytes)
        if (upTo >= bytes.size) {
            return bytes.decodeToString()
        }
        val chars = CharArray(bytes.size)
        Ascii.unpackLatin1(bytes, chars, bytes.size)
        return chars.concatToString()
    }

    /**
     * Encodes String to Latin-1 bytes lossy.
     */
    public fun encodeLatin1Lossy(string: String): ByteArray {
        val bytes = ByteArray(string.length)
        for (i in string.indices) {
            val c = string[i].code
            bytes[i] = (if (c <= 0xFF) c else '?'.code).toByte()
        }
        return bytes
    }

    /**
     * Returns the index of the first unpaired surrogate or, if the input is
     * valid UTF-16 in its entirety, the length of the input.
     */
    public fun utf16ValidUpTo(buffer: CharArray): Int {
        var offset = 0
        while (offset < buffer.size) {
            val unit = buffer[offset]
            val unitMinusSurrogateStart = unit.code - 0xD800
            if (unitMinusSurrogateStart !in 0..(0xDFFF - 0xD800)) {
                offset++
                continue
            }
            if (unitMinusSurrogateStart in 0..(0xDBFF - 0xD800)) {
                if (offset + 1 < buffer.size) {
                    val second = buffer[offset + 1]
                    val secondMinusLowSurrogateStart = second.code - 0xDC00
                    if (secondMinusLowSurrogateStart in 0..(0xDFFF - 0xDC00)) {
                        offset += 2
                        continue
                    }
                }
            }
            return offset
        }
        return buffer.size
    }

    internal fun utf16ValidUpToAlu(buffer: CharArray): Int = utf16ValidUpTo(buffer)

    /**
     * Returns the index of first byte that starts an invalid byte
     * sequence or a non-Latin1 byte sequence, or the length of the
     * string if there are neither.
     */
    public fun utf8Latin1UpTo(buffer: ByteArray): Int {
        var offset = 0
        while (offset < buffer.size) {
            val b = buffer[offset].toInt() and 0xFF
            if (b < 0x80) {
                offset++
                continue
            }
            if (b in 0xC2..0xC3) {
                if (offset + 1 >= buffer.size) return offset
                val second = buffer[offset + 1].toInt() and 0xFF
                if ((second and 0xC0) != 0x80) return offset
                offset += 2
                continue
            }
            return offset
        }
        return buffer.size
    }

    /**
     * Returns the index of first byte that starts a non-Latin1 byte
     * sequence, or the length of the string if there are none.
     */
    public fun strLatin1UpTo(buffer: String): Int {
        for (i in buffer.indices) {
            if (buffer[i].code > 0xFF) return i
        }
        return buffer.length
    }

    /**
     * Replaces unpaired surrogates in the input with the REPLACEMENT CHARACTER.
     */
    public fun ensureUtf16Validity(buffer: CharArray) {
        var offset = 0
        while (offset < buffer.size) {
            val slice = buffer.copyOfRange(offset, buffer.size)
            val valid = utf16ValidUpTo(slice)
            offset += valid
            if (offset == buffer.size) return
            buffer[offset] = '\uFFFD'
            offset += 1
        }
    }

    /**
     * Copies ASCII from source to destination up to the first non-ASCII byte.
     */
    public fun copyAsciiToAscii(src: ByteArray, dst: ByteArray): Int {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        val invalid = Ascii.asciiToAscii(src, dst, src.size)
        return invalid?.second ?: src.size
    }

    /**
     * Copies ASCII from source to destination zero-extending it to UTF-16.
     */
    public fun copyAsciiToBasicLatin(src: ByteArray, dst: CharArray): Int {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        val invalid = Ascii.asciiToBasicLatin(src, dst, src.size)
        return invalid?.second ?: src.size
    }

    /**
     * Copies Basic Latin from source to destination narrowing it to ASCII.
     */
    public fun copyBasicLatinToAscii(src: CharArray, dst: ByteArray): Int {
        require(dst.size >= src.size) { "Destination must not be shorter than the source." }
        val invalid = Ascii.basicLatinToAscii(src, dst, src.size)
        return invalid?.second ?: src.size
    }
}
