// port-lint: source encoding_rs/src/gb18030_2022.rs
package io.github.kotlinmania.encodingrs

// Copyright WHATWG (Apple, Google, Mozilla, Microsoft).
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. Neither the name of the copyright holder nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT INCLUDING NEGLIGENCE OR OTHERWISE ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

/**
 * The private-use-area code points special-cased in the GB18030 encoder.
 */
internal val GB18030_2022_OVERRIDE_PUA: UShortArray =
    ushortArrayOf(
        0xE78Du,
        0xE78Eu,
        0xE78Fu,
        0xE790u,
        0xE791u,
        0xE792u,
        0xE793u,
        0xE794u,
        0xE795u,
        0xE796u,
        0xE81Eu,
        0xE826u,
        0xE82Bu,
        0xE82Cu,
        0xE832u,
        0xE843u,
        0xE854u,
        0xE864u,
    )

/**
 * The bytes corresponding to the private-use-area code points special-cased in
 * the GB18030 encoder.
 */
internal val GB18030_2022_OVERRIDE_BYTES: Array<UByteArray> =
    arrayOf(
        ubyteArrayOf(0xA6u, 0xD9u),
        ubyteArrayOf(0xA6u, 0xDAu),
        ubyteArrayOf(0xA6u, 0xDBu),
        ubyteArrayOf(0xA6u, 0xDCu),
        ubyteArrayOf(0xA6u, 0xDDu),
        ubyteArrayOf(0xA6u, 0xDEu),
        ubyteArrayOf(0xA6u, 0xDFu),
        ubyteArrayOf(0xA6u, 0xECu),
        ubyteArrayOf(0xA6u, 0xEDu),
        ubyteArrayOf(0xA6u, 0xF3u),
        ubyteArrayOf(0xFEu, 0x59u),
        ubyteArrayOf(0xFEu, 0x61u),
        ubyteArrayOf(0xFEu, 0x66u),
        ubyteArrayOf(0xFEu, 0x67u),
        ubyteArrayOf(0xFEu, 0x6Du),
        ubyteArrayOf(0xFEu, 0x7Eu),
        ubyteArrayOf(0xFEu, 0x90u),
        ubyteArrayOf(0xFEu, 0xA0u),
    )
