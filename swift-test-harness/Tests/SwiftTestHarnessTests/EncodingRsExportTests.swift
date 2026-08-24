import Testing
import EncodingRs

@Suite
struct EncodingRsExportTests {
    @Test
    func gb180302022OverrideTableMatchesCommonTest() {
        #expect(gb180302022OverrideCount() == 18)
        #expect(gb180302022OverridePuaAt(index: 0) == 0xE78D)
        #expect(gb180302022OverrideByteAt(index: 0, byteIndex: 0) == 0xA6)
        #expect(gb180302022OverrideByteAt(index: 0, byteIndex: 1) == 0xD9)
        #expect(gb180302022OverridePuaAt(index: 17) == 0xE864)
        #expect(gb180302022OverrideByteAt(index: 17, byteIndex: 0) == 0xFE)
        #expect(gb180302022OverrideByteAt(index: 17, byteIndex: 1) == 0xA0)
    }
}
