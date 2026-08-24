# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 11/22 (50.0%)
- **Function parity:** 214/558 matched (target 355) — 38.4%
- **Class/type parity:** 45/69 matched (target 88) — 65.2%
- **Combined symbol parity:** 259/627 matched (target 443) — 41.3%
- **Average inline-code cosine:** 0.47 (function body across 11 matched files)
- **Average documentation cosine:** 0.17 (doc text across 11 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 6 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results [PROVENANCE-FALLBACK]`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 942608.4
- **Functions:** 26/115 matched (target 43)
- **Missing functions:** `name`, `is_potentially_borrowable`, `decode_with_bom_removal`, `utf8_valid_up_to`, `ascii_valid_up_to`, `iso_2022_jp_ascii_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `max_utf8_buffer_length_without_replacement`, `decode_to_utf8`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `latin1_byte_compatible_up_to`, `has_pending_state`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`, `sniff_to_utf16`, `test_bom_sniffing`, `test_output_encoding`, `test_label_resolution`, `test_decode_valid_windows_1257_to_cow`, `test_decode_invalid_windows_1257_to_cow`, `test_decode_ascii_only_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_invalid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow`, `test_decode_bomful_invalid_utf8_as_utf_8_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow_with_bom_removal`, `test_decode_valid_windows_1257_to_cow_with_bom_removal`, `test_decode_invalid_windows_1257_to_cow_with_bom_removal`, `test_decode_ascii_only_windows_1257_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling`, `test_decode_valid_windows_1257_to_cow_without_bom_handling`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_valid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_encode_ascii_only_windows_1257_to_cow`, `test_encode_valid_windows_1257_to_cow`, `test_utf16_space_with_one_bom_byte`, `test_utf8_space_with_one_bom_byte`, `test_utf16_space_with_two_bom_bytes`, `test_utf8_space_with_two_bom_bytes`, `test_utf16_space_with_one_bom_byte_and_a_second_byte_in_same_call`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf8`, `test_buffer_end_iso_2022_jp_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf16`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf16`, `test_buffer_end_iso_2022_jp_from_utf16`, `test_buffer_end_utf16be`, `test_hash`, `test_iso_2022_jp_ncr_extra_from_utf16`, `test_iso_2022_jp_ncr_extra_from_utf8`, `test_max_length_with_bom_to_utf8`, `test_serde`, `test_is_single_byte`, `test_latin1_byte_compatible_up_to`
- **Types:** 7/11 matched (target 29)
- **Missing types:** `EncodingVisitor`, `Value`, `DecoderLifeCycle`, `Demo`
- **Tests:** 0/46 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 2

### 2. data

- **Target:** `encodingrs.Data [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 484810.0
- **Functions:** 0/47 matched (target 0)
- **Missing functions:** `map_with_ranges`, `map_with_unsorted_ranges`, `position`, `gb18030_range_decode`, `gb18030_range_encode`, `gbk_top_ideograph_decode`, `gbk_top_ideograph_encode`, `gbk_left_ideograph_decode`, `gbk_left_ideograph_encode`, `cp949_top_hangul_decode`, `cp949_left_hangul_decode`, `cp949_top_hangul_encode`, `cp949_left_hangul_encode`, `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_other_decode`, `gbk_other_encode`, `gb2312_other_decode`, `gb2312_other_encode`, `gbk_hanzi_encode`, `gb2312_level1_hanzi_encode`, `gb2312_level2_hanzi_encode`, `ksx1001_other_decode`, `ksx1001_other_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `jis0208_level1_kanji_shift_jis_encode`, `jis0208_level1_kanji_euc_jp_encode`, `jis0208_level1_kanji_iso_2022_jp_encode`, `jis0208_level2_and_additional_kanji_encode`, `jis0208_symbol_decode`, `jis0208_symbol_encode`, `ibm_symbol_encode`, `jis0208_range_decode`, `jis0208_range_encode`, `jis0212_accented_decode`, `big5_is_astral`, `big5_low_bits`, `big5_astral_encode`, `big5_level1_hanzi_encode`, `big5_box_encode`, `big5_other_encode`, `mul_94`
- **Types:** 0/1 matched
- **Missing types:** `SingleByteData`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Lint issues:** 1

### 3. mem

- **Target:** `encodingrs.Mem [PROVENANCE-FALLBACK]`
- **Similarity:** 0.49
- **Dependents:** 0
- **Priority Score:** 208205.1
- **Functions:** 61/81 matched (target 63)
- **Missing functions:** `utf16_valid_up_to_alu`, `is_utf8_latin1_impl`, `test_convert_utf8_to_latin1_lossy_panics`, `test_convert_utf16_to_latin1_lossy_panics`, `test_is_utf8_bidi`, `test_is_utf16_bidi`, `test_check_str_for_latin1_and_bidi`, `test_check_utf8_for_latin1_and_bidi`, `test_check_utf16_for_latin1_and_bidi`, `reference_is_char_bidi`, `reference_is_utf16_code_unit_bidi`, `test_is_char_bidi_thoroughly`, `test_is_utf16_code_unit_bidi_thoroughly`, `test_is_str_bidi_thoroughly`, `test_is_utf8_bidi_thoroughly`, `test_is_utf16_bidi_thoroughly`, `test_is_utf8_bidi_edge_cases`, `test_decode_latin1`, `test_encode_latin1_lossy`, `test_convert_utf8_to_utf16_without_replacement`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 25/35 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `mem.rs` vs expected `mem.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:mem.rs` vs expected `mem.rs`
- **Proposed provenance header:** `// port-lint: source mem.rs` (current: `// port-lint: source mem.rs`)
- **Proposed provenance header:** `// port-lint: tests mem.rs` (current: `// port-lint: tests mem.rs`)
- **Lint issues:** 2

### 4. handles

- **Target:** `encodingrs.Handles [PROVENANCE-FALLBACK]`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 87302.8
- **Functions:** 37/45 matched (target 130)
- **Missing functions:** `new`, `simd_at`, `len`, `copy_unaligned_basic_latin_to_ascii_alu`, `swap_if_opposite_endian`, `copy_unaligned_basic_latin_to_ascii`, `convert_unaligned_utf16_to_utf8`, `copy_utf16_from`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handles.rs` vs expected `handles.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handles.rs` vs expected `handles.rs`
- **Proposed provenance header:** `// port-lint: source handles.rs` (current: `// port-lint: source handles.rs`)
- **Proposed provenance header:** `// port-lint: tests handles.rs` (current: `// port-lint: tests handles.rs`)
- **Lint issues:** 2

### 5. utf_8

- **Target:** `encodingrs.Utf8 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 43104.0
- **Functions:** 24/28 matched
- **Missing functions:** `new_inner`, `extra_from_state`, `convert_utf16_to_utf8_partial_inner`, `convert_utf16_to_utf8_partial_tail`
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `utf_8.rs` vs expected `utf_8.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:utf_8.rs` vs expected `utf_8.rs`
- **Proposed provenance header:** `// port-lint: source utf_8.rs` (current: `// port-lint: source utf_8.rs`)
- **Proposed provenance header:** `// port-lint: tests utf_8.rs` (current: `// port-lint: tests utf_8.rs`)
- **Lint issues:** 2

### 6. single_byte

- **Target:** `encodingrs.SingleByte [PROVENANCE-FALLBACK]`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 2304.1
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `single_byte.rs` vs expected `single_byte.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:single_byte.rs` vs expected `single_byte.rs`
- **Proposed provenance header:** `// port-lint: source single_byte.rs` (current: `// port-lint: source single_byte.rs`)
- **Proposed provenance header:** `// port-lint: tests single_byte.rs` (current: `// port-lint: tests single_byte.rs`)
- **Lint issues:** 2

### 7. utf_16

- **Target:** `encodingrs.Utf16 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `utf_16.rs` vs expected `utf_16.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:utf_16.rs` vs expected `utf_16.rs`
- **Proposed provenance header:** `// port-lint: source utf_16.rs` (current: `// port-lint: source utf_16.rs`)
- **Proposed provenance header:** `// port-lint: tests utf_16.rs` (current: `// port-lint: tests utf_16.rs`)
- **Lint issues:** 2

### 8. x_user_defined

- **Target:** `encodingrs.XUserDefined [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 1403.9
- **Functions:** 12/12 matched (target 15)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `x_user_defined.rs` vs expected `x_user_defined.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:x_user_defined.rs` vs expected `x_user_defined.rs`
- **Proposed provenance header:** `// port-lint: source x_user_defined.rs` (current: `// port-lint: source x_user_defined.rs`)
- **Proposed provenance header:** `// port-lint: tests x_user_defined.rs` (current: `// port-lint: tests x_user_defined.rs`)
- **Lint issues:** 2

### 9. replacement

- **Target:** `encodingrs.Replacement [PROVENANCE-FALLBACK]`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1102.9
- **Functions:** 10/10 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `replacement.rs` vs expected `replacement.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:replacement.rs` vs expected `replacement.rs`
- **Proposed provenance header:** `// port-lint: source replacement.rs` (current: `// port-lint: source replacement.rs`)
- **Proposed provenance header:** `// port-lint: tests replacement.rs` (current: `// port-lint: tests replacement.rs`)
- **Lint issues:** 2

### 10. ascii

- **Target:** `encodingrs.Ascii [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ascii.rs` vs expected `ascii.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:ascii.rs` vs expected `ascii.rs`
- **Proposed provenance header:** `// port-lint: source ascii.rs` (current: `// port-lint: source ascii.rs`)
- **Proposed provenance header:** `// port-lint: tests ascii.rs` (current: `// port-lint: tests ascii.rs`)
- **Lint issues:** 2

### 11. gb18030_2022

- **Target:** `encodingrs.Gb180302022 [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gb18030_2022.rs` vs expected `gb18030_2022.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gb18030_2022.rs` vs expected `gb18030_2022.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:gb18030_2022.rs` vs expected `gb18030_2022.rs`
- **Proposed provenance header:** `// port-lint: source gb18030_2022.rs` (current: `// port-lint: source gb18030_2022.rs`)
- **Proposed provenance header:** `// port-lint: source gb18030_2022.rs` (current: `// port-lint: source gb18030_2022.rs`)
- **Proposed provenance header:** `// port-lint: tests gb18030_2022.rs` (current: `// port-lint: tests gb18030_2022.rs`)
- **Lint issues:** 3

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

