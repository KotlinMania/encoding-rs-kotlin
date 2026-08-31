// port-lint: source encoding_rs/src/testing.rs
package io.github.kotlinmania.encodingrs

internal object Testing {
    private fun assertEq(expected: Any?, actual: Any?, message: String? = null) {
        check(expected == actual) { message ?: "Expected <$expected>, got <$actual>" }
    }

    fun decode(encoding: Encoding, bytes: ByteArray, expect: String) {
        val range = 0 until 32
        for (i in range) {
            val vec = ByteArray(i + bytes.size)
            val sb = StringBuilder()
            for (j in 0 until i) {
                val c = (0x40 + j).toByte()
                vec[j] = c
                sb.append(c.toInt().toChar())
            }
            bytes.copyInto(vec, destinationOffset = i)
            sb.append(expect)
            decodeWithoutPaddingImpl(encoding, vec, sb.toString(), i)
        }
    }

    fun decodeWithoutPadding(encoding: Encoding, bytes: ByteArray, expect: String) {
        decodeWithoutPaddingImpl(encoding, bytes, expect, 0)
    }

    fun decodeWithoutPaddingImpl(
        encoding: Encoding,
        bytes: ByteArray,
        expect: String,
        padding: Int,
    ) {
        decodeToUtf16Impl(encoding, bytes, expect.toCharArray(), padding)
        decodeToUtf8Impl(encoding, bytes, expect, padding)
        decodeToString(encoding, bytes, expect)
    }

    fun encode(encoding: Encoding, string: String, expect: ByteArray) {
        val range = 0 until 32
        for (i in range) {
            val vec = ByteArray(i + expect.size)
            for (j in 0 until i) {
                vec[j] = (0x40 + j).toByte()
            }
            expect.copyInto(vec, destinationOffset = i)
            val s =
                buildString {
                    for (j in 0 until i) {
                        append((0x40 + j).toChar())
                    }
                    append(string)
                }
            encodeWithoutPadding(encoding, s, vec)
        }
    }

    fun encodeWithoutPadding(encoding: Encoding, string: String, expect: ByteArray) {
        encodeFromUtf8(encoding, string, expect)
        encodeFromUtf16(encoding, utf16FromUtf8(string), expect)
        encodeToVec(encoding, string, expect)
    }

    fun decodeToUtf16(encoding: Encoding, bytes: ByteArray, expect: CharArray) {
        decodeToUtf16Impl(encoding, bytes, expect, 0)
    }

    fun decodeToUtf16Impl(
        encoding: Encoding,
        bytes: ByteArray,
        expect: CharArray,
        padding: Int,
    ) {
        for (i in padding..bytes.size) {
            val head = bytes.copyOfRange(0, i)
            val tail = bytes.copyOfRange(i, bytes.size)
            decodeToUtf16WithBoundary(encoding, head, tail, expect)
        }
    }

    fun decodeToUtf16WithBoundary(
        encoding: Encoding,
        head: ByteArray,
        tail: ByteArray,
        expect: CharArray,
    ) {
        val decoder = encoding.newDecoder()
        val dest = CharArray(decoder.maxUtf16BufferLength(head.size + tail.size) ?: (head.size + tail.size + 4))
        var totalRead = 0
        var totalWritten = 0

        val (_, read1, written1) = decoder.decodeToUtf16Raw(head, dest, last = false)
        totalRead += read1
        totalWritten += written1

        val tailDest = CharArray(dest.size - totalWritten)
        val (_, read2, written2) = decoder.decodeToUtf16Raw(tail, tailDest, last = true)
        tailDest.copyInto(dest, destinationOffset = totalWritten, startIndex = 0, endIndex = written2)
        totalRead += read2
        totalWritten += written2

        assertEq(head.size + tail.size, totalRead)
        assertEq(expect.size, totalWritten)
        assertEq(expect.concatToString(), dest.concatToString(0, totalWritten))
    }

    fun decodeToUtf8(encoding: Encoding, bytes: ByteArray, expect: String) {
        decodeToUtf8Impl(encoding, bytes, expect, 0)
    }

    fun decodeToUtf8Impl(
        encoding: Encoding,
        bytes: ByteArray,
        expect: String,
        padding: Int,
    ) {
        for (i in padding..bytes.size) {
            val head = bytes.copyOfRange(0, i)
            val tail = bytes.copyOfRange(i, bytes.size)
            decodeToUtf8WithBoundary(encoding, head, tail, expect)
        }
    }

    fun decodeToUtf8WithBoundary(
        encoding: Encoding,
        head: ByteArray,
        tail: ByteArray,
        expect: String,
    ) {
        val decoder = encoding.newDecoder()
        val dest = ByteArray(decoder.maxUtf8BufferLength(head.size + tail.size) ?: ((head.size + tail.size) * 3 + 4))
        var totalRead = 0
        var totalWritten = 0

        val (_, read1, written1) = decoder.decodeToUtf8WithoutReplacement(head, dest, last = false)
        totalRead += read1
        totalWritten += written1

        val tailDest = ByteArray(dest.size - totalWritten)
        val (_, read2, written2) = decoder.decodeToUtf8WithoutReplacement(tail, tailDest, last = true)
        tailDest.copyInto(dest, destinationOffset = totalWritten, startIndex = 0, endIndex = written2)
        totalRead += read2
        totalWritten += written2

        assertEq(head.size + tail.size, totalRead)
        val actualStr = dest.decodeToString(0, totalWritten)
        assertEq(expect, actualStr)
    }

    fun decodeToString(encoding: Encoding, bytes: ByteArray, expect: String) {
        val (str, _, _) = encoding.decode(bytes)
        assertEq(expect, str)
    }

    fun encodeFromUtf8(encoding: Encoding, string: String, expect: ByteArray) {
        val encoder = encoding.newEncoder()
        val dest = ByteArray(10 * (string.length + 1))
        val (_, read, written) = encoder.encodeFromUtf8Raw(string, dest, last = true)
        assertEq(string.length, read)
        assertEq(expect.size, written)
    }

    fun encodeFromUtf16(encoding: Encoding, string: CharArray, expect: ByteArray) {
        val encoder = encoding.newEncoder()
        val dest = ByteArray(10 * (string.size + 1))
        val (_, read, written) = encoder.encodeFromUtf16Raw(string, dest, last = true)
        assertEq(string.size, read)
        assertEq(expect.size, written)
    }

    fun encodeToVec(encoding: Encoding, string: String, expect: ByteArray) {
        val (bytes, _, _) = encoding.encode(string)
        assertEq(expect.size, bytes.size)
    }

    fun utf16FromUtf8(string: String): CharArray = string.toCharArray()
}
