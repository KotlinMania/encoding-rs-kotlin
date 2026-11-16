// port-lint: source simd_funcs.rs
package io.github.kotlinmania.encodingrs

/**
 * SIMD and word-at-a-time vector processing operations for encoding routines.
 */
object SimdFuncs {
    /**
     * Loads 16 bytes from an unaligned byte array.
     */
    fun load16Unaligned(src: ByteArray, offset: Int = 0): ByteArray {
        val result = ByteArray(16)
        src.copyInto(result, 0, offset, offset + 16)
        return result
    }

    /**
     * Loads 16 bytes from an aligned byte array.
     */
    fun load16Aligned(src: ByteArray, offset: Int = 0): ByteArray =
        load16Unaligned(src, offset)

    /**
     * Stores 16 bytes into an unaligned byte array.
     */
    fun store16Unaligned(dst: ByteArray, offset: Int, value: ByteArray) {
        value.copyInto(dst, offset, 0, 16)
    }

    /**
     * Stores 16 bytes into an aligned byte array.
     */
    fun store16Aligned(dst: ByteArray, offset: Int, value: ByteArray) {
        store16Unaligned(dst, offset, value)
    }

    /**
     * Loads 8 16-bit code units from an unaligned char array.
     */
    fun load8Unaligned(src: CharArray, offset: Int = 0): CharArray {
        val result = CharArray(8)
        src.copyInto(result, 0, offset, offset + 8)
        return result
    }

    /**
     * Loads 8 16-bit code units from an aligned char array.
     */
    fun load8Aligned(src: CharArray, offset: Int = 0): CharArray =
        load8Unaligned(src, offset)

    /**
     * Stores 8 16-bit code units into an unaligned char array.
     */
    fun store8Unaligned(dst: CharArray, offset: Int, value: CharArray) {
        value.copyInto(dst, offset, 0, 8)
    }

    /**
     * Stores 8 16-bit code units into an aligned char array.
     */
    fun store8Aligned(dst: CharArray, offset: Int, value: CharArray) {
        store8Unaligned(dst, offset, value)
    }

    /**
     * Swaps bytes in 16-bit lanes.
     */
    fun simdByteSwap(s: CharArray): CharArray {
        val result = CharArray(s.size)
        for (i in s.indices) {
            val code = s[i].code
            val swapped = ((code and 0xFF) shl 8) or ((code ushr 8) and 0xFF)
            result[i] = swapped.toChar()
        }
        return result
    }

    /**
     * Converts a 16-byte lane into 8 16-bit lanes.
     */
    fun toU16Lanes(s: ByteArray): CharArray {
        val result = CharArray(s.size / 2)
        for (i in result.indices) {
            val low = s[i * 2].toInt() and 0xFF
            val high = s[i * 2 + 1].toInt() and 0xFF
            result[i] = (low or (high shl 8)).toChar()
        }
        return result
    }

    /**
     * Returns true if any 16-bit lane contains a surrogate code point.
     */
    fun containsSurrogates(s: CharArray): Boolean {
        for (c in s) {
            val code = c.code
            if (code in 0xD800..0xDFFF) return true
        }
        return false
    }

    /**
     * Returns true if any 16-bit lane indicates bidirectional text.
     */
    fun isU16x8Bidi(s: CharArray): Boolean {
        var belowHebrew = true
        for (c in s) {
            if (c.code >= 0x0590) {
                belowHebrew = false
                break
            }
        }
        if (belowHebrew) return false

        for (c in s) {
            val code = c.code
            if (code in 0x0590..0x08FF ||
                code in 0xFB1D..0xFDFF ||
                code in 0xFE70..0xFEFE ||
                code in 0xD802..0xD803 ||
                code in 0xD83A..0xD83B ||
                code == 0x200F ||
                code == 0x202B ||
                code == 0x202E ||
                code == 0x2067
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Unpacks 16 bytes into two 8-character arrays.
     */
    fun simdUnpack(s: ByteArray): Pair<CharArray, CharArray> {
        val first = CharArray(8)
        val second = CharArray(8)
        for (i in 0 until 8) {
            first[i] = (s[i].toInt() and 0xFF).toChar()
            second[i] = (s[i + 8].toInt() and 0xFF).toChar()
        }
        return Pair(first, second)
    }

    /**
     * Packs two 8-character arrays into 16 bytes.
     */
    fun simdPack(a: CharArray, b: CharArray): ByteArray {
        val result = ByteArray(16)
        for (i in 0 until 8) {
            result[i] = (a[i].code and 0xFF).toByte()
            result[i + 8] = (b[i].code and 0xFF).toByte()
        }
        return result
    }

    /**
     * Checks if all bytes in a 16-byte slice are ASCII.
     */
    fun simdIsAscii(s: ByteArray): Boolean {
        for (b in s) {
            if ((b.toInt() and 0x80) != 0) return false
        }
        return true
    }

    /**
     * Checks if all characters in an 8-character slice are Basic Latin (ASCII).
     */
    fun simdIsBasicLatin(s: CharArray): Boolean {
        for (c in s) {
            if (c.code >= 0x80) return false
        }
        return true
    }

    /**
     * Checks if all characters in an 8-character slice are Latin-1.
     */
    fun simdIsLatin1(s: CharArray): Boolean {
        for (c in s) {
            if (c.code > 0xFF) return false
        }
        return true
    }

    /**
     * Checks if all bytes in a 16-byte slice are valid ISO-8859-1 strings.
     */
    fun simdIsStrLatin1(s: ByteArray): Boolean {
        for (b in s) {
            if ((b.toInt() and 0xFF) >= 0xC4) return false
        }
        return true
    }

    /**
     * Returns a bitmask of non-ASCII bytes in the vector.
     */
    fun maskAscii(s: ByteArray): Int {
        var mask = 0
        for (i in s.indices) {
            if ((s[i].toInt() and 0x80) != 0) {
                mask = mask or (1 shl i)
            }
        }
        return mask
    }
}
