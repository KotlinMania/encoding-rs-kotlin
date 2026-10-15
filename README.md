# encoding-rs-kotlin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/encoding-rs-kotlin.svg?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.kotlinmania/encoding-rs-kotlin)
[![GitHub Release](https://img.shields.io/github/v/release/KotlinMania/encoding-rs-kotlin?color=blue&label=Release&logo=github)](https://github.com/KotlinMania/encoding-rs-kotlin/releases)
[![Build Status](https://img.shields.io/github/actions/workflow/status/KotlinMania/encoding-rs-kotlin/ci.yml?branch=main&label=CI&logo=githubactions&logoColor=white)](https://github.com/KotlinMania/encoding-rs-kotlin/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0%20%7C%20MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.x%20%7C%20Multiplatform-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Swift Export](https://img.shields.io/badge/Swift%20Export-Supported-FA7343.svg?logo=swift&logoColor=white)](https://www.swift.org/)

[![JVM](https://img.shields.io/badge/Platform-JVM-ED8B00.svg?logo=openjdk&logoColor=white)](https://kotlinlang.org/docs/jvm-get-started.html)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com/)
[![iOS](https://img.shields.io/badge/Platform-iOS-000000.svg?logo=apple&logoColor=white)](https://developer.apple.com/ios/)
[![macOS](https://img.shields.io/badge/Platform-macOS-000000.svg?logo=apple&logoColor=white)](https://www.apple.com/macos/)
[![Linux](https://img.shields.io/badge/Platform-Linux-FCC624.svg?logo=linux&logoColor=black)](https://www.kernel.org/)
[![Windows](https://img.shields.io/badge/Platform-Windows-0078D6.svg?logo=windows&logoColor=white)](https://www.microsoft.com/windows)
[![JavaScript](https://img.shields.io/badge/Platform-JavaScript-F7DF1E.svg?logo=javascript&logoColor=black)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
[![WebAssembly](https://img.shields.io/badge/Platform-Wasm--JS-654FF0.svg?logo=webassembly&logoColor=white)](https://webassembly.org/)
[![WHATWG Encoding Standard](https://img.shields.io/badge/Standard-WHATWG%20Encoding-brightgreen.svg)](https://encoding.spec.whatwg.org/)
[![Upstream Rust Crate](https://img.shields.io/crates/v/encoding_rs.svg?label=Upstream%20encoding_rs&logo=rust&logoColor=white)](https://crates.io/crates/encoding_rs)
[![Upstream docs.rs](https://img.shields.io/badge/docs.rs-encoding__rs-blue.svg?logo=docs.rs&logoColor=white)](https://docs.rs/encoding_rs/)

**encoding-rs-kotlin** is an idiomatic, high-performance, 100% pure Kotlin Multiplatform implementation of the [WHATWG Encoding Standard](https://encoding.spec.whatwg.org/), ported line-by-line from Henri Sivonen's upstream Rust crate [`encoding_rs`](https://github.com/hsivonen/encoding_rs).

`encoding_rs` is the battle-tested character encoding library developed for Mozilla Gecko and used in Firefox since version 56. This Kotlin port faithfully reproduces its algorithms, lookup tables, streaming state machines, and in-memory text manipulation utilities across all modern Kotlin targets without native C dependencies or JNI overhead.

---

## Table of Contents

- [Installation](#installation)
- [Supported Platforms](#supported-platforms)
- [Key Features](#key-features)
- [The WHATWG Encoding Standard in Kotlin](#the-whatwg-encoding-standard-in-kotlin)
- [Kotlin API Guide & Translations](#kotlin-api-guide--translations)
  - [Core Types & Rust-to-Kotlin Mapping](#core-types--rust-to-kotlin-mapping)
  - [Non-Streaming Decoding](#non-streaming-decoding)
  - [Non-Streaming Encoding](#non-streaming-encoding)
  - [Streaming Decoding with `Decoder`](#streaming-decoding-with-decoder)
  - [Streaming Encoding with `Encoder`](#streaming-encoding-with-encoder)
  - [Label Lookup & BOM Sniffing](#label-lookup--bom-sniffing)
  - [The `Mem` Module — In-Memory Fast Text Operations](#the-mem-module--in-memory-fast-text-operations)
- [Supported Character Encodings](#supported-character-encodings)
- [Code Examples](#code-examples)
- [Performance & Design Notes](#performance--design-notes)
- [Building & Testing](#building--testing)
- [Acknowledgments & Attribution](#acknowledgments--attribution)
- [Licensing](#licensing)

---

## Installation

Add the dependency to your project:

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.kotlinmania:encoding-rs-kotlin:0.1.2")
}
```

### Gradle Version Catalog (`libs.versions.toml`)

```toml
[versions]
encoding-rs = "0.1.2"

[libraries]
encoding-rs = { module = "io.github.kotlinmania:encoding-rs-kotlin", version.ref = "encoding-rs" }
```

---

## Supported Platforms

`encoding-rs-kotlin` compiles and runs natively across all Tier-1 and Tier-2 Kotlin Multiplatform targets:

| Family | Targets | Notes |
|---|---|---|
| **JVM** | Java 11+ | Full JVM runtime support |
| **Android** | Android 7.0+ (API 24+) | Android library AAR |
| **Apple** | macOS (`macosArm64`), iOS (`iosArm64`, `iosSimulatorArm64`), tvOS (`tvosArm64`, `tvosSimulatorArm64`), watchOS (`watchosDeviceArm64`, `watchosSimulatorArm64`) | Direct Native binaries, XCFrameworks, Swift Export support |
| **Linux** | `linuxX64`, `linuxArm64` | Linux native binaries |
| **Windows** | `mingwX64` | Windows native binaries |
| **Web** | JavaScript (Node.js & Browsers), Wasm-JS (Node.js & Browsers) | Pure Kotlin JS / WebAssembly |

---

## Key Features

- **Full WHATWG Specification Compliance**: Implements all 39 standard character encodings defined in the WHATWG Encoding Standard, including GB18030-2022 updates.
- **Bi-Directional UTF-8 & UTF-16 Conversion**: Decodes from legacy encodings into Kotlin `String` (UTF-8/UTF-16) or raw UTF-16 buffers (`CharArray`), and encodes to legacy bytes (`ByteArray`).
- **Streaming & Non-Streaming Processing**: High-level convenience functions for one-shot conversions alongside low-level stateful `Decoder` and `Encoder` objects for chunked or network stream processing.
- **Zero-Copy & Fast Paths**: Efficient ASCII and UTF-8 validation fast paths, specialized CJK lookup tables, and minimal allocations.
- **BOM Sniffing & Handling**: Built-in sniffing for UTF-8, UTF-16LE, and UTF-16BE Byte Order Marks with optional stripping or stripping suppression.
- **In-Memory Text Diagnostics (`Mem`)**: Fast checks for ASCII, Latin-1, and Bidirectional (RTL) text, in-place conversions, and UTF-16 validity enforcement.
- **No Native Dependencies**: Pure Kotlin implementation that builds with standard Gradle tooling and runs everywhere Kotlin does.

---

## The WHATWG Encoding Standard in Kotlin

The WHATWG Encoding Standard defines the exact set of legacy character encodings required for compatibility with the World Wide Web.

Unlike generic charset libraries that attempt to expose hundreds of platform-specific or historical code pages with loose round-tripping rules, this library follows the strict, deterministic security and error-handling requirements mandated by the web platform:

1. **Security-First Error Handling**: Malformed byte sequences are replaced with the Unicode Replacement Character (`\uFFFD` / ``) using WHATWG's maximal subpart replacement algorithm.
2. **Deterministic Label Matching**: Case-insensitive and whitespace-trimmed lookup matching standard web labels (`"windows-1252"`, `"latin1"`, `"shift_jis"`, `"gbk"`, etc.).
3. **Encoders Can Replace or Report**: Encoders can either replace unmappable code points with HTML numeric character references (NCRs) / replacement characters, or stop and report the exact unmappable Unicode scalar value.

---

## Kotlin API Guide & Translations

All types reside in the package:
```kotlin
package io.github.kotlinmania.encodingrs
```

### Core Types & Rust-to-Kotlin Mapping

| Upstream Rust (`encoding_rs`) | Kotlin Multiplatform (`encoding-rs-kotlin`) | Description |
|---|---|---|
| `&'static Encoding` | `Encoding` | Type-safe representation of a character encoding |
| `Encoding::UTF_8`, `WINDOWS_1252`, etc. | `Encoding.UTF_8`, `Encoding.WINDOWS_1252`, etc. | Encoding constant definitions |
| `&[u8]` / `Vec<u8>` | `ByteArray` | Raw byte sequence |
| `&str` / `String` | `String` | Valid UTF-8 / UTF-16 string |
| `&[u16]` / `Vec<u16>` | `CharArray` / `UShortArray` | UTF-16 code unit buffer |
| `(Cow<str>, &'static Encoding, bool)` | `Triple<String, Encoding, Boolean>` | Result of non-streaming `decode`: string, actual encoding, error flag |
| `(Cow<[u8]>, &'static Encoding, bool)` | `Triple<ByteArray, Encoding, Boolean>` | Result of non-streaming `encode`: bytes, actual encoding, error flag |
| `Decoder` | `Decoder` | Stateful streaming decoder |
| `Encoder` | `Encoder` | Stateful streaming encoder |
| `CoderResult::InputEmpty` | `CoderResult.InputEmpty` | All input bytes have been consumed |
| `CoderResult::OutputFull` | `CoderResult.OutputFull` | Destination buffer ran out of capacity |
| `DecoderResult::Malformed(u8, u8)` | `DecoderResult.Malformed(length, consumedAfter)` | Encountered malformed sequence without replacement |
| `EncoderResult::Unmappable(char)` | `EncoderResult.Unmappable(codePoint)` | Encountered unmappable character without replacement |
| `encoding_rs::mem` | `Mem` (`object`) | In-memory text utilities and fast validation |

---

### Non-Streaming Decoding

The non-streaming API processes an entire `ByteArray` at once:

```kotlin
val encoding = Encoding.forLabel("windows-1252") ?: Encoding.UTF_8

// 1. Decode with automatic BOM sniffing:
// Returns Triple(decodedString, actualEncoding, hadErrors)
val (text, actualEncoding, hadErrors) = encoding.decode(bytes)

// 2. Decode with BOM removal for UTF-8 / UTF-16:
val (textWithoutBom, hadErrors) = encoding.decodeWithBomRemoval(bytes)

// 3. Decode without BOM handling:
val (rawText, hadErrors) = encoding.decodeWithoutBomHandling(bytes)

// 4. Strict decode without replacement (returns null on malformed sequences):
val strictText: String? = encoding.decodeWithoutBomHandlingAndWithoutReplacement(bytes)
```

---

### Non-Streaming Encoding

The non-streaming API encodes a Kotlin `String` or `CharArray` into a `ByteArray`:

```kotlin
val encoding = Encoding.SHIFT_JIS

// Encodes the string into Shift_JIS bytes.
// If an unmappable character is encountered, it is replaced with HTML NCRs or '?'
// Returns Triple(outputBytes, actualEncoding, hadErrors)
val (bytes, actualEncoding, hadErrors) = encoding.encode("こんにちは")

// Alternatively encode from a UTF-16 CharArray:
val (bytesFromChars, _, _) = encoding.encodeFromUtf16(charArrayOf('h', 'e', 'l', 'l', 'o'))
```

---

### Streaming Decoding with `Decoder`

For streaming IO (files, sockets, HTTP response bodies), create a stateful `Decoder`:

```kotlin
// Create a decoder with automatic BOM sniffing:
val decoder = Encoding.UTF_8.newDecoder()

// Or with BOM removal:
val bomStrippingDecoder = Encoding.UTF_8.newDecoderWithBomRemoval()

// Or without any BOM handling:
val rawDecoder = Encoding.UTF_8.newDecoderWithoutBomHandling()
```

#### Chunked Decoding to UTF-8 (`String`):

```kotlin
val decoder = Encoding.WINDOWS_1252.newDecoder()
val chunk = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F) // "Hello"
val outputBuilder = StringBuilder()

val result: DecodeResult = decoder.decodeToString(chunk, outputBuilder, last = false)
// result.result == CoderResult.InputEmpty
// result.read == 5
// result.written == 5
// result.hadErrors == false
```

#### Chunked Decoding to Raw Buffers:

```kotlin
val dstChars = CharArray(1024)
val (coderResult, bytesRead, charsWritten, hadErrors) = 
    decoder.decodeToUtf16(chunk, dstChars, last = false)
```

#### Strict Streaming Decoding (Reporting Errors):

```kotlin
val dstChars = CharArray(1024)
val (decoderResult, bytesRead, charsWritten) = 
    decoder.decodeToUtf16WithoutReplacement(chunk, dstChars, last = false)

when (decoderResult) {
    is DecoderResult.InputEmpty -> { /* Feed next chunk */ }
    is DecoderResult.OutputFull -> { /* Flush output buffer and resume */ }
    is DecoderResult.Malformed -> {
        println("Malformed sequence of length ${decoderResult.length}")
    }
}
```

---

### Streaming Encoding with `Encoder`

For streaming output to legacy encodings:

```kotlin
val encoder = Encoding.GBK.newEncoder()
val dstBytes = ByteArray(1024)

// Encode a string chunk with replacement:
val result: EncodeResult = encoder.encodeFromUtf8("中文文本", dstBytes, last = false)

// Or encode strictly without replacement:
val (encoderResult, charsRead, bytesWritten) = 
    encoder.encodeFromUtf8WithoutReplacement("中文文本", dstBytes, last = false)

when (encoderResult) {
    is EncoderResult.InputEmpty -> { /* Feed next chunk */ }
    is EncoderResult.OutputFull -> { /* Flush output buffer and resume */ }
    is EncoderResult.Unmappable -> {
        println("Unmappable code point: U+${encoderResult.codePoint.toString(16).uppercase()}")
    }
}
```

---

### Label Lookup & BOM Sniffing

#### Label Resolution:
Matches standard labels per WHATWG specification:

```kotlin
val enc1 = Encoding.forLabel("utf-8")           // Encoding.UTF_8
val enc2 = Encoding.forLabel("  CSISO2022JP  ") // Encoding.ISO_2022_JP
val enc3 = Encoding.forLabel("x-sjis")          // Encoding.SHIFT_JIS
val enc4 = Encoding.forLabel("latin1")          // Encoding.WINDOWS_1252
```

#### BOM Sniffing:
Inspects the start of a byte buffer for UTF-8, UTF-16LE, or UTF-16BE BOMs:

```kotlin
val buffer = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 0x61, 0x62)
val match = Encoding.forBom(buffer)

if (match != null) {
    val (encoding, bomLength) = match
    println("Detected BOM: ${encoding.name}, length: $bomLength bytes")
    // encoding == Encoding.UTF_8, bomLength == 3
}
```

#### Fast Validation Utilities:
```kotlin
val asciiLength = Encoding.asciiValidUpTo(byteArray)
val utf8Length = Encoding.utf8ValidUpTo(byteArray)
```

---

### The `Mem` Module — In-Memory Fast Text Operations

The `Mem` singleton object (`io.github.kotlinmania.encodingrs.Mem`) provides high-speed in-memory operations on text buffers:

#### 1. Character Set & Bidirectional (Bidi) Checks
- `Mem.isAscii(buffer: ByteArray): Boolean`: Checks if bytes are all ASCII (0x00..0x7F).
- `Mem.isBasicLatin(buffer: CharArray): Boolean`: Checks if UTF-16 code units are all Basic Latin.
- `Mem.isUtf8Latin1(buffer: ByteArray): Boolean`: Checks if UTF-8 bytes represent code points below U+0100.
- `Mem.isStrLatin1(buffer: String): Boolean`: Checks if string contains only code points below U+0100.
- `Mem.isCharBidi(codePoint: Int): Boolean`: Checks if a Unicode code point can trigger RTL (Right-to-Left) behavior.
- `Mem.isStrBidi(buffer: String): Boolean`: Checks if a string can trigger RTL behavior (allows skipping Unicode Bidi Algorithm).
- `Mem.isUtf8Bidi(buffer: ByteArray): Boolean`: Checks if a UTF-8 buffer can trigger RTL behavior.
- `Mem.checkStrForLatin1AndBidi(buffer: String): Latin1Bidi`: Combined check for Latin-1 and Bidi status.

#### 2. Fast Conversions
- `Mem.convertUtf8ToUtf16(src: ByteArray, dst: CharArray): Int`: Converts UTF-8 to UTF-16, replacing invalid sequences.
- `Mem.convertStrToUtf16(src: String, dst: CharArray): Int`: Copies string code units to a UTF-16 buffer.
- `Mem.convertUtf16ToUtf8(src: CharArray, dst: ByteArray): Int`: Converts UTF-16 to UTF-8.
- `Mem.convertLatin1ToUtf16(src: ByteArray, dst: CharArray)`: Widens Latin-1 bytes to UTF-16.
- `Mem.convertLatin1ToUtf8(src: ByteArray, dst: ByteArray): Int`: Converts Latin-1 bytes to valid UTF-8.
- `Mem.decodeLatin1(bytes: ByteArray): String`: Directly decodes Latin-1 bytes into a Kotlin String.
- `Mem.encodeLatin1Lossy(string: String): ByteArray`: Lossily encodes string to Latin-1 bytes.

#### 3. Validation & In-Place Sanitization
- `Mem.utf16ValidUpTo(buffer: CharArray): Int`: Finds the first unmatched surrogate code unit.
- `Mem.ensureUtf16Validity(buffer: CharArray)`: In-place replaces lone surrogates with `\uFFFD`.
- `Mem.copyAscii(src: ByteArray, dst: ByteArray): Int`: Copies ASCII bytes up to the first non-ASCII byte.

---

## Supported Character Encodings

All 39 encodings from the WHATWG Encoding Standard are available as constants on `Encoding`:

### Unicode & Special Encodings
- `Encoding.UTF_8` ("UTF-8")
- `Encoding.UTF_16LE` ("UTF-16LE")
- `Encoding.UTF_16BE` ("UTF-16BE")
- `Encoding.REPLACEMENT` ("replacement")

### Legacy Windows Code Pages
- `Encoding.WINDOWS_1250` ("windows-1250" - Central/Eastern European)
- `Encoding.WINDOWS_1251` ("windows-1251" - Cyrillic)
- `Encoding.WINDOWS_1252` ("windows-1252" - Western European / Latin-1 superset)
- `Encoding.WINDOWS_1253` ("windows-1253" - Greek)
- `Encoding.WINDOWS_1254` ("windows-1254" - Turkish)
- `Encoding.WINDOWS_1255` ("windows-1255" - Hebrew)
- `Encoding.WINDOWS_1256` ("windows-1256" - Arabic)
- `Encoding.WINDOWS_1257` ("windows-1257" - Baltic)
- `Encoding.WINDOWS_1258` ("windows-1258" - Vietnamese)
- `Encoding.WINDOWS_874` ("windows-874" - Thai)

### ISO 8859 Family
- `Encoding.ISO_8859_2`, `ISO_8859_3`, `ISO_8859_4`, `ISO_8859_5`, `ISO_8859_6`, `ISO_8859_7`, `ISO_8859_8`, `ISO_8859_8_I`, `ISO_8859_10`, `ISO_8859_13`, `ISO_8859_14`, `ISO_8859_15`, `ISO_8859_16`

### East Asian Encodings (CJK)
- **Japanese**: `Encoding.SHIFT_JIS`, `Encoding.EUC_JP`, `Encoding.ISO_2022_JP`
- **Simplified & Traditional Chinese**: `Encoding.GBK`, `Encoding.GB18030` (including GB18030-2022 support), `Encoding.BIG5`
- **Korean**: `Encoding.EUC_KR`

### Historical & Miscellaneous Encodings
- `Encoding.KOI8_R`, `Encoding.KOI8_U` (Cyrillic)
- `Encoding.IBM866` (DOS Cyrillic)
- `Encoding.MACINTOSH`, `Encoding.X_MAC_CYRILLIC` (Classic Mac OS)
- `Encoding.X_USER_DEFINED` (Byte-preserving 8-bit mapping into Unicode PUA)

---

## Code Examples

### 1. Robust Web Content Decoding

```kotlin
import io.github.kotlinmania.encodingrs.Encoding

fun decodeWebContent(payload: ByteArray, contentTypeCharset: String?): String {
    // 1. Resolve encoding from header or default to windows-1252
    val baseEncoding = contentTypeCharset?.let { Encoding.forLabel(it) } ?: Encoding.WINDOWS_1252

    // 2. decode() will automatically sniff for UTF-8 / UTF-16 BOMs,
    // overriding baseEncoding if a BOM is present!
    val (content, actualEncoding, hadErrors) = baseEncoding.decode(payload)

    if (hadErrors) {
        println("Warning: Encountered malformed sequences while decoding as ${actualEncoding.name}")
    }
    return content
}
```

### 2. Encoding to Legacy Format with Error Detection

```kotlin
import io.github.kotlinmania.encodingrs.Encoding

fun exportToShiftJis(text: String): ByteArray {
    val (bytes, encoding, hadErrors) = Encoding.SHIFT_JIS.encode(text)
    if (hadErrors) {
        println("Some characters could not be represented in Shift_JIS and were replaced.")
    }
    return bytes
}
```

### 3. Swift Export Interop

When targeting Apple platforms, Kotlin Multiplatform's Swift Export provides native Swift wrappers:

```swift
import IoGithubKotlinmaniaEncodingRsKotlin

let encoding = ExportedKotlinPackages.io.github.kotlinmania.encodingrs.Encoding.UTF_8
let isAscii = ExportedKotlinPackages.io.github.kotlinmania.encodingrs.Mem.isAscii(buffer: myData)
```

---

## Performance & Design Notes

- **Optimized Lookup Tables**: Tables for multibyte character encodings (EUC-JP, Shift_JIS, Big5, EUC-KR, GB18030) are compressed and indexed for fast binary and direct lookups.
- **Fast UTF-8 & ASCII Paths**: Processing paths inspect chunks of data to fast-forward through pure ASCII or valid UTF-8 sequences.
- **Memory Safety & Multiplatform Portability**: All operations are 100% memory-safe Kotlin with bounds checking and without raw memory pointer arithmetic or unverified casts.

---

## Building & Testing

This project uses standard Gradle multiplatform builds:

```bash
# Run tests across all local multiplatform test targets and Swift Export harness:
./gradlew test

# Run full multiplatform build gate across all targets:
./gradlew build
```

---

## Acknowledgments & Attribution

This library is a direct Kotlin Multiplatform transliteration of [`encoding_rs`](https://github.com/hsivonen/encoding_rs) written by **Henri Sivonen**.

We express our deepest gratitude and credit to:
- **Henri Sivonen ([@hsivonen](https://github.com/hsivonen))**: The author and primary architect of `encoding_rs`. His profound research into the WHATWG Encoding Standard, SIMD acceleration, table compression, and character encoding corner cases created the gold standard for text encoding engines.
- **Mozilla Corporation & The Mozilla Gecko Project**: For sponsoring and battle-testing `encoding_rs` in Firefox across hundreds of millions of users worldwide.
- **The WHATWG (Web Hypertext Application Technology Working Group)**: For standardizing web character encodings and providing authoritative test fixtures.
- **Upstream Contributors**: Everyone who contributed optimizations, documentation, and test cases to `encoding_rs`, `encoding_c`, and adjacent crates.

This Kotlin port aims to bring that same standard of correctness, performance, and Web compatibility to the Kotlin and multiplatform ecosystem.

---

## Licensing

`encoding-rs-kotlin` is licensed under the same terms as upstream `encoding_rs`:

- **Code**: Dual-licensed under either **Apache License, Version 2.0** ([LICENSE](LICENSE)) or **MIT License**.
- **WHATWG Data Tables**: Portions generated from WHATWG specifications are licensed under the **BSD 3-Clause License**.
- **Tests**: Upstream test fixtures and tests are dedicated to the public domain under **CC0 1.0 Universal**.

Original Rust implementation: Copyright (c) Henri Sivonen and Mozilla Corporation.  
Kotlin Multiplatform port: Copyright (c) 2026 Sydney Renee and The Solace Project.
