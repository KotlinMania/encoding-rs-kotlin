# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/22 (100.0%)
- **Function parity:** 462/545 matched (target 628) — 84.8%
- **Class/type parity:** 61/69 matched (target 122) — 88.4%
- **Combined symbol parity:** 523/614 matched (target 750) — 85.2%
- **Average inline-code cosine:** 0.60 (function body across 18 matched files)
- **Average documentation cosine:** 0.10 (doc text across 18 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 11 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 382610.0
- **Functions:** 81/115 matched (target 95)
- **Missing functions:** `name`, `new_variant_decoder`, `utf8_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`
- **Types:** 8/11 matched (target 15)
- **Missing types:** `EncodingVisitor`, `Value`, `Demo`
- **Tests:** 46/46 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source encoding_rs/src/lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source encoding_rs/src/lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests encoding_rs/src/lib.rs`)
- **Lint issues:** 3

### 2. simd_funcs

- **Target:** `encodingrs.SimdFuncs [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 132210.0
- **Functions:** 9/22 matched (target 9)
- **Missing functions:** `load16_unaligned`, `load16_aligned`, `store16_unaligned`, `store16_aligned`, `load8_unaligned`, `load8_aligned`, `store8_unaligned`, `store8_aligned`, `simd_byte_swap`, `to_u16_lanes`, `contains_surrogates`, `is_u16x8_bidi`, `simd_unpack`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 9/9 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/simd_funcs.rs` vs expected `simd_funcs.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/simd_funcs.rs` vs expected `simd_funcs.rs`
- **Proposed provenance header:** `// port-lint: source simd_funcs.rs` (current: `// port-lint: source encoding_rs/src/simd_funcs.rs`)
- **Proposed provenance header:** `// port-lint: tests simd_funcs.rs` (current: `// port-lint: tests encoding_rs/src/simd_funcs.rs`)
- **Lint issues:** 2

### 3. data

- **Target:** `encodingrs.Data [PROVENANCE-FALLBACK]`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 114804.1
- **Functions:** 37/47 matched (target 40)
- **Missing functions:** `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_hanzi_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `mul_94`
- **Types:** 0/1 matched
- **Missing types:** `SingleByteData`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/data.rs` vs expected `data.rs`
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source encoding_rs/src/data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source encoding_rs/src/data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source encoding_rs/src/data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source encoding_rs/src/data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source encoding_rs/src/data.rs`)
- **Lint issues:** 5

### 4. gb18030

- **Target:** `encodingrs.Gb18030 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 102406.1
- **Functions:** 13/21 matched (target 15)
- **Missing functions:** `is_none`, `in_neutral_state`, `extra_from_state`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`, `gbk_encode_non_unified`, `encode_hanzi`
- **Types:** 1/3 matched (target 5)
- **Missing types:** `Gb18030Decoder`, `Gb18030Encoder`
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/gb18030.rs` vs expected `gb18030.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/gb18030.rs` vs expected `gb18030.rs`
- **Proposed provenance header:** `// port-lint: source gb18030.rs` (current: `// port-lint: source encoding_rs/src/gb18030.rs`)
- **Proposed provenance header:** `// port-lint: tests gb18030.rs` (current: `// port-lint: tests encoding_rs/src/gb18030.rs`)
- **Lint issues:** 2

### 5. handles

- **Target:** `encodingrs.Handles [PROVENANCE-FALLBACK]`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 87302.8
- **Functions:** 37/45 matched (target 130)
- **Missing functions:** `new`, `simd_at`, `len`, `copy_unaligned_basic_latin_to_ascii_alu`, `swap_if_opposite_endian`, `copy_unaligned_basic_latin_to_ascii`, `convert_unaligned_utf16_to_utf8`, `copy_utf16_from`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/handles.rs` vs expected `handles.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/handles.rs` vs expected `handles.rs`
- **Proposed provenance header:** `// port-lint: source handles.rs` (current: `// port-lint: source encoding_rs/src/handles.rs`)
- **Proposed provenance header:** `// port-lint: tests handles.rs` (current: `// port-lint: tests encoding_rs/src/handles.rs`)
- **Lint issues:** 2

### 6. big5

- **Target:** `encodingrs.Big5 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 71705.3
- **Functions:** 10/15 matched (target 12)
- **Missing functions:** `in_neutral_state`, `plus_one_if_lead`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `Big5Decoder`, `Big5Encoder`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/big5.rs` vs expected `big5.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/big5.rs` vs expected `big5.rs`
- **Proposed provenance header:** `// port-lint: source big5.rs` (current: `// port-lint: source encoding_rs/src/big5.rs`)
- **Proposed provenance header:** `// port-lint: tests big5.rs` (current: `// port-lint: tests encoding_rs/src/big5.rs`)
- **Lint issues:** 2

### 7. utf_8

- **Target:** `encodingrs.Utf8 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 43104.0
- **Functions:** 24/28 matched (target 29)
- **Missing functions:** `new_inner`, `extra_from_state`, `convert_utf16_to_utf8_partial_inner`, `convert_utf16_to_utf8_partial_tail`
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/utf_8.rs` vs expected `utf_8.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/utf_8.rs` vs expected `utf_8.rs`
- **Proposed provenance header:** `// port-lint: source utf_8.rs` (current: `// port-lint: source encoding_rs/src/utf_8.rs`)
- **Proposed provenance header:** `// port-lint: tests utf_8.rs` (current: `// port-lint: tests encoding_rs/src/utf_8.rs`)
- **Lint issues:** 2

### 8. euc_jp

- **Target:** `encodingrs.EucJp [PROVENANCE-FALLBACK]`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 12104.1
- **Functions:** 17/18 matched (target 20)
- **Missing functions:** `is_none`
- **Types:** 3/3 matched (target 9)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/euc_jp.rs` vs expected `euc_jp.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/euc_jp.rs` vs expected `euc_jp.rs`
- **Proposed provenance header:** `// port-lint: source euc_jp.rs` (current: `// port-lint: source encoding_rs/src/euc_jp.rs`)
- **Proposed provenance header:** `// port-lint: tests euc_jp.rs` (current: `// port-lint: tests encoding_rs/src/euc_jp.rs`)
- **Lint issues:** 2

### 9. mem

- **Target:** `encodingrs.Mem [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 8203.9
- **Functions:** 81/81 matched (target 83)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 35/35 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/mem.rs` vs expected `mem.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/mem.rs` vs expected `mem.rs`
- **Proposed provenance header:** `// port-lint: source mem.rs` (current: `// port-lint: source encoding_rs/src/mem.rs`)
- **Proposed provenance header:** `// port-lint: tests mem.rs` (current: `// port-lint: tests encoding_rs/src/mem.rs`)
- **Lint issues:** 2

### 10. iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp [PROVENANCE-FALLBACK]`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2603.3
- **Functions:** 22/22 matched (target 25)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/iso_2022_jp.rs` vs expected `iso_2022_jp.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/iso_2022_jp.rs` vs expected `iso_2022_jp.rs`
- **Proposed provenance header:** `// port-lint: source iso_2022_jp.rs` (current: `// port-lint: source encoding_rs/src/iso_2022_jp.rs`)
- **Proposed provenance header:** `// port-lint: tests iso_2022_jp.rs` (current: `// port-lint: tests encoding_rs/src/iso_2022_jp.rs`)
- **Lint issues:** 2

### 11. single_byte

- **Target:** `encodingrs.SingleByte [PROVENANCE-FALLBACK]`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 2304.1
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/single_byte.rs` vs expected `single_byte.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/single_byte.rs` vs expected `single_byte.rs`
- **Proposed provenance header:** `// port-lint: source single_byte.rs` (current: `// port-lint: source encoding_rs/src/single_byte.rs`)
- **Proposed provenance header:** `// port-lint: tests single_byte.rs` (current: `// port-lint: tests encoding_rs/src/single_byte.rs`)
- **Lint issues:** 2

### 12. utf_16

- **Target:** `encodingrs.Utf16 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/utf_16.rs` vs expected `utf_16.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/utf_16.rs` vs expected `utf_16.rs`
- **Proposed provenance header:** `// port-lint: source utf_16.rs` (current: `// port-lint: source encoding_rs/src/utf_16.rs`)
- **Proposed provenance header:** `// port-lint: tests utf_16.rs` (current: `// port-lint: tests encoding_rs/src/utf_16.rs`)
- **Lint issues:** 2

### 13. euc_kr

- **Target:** `encodingrs.EucKr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2003.3
- **Functions:** 18/18 matched (target 21)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/euc_kr.rs` vs expected `euc_kr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/euc_kr.rs` vs expected `euc_kr.rs`
- **Proposed provenance header:** `// port-lint: source euc_kr.rs` (current: `// port-lint: source encoding_rs/src/euc_kr.rs`)
- **Proposed provenance header:** `// port-lint: tests euc_kr.rs` (current: `// port-lint: tests encoding_rs/src/euc_kr.rs`)
- **Lint issues:** 2

### 14. shift_jis

- **Target:** `encodingrs.ShiftJis [PROVENANCE-FALLBACK]`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1802.9
- **Functions:** 16/16 matched (target 19)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/shift_jis.rs` vs expected `shift_jis.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/shift_jis.rs` vs expected `shift_jis.rs`
- **Proposed provenance header:** `// port-lint: source shift_jis.rs` (current: `// port-lint: source encoding_rs/src/shift_jis.rs`)
- **Proposed provenance header:** `// port-lint: tests shift_jis.rs` (current: `// port-lint: tests encoding_rs/src/shift_jis.rs`)
- **Lint issues:** 2

### 15. variant

- **Target:** `encodingrs.Variant [PROVENANCE-FALLBACK]`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 1702.8
- **Functions:** 14/14 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 16)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/variant.rs` vs expected `variant.rs`
- **Proposed provenance header:** `// port-lint: source variant.rs` (current: `// port-lint: source encoding_rs/src/variant.rs`)
- **Lint issues:** 1

### 16. testing

- **Target:** `encodingrs.Testing [PROVENANCE-FALLBACK]`
- **Similarity:** 0.82
- **Dependents:** 0
- **Priority Score:** 1601.8
- **Functions:** 16/16 matched (target 23)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/testing.rs` vs expected `testing.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/testing.rs` vs expected `testing.rs`
- **Proposed provenance header:** `// port-lint: source testing.rs` (current: `// port-lint: source encoding_rs/src/testing.rs`)
- **Proposed provenance header:** `// port-lint: tests testing.rs` (current: `// port-lint: tests encoding_rs/src/testing.rs`)
- **Lint issues:** 2

### 17. x_user_defined

- **Target:** `encodingrs.XUserDefined [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 1403.9
- **Functions:** 12/12 matched (target 15)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/x_user_defined.rs` vs expected `x_user_defined.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/x_user_defined.rs` vs expected `x_user_defined.rs`
- **Proposed provenance header:** `// port-lint: source x_user_defined.rs` (current: `// port-lint: source encoding_rs/src/x_user_defined.rs`)
- **Proposed provenance header:** `// port-lint: tests x_user_defined.rs` (current: `// port-lint: tests encoding_rs/src/x_user_defined.rs`)
- **Lint issues:** 2

### 18. replacement

- **Target:** `encodingrs.Replacement [PROVENANCE-FALLBACK]`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1102.9
- **Functions:** 10/10 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/replacement.rs` vs expected `replacement.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/replacement.rs` vs expected `replacement.rs`
- **Proposed provenance header:** `// port-lint: source replacement.rs` (current: `// port-lint: source encoding_rs/src/replacement.rs`)
- **Proposed provenance header:** `// port-lint: tests replacement.rs` (current: `// port-lint: tests encoding_rs/src/replacement.rs`)
- **Lint issues:** 2

### 19. ascii

- **Target:** `encodingrs.Ascii [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/ascii.rs` vs expected `ascii.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/ascii.rs` vs expected `ascii.rs`
- **Proposed provenance header:** `// port-lint: source ascii.rs` (current: `// port-lint: source encoding_rs/src/ascii.rs`)
- **Proposed provenance header:** `// port-lint: tests ascii.rs` (current: `// port-lint: tests encoding_rs/src/ascii.rs`)
- **Lint issues:** 2

### 20. test_labels_names

- **Target:** `encodingrs.TestLabelsNames [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/test_labels_names.rs` vs expected `test_labels_names.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/test_labels_names.rs` vs expected `test_labels_names.rs`
- **Proposed provenance header:** `// port-lint: source test_labels_names.rs` (current: `// port-lint: source encoding_rs/src/test_labels_names.rs`)
- **Proposed provenance header:** `// port-lint: tests test_labels_names.rs` (current: `// port-lint: tests encoding_rs/src/test_labels_names.rs`)
- **Lint issues:** 2

### 21. gb18030_2022

- **Target:** `encodingrs.Gb180302022 [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/gb18030_2022.rs` vs expected `gb18030_2022.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/gb18030_2022.rs` vs expected `gb18030_2022.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:encoding_rs/src/gb18030_2022.rs` vs expected `gb18030_2022.rs`
- **Proposed provenance header:** `// port-lint: source gb18030_2022.rs` (current: `// port-lint: source encoding_rs/src/gb18030_2022.rs`)
- **Proposed provenance header:** `// port-lint: source gb18030_2022.rs` (current: `// port-lint: source encoding_rs/src/gb18030_2022.rs`)
- **Proposed provenance header:** `// port-lint: tests gb18030_2022.rs` (current: `// port-lint: tests encoding_rs/src/gb18030_2022.rs`)
- **Lint issues:** 3

### 22. macros

- **Target:** `encodingrs.Macros [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `encoding_rs/src/macros.rs` vs expected `macros.rs`
- **Proposed provenance header:** `// port-lint: source macros.rs` (current: `// port-lint: source encoding_rs/src/macros.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

