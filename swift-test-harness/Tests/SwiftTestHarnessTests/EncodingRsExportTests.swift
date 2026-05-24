import XCTest
import EncodingRs

final class EncodingRsExportTests: XCTestCase {
    func testGb180302022OverrideTableMatchesCommonTest() throws {
        XCTAssertEqual(gb180302022OverrideCount(), 18)
        XCTAssertEqual(gb180302022OverridePuaAt(index: 0), 0xE78D)
        XCTAssertEqual(gb180302022OverrideByteAt(index: 0, byteIndex: 0), 0xA6)
        XCTAssertEqual(gb180302022OverrideByteAt(index: 0, byteIndex: 1), 0xD9)
        XCTAssertEqual(gb180302022OverridePuaAt(index: 17), 0xE864)
        XCTAssertEqual(gb180302022OverrideByteAt(index: 17, byteIndex: 0), 0xFE)
        XCTAssertEqual(gb180302022OverrideByteAt(index: 17, byteIndex: 1), 0xA0)
    }
}
