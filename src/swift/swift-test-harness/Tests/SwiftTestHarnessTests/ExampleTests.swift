import Testing
import EncodingRs
import KotlinStdlib

@Suite("Encoding API Swift Export Tests")
struct EncodingApiExportTests {
    @Test
    func utf8LabelsResolveAliasesAndAsciiWhitespace() throws {
        for label in ["utf-8", "UTF-8", "utf8", "unicode-1-1-utf-8", " \t\n\u{000C}UTF-8\r\n "] {
            let encoding = try #require(Encoding.Companion.shared.forLabel(label: label))
            #expect(encoding.name == "UTF-8")
            #expect(encoding.isAsciiCompatible())
            #expect(encoding.canEncodeEverything())
        }
    }

    @Test
    func legacyAsciiAndLatin1LabelsResolveToWindows1252() throws {
        for label in ["ascii", "us-ascii", "latin1", "iso-8859-1"] {
            let encoding = try #require(Encoding.Companion.shared.forLabel(label: label))
            #expect(encoding.name == "windows-1252")
            #expect(encoding.isSingleByte())
            #expect(!encoding.canEncodeEverything())
        }
    }

    @Test
    func unknownAndMalformedLabelsAreRejected() {
        for label in ["", " \t\r\n", "bogus", "utf-8 _", "utf 8", "utf-8\u{00A0}"] {
            #expect(Encoding.Companion.shared.forLabel(label: label) == nil)
        }
    }

    @Test
    func utf8DecodesNonAsciiWithoutReplacement() {
        let input = kotlin.ByteArray(size: 5)
        input._set(index: 0, value: Int8(bitPattern: 0xE2))
        input._set(index: 1, value: Int8(bitPattern: 0x82))
        input._set(index: 2, value: Int8(bitPattern: 0xAC))
        input._set(index: 3, value: Int8(bitPattern: 0xC3))
        input._set(index: 4, value: Int8(bitPattern: 0xA4))

        #expect(UTF_8.decodeWithoutBomHandlingAndWithoutReplacement(bytes: input) == "\u{20AC}\u{00E4}")
    }

    @Test
    func utf8PreservesBomWithoutBomHandling() {
        let input = kotlin.ByteArray(size: 8)
        input._set(index: 0, value: Int8(bitPattern: 0xEF))
        input._set(index: 1, value: Int8(bitPattern: 0xBB))
        input._set(index: 2, value: Int8(bitPattern: 0xBF))
        input._set(index: 3, value: Int8(bitPattern: 0xE2))
        input._set(index: 4, value: Int8(bitPattern: 0x82))
        input._set(index: 5, value: Int8(bitPattern: 0xAC))
        input._set(index: 6, value: Int8(bitPattern: 0xC3))
        input._set(index: 7, value: Int8(bitPattern: 0xA4))

        #expect(UTF_8.decodeWithoutBomHandlingAndWithoutReplacement(bytes: input) == "\u{FEFF}\u{20AC}\u{00E4}")
    }

    @Test
    func utf8RejectsMalformedInputWithoutReplacement() {
        let input = kotlin.ByteArray(size: 9)
        input._set(index: 0, value: Int8(bitPattern: 0xEF))
        input._set(index: 1, value: Int8(bitPattern: 0xBB))
        input._set(index: 2, value: Int8(bitPattern: 0xBF))
        input._set(index: 3, value: Int8(bitPattern: 0xE2))
        input._set(index: 4, value: Int8(bitPattern: 0x82))
        input._set(index: 5, value: Int8(bitPattern: 0xAC))
        input._set(index: 6, value: Int8(bitPattern: 0x80))
        input._set(index: 7, value: Int8(bitPattern: 0xC3))
        input._set(index: 8, value: Int8(bitPattern: 0xA4))

        #expect(UTF_8.decodeWithoutBomHandlingAndWithoutReplacement(bytes: input) == nil)
    }
}
