// port-lint: tests encoding_rs/src/testing.rs
package io.github.kotlinmania.encodingrs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestingTest {
    private fun decode(encoding: Encoding, bytes: ByteArray, expect: String) {
        val (decoded, _, _) = encoding.decode(bytes)
        assertEquals(expect, decoded)
    }

    private fun encode(encoding: Encoding, str: String, expect: ByteArray) {
        val (encoded, _, _) = encoding.encode(str)
        assertContentEquals(expect, encoded)
    }

    private fun decodeToString(encoding: Encoding, bytes: ByteArray, expect: String) {
        val (cow, _, _) = encoding.decode(bytes)
        assertEquals(expect, cow)
    }

    private fun encodeToVec(encoding: Encoding, string: String, expect: ByteArray) {
        val (cow, _, _) = encoding.encode(string)
        assertContentEquals(expect, cow)
    }

    @Test
    fun testTestingDecodeAndEncodeHelpers() {
        decode(Encoding.UTF_8, "hello".encodeToByteArray(), "hello")
        encode(Encoding.UTF_8, "world", "world".encodeToByteArray())
        decodeToString(Encoding.WINDOWS_1252, "test".encodeToByteArray(), "test")
        encodeToVec(Encoding.WINDOWS_1252, "test", "test".encodeToByteArray())
    }

    @Test
    fun testDecoderEncoderWithPadding() {
        val bytes = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F) // "Hello"
        val expect = "Hello"
        val (str, _) = Encoding.UTF_8.decodeWithoutBomHandling(bytes)
        assertEquals(expect, str)

        val (encoded, _, _) = Encoding.UTF_8.encode(expect)
        assertContentEquals(bytes, encoded)
    }
}
