# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/22 (100.0%)
- **Function parity:** 268/545 matched (target 415) — 49.2%
- **Class/type parity:** 61/69 matched (target 105) — 88.4%
- **Combined symbol parity:** 329/614 matched (target 520) — 53.6%
- **Average inline-code cosine:** 0.42 (function body across 19 matched files)
- **Average documentation cosine:** 0.10 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 18 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 852607.9
- **Functions:** 34/115 matched (target 48)
- **Missing functions:** `name`, `new_variant_decoder`, `utf8_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`, `sniff_to_utf16`, `test_bom_sniffing`, `test_output_encoding`, `test_label_resolution`, `test_decode_valid_windows_1257_to_cow`, `test_decode_invalid_windows_1257_to_cow`, `test_decode_ascii_only_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_invalid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow`, `test_decode_bomful_invalid_utf8_as_utf_8_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow_with_bom_removal`, `test_decode_valid_windows_1257_to_cow_with_bom_removal`, `test_decode_invalid_windows_1257_to_cow_with_bom_removal`, `test_decode_ascii_only_windows_1257_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling`, `test_decode_valid_windows_1257_to_cow_without_bom_handling`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_valid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_encode_ascii_only_windows_1257_to_cow`, `test_encode_valid_windows_1257_to_cow`, `test_utf16_space_with_one_bom_byte`, `test_utf8_space_with_one_bom_byte`, `test_utf16_space_with_two_bom_bytes`, `test_utf8_space_with_two_bom_bytes`, `test_utf16_space_with_one_bom_byte_and_a_second_byte_in_same_call`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf8`, `test_buffer_end_iso_2022_jp_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf16`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf16`, `test_buffer_end_iso_2022_jp_from_utf16`, `test_buffer_end_utf16be`, `test_hash`, `test_iso_2022_jp_ncr_extra_from_utf16`, `test_iso_2022_jp_ncr_extra_from_utf8`, `test_max_length_with_bom_to_utf8`, `test_serde`, `test_is_single_byte`, `test_latin1_byte_compatible_up_to`
- **Types:** 8/11 matched (target 14)
- **Missing types:** `EncodingVisitor`, `Value`, `Demo`
- **Tests:** 0/46 matched

### 2. mem

- **Target:** `encodingrs.Mem`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 438207.1
- **Functions:** 38/81 matched (target 40)
- **Missing functions:** `test_is_ascii_success`, `test_is_ascii_fail`, `test_is_basic_latin_success`, `test_is_basic_latin_fail`, `test_is_utf16_latin1_success`, `test_is_utf16_latin1_fail`, `test_is_str_latin1_success`, `test_is_str_latin1_fail`, `test_is_utf8_latin1_success`, `test_is_utf8_latin1_fail`, `test_is_utf8_latin1_invalid`, `test_convert_utf8_to_utf16`, `test_convert_str_to_utf16`, `test_convert_utf16_to_utf8_partial`, `test_convert_utf16_to_utf8`, `test_convert_latin1_to_utf16`, `test_convert_latin1_to_utf8_partial`, `test_convert_latin1_to_utf8`, `test_convert_utf8_to_latin1_lossy`, `test_convert_utf8_to_latin1_lossy_panics`, `test_convert_utf16_to_latin1_lossy`, `test_convert_utf16_to_latin1_lossy_panics`, `test_utf16_valid_up_to`, `test_ensure_utf16_validity`, `test_is_char_bidi`, `test_is_utf16_code_unit_bidi`, `test_is_str_bidi`, `test_is_utf8_bidi`, `test_is_utf16_bidi`, `test_check_str_for_latin1_and_bidi`, `test_check_utf8_for_latin1_and_bidi`, `test_check_utf16_for_latin1_and_bidi`, `reference_is_char_bidi`, `reference_is_utf16_code_unit_bidi`, `test_is_char_bidi_thoroughly`, `test_is_utf16_code_unit_bidi_thoroughly`, `test_is_str_bidi_thoroughly`, `test_is_utf8_bidi_thoroughly`, `test_is_utf16_bidi_thoroughly`, `test_is_utf8_bidi_edge_cases`, `test_decode_latin1`, `test_encode_latin1_lossy`, `test_convert_utf8_to_utf16_without_replacement`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 0/35 matched

### 3. simd_funcs

- **Target:** `encodingrs.SimdFuncs [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 222210.0
- **Functions:** 0/22 matched (target 0)
- **Missing functions:** `load16_unaligned`, `load16_aligned`, `store16_unaligned`, `store16_aligned`, `load8_unaligned`, `load8_aligned`, `store8_unaligned`, `store8_aligned`, `simd_byte_swap`, `to_u16_lanes`, `contains_surrogates`, `is_u16x8_bidi`, `simd_unpack`, `test_unpack`, `test_simd_is_basic_latin_success`, `test_simd_is_basic_latin_c0`, `test_simd_is_basic_latin_0fff`, `test_simd_is_basic_latin_ffff`, `test_simd_is_ascii_success`, `test_simd_is_ascii_failure`, `test_check_ascii`, `test_alu`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/9 matched

### 4. gb18030

- **Target:** `encodingrs.Gb18030`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 192408.6
- **Functions:** 4/21 matched (target 6)
- **Missing functions:** `is_none`, `in_neutral_state`, `extra_from_state`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`, `gbk_encode_non_unified`, `encode_hanzi`, `decode_gb18030`, `encode_gb18030`, `encode_gbk`, `test_gb18030_decode`, `test_gb18030_encode`, `test_gbk_encode`, `test_gb18030_decode_all`, `test_gb18030_encode_all`, `test_gb18030_encode_from_utf16_max_length`
- **Types:** 1/3 matched (target 5)
- **Missing types:** `Gb18030Decoder`, `Gb18030Encoder`
- **Tests:** 0/4 matched

### 5. utf_8

- **Target:** `encodingrs.Utf8`
- **Similarity:** 0.31
- **Dependents:** 0
- **Priority Score:** 173107.0
- **Functions:** 11/28 matched (target 15)
- **Missing functions:** `new_inner`, `extra_from_state`, `convert_utf16_to_utf8_partial_inner`, `convert_utf16_to_utf8_partial_tail`, `decode_utf8_to_utf8`, `decode_valid_utf8`, `encode_utf8_from_utf16`, `encode_utf8_from_utf8`, `encode_utf8_from_utf16_with_output_limit`, `test_utf8_decode`, `test_utf8_encode`, `test_encode_utf8_from_utf16_with_output_limit`, `test_utf8_max_length_from_utf16`, `test_decode_bom_prefixed_split_byte_triple`, `test_decode_bom_prefixed_split_byte_pair`, `test_decode_bom_prefix`, `test_tail`
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_
- **Tests:** 0/8 matched

### 6. utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.17
- **Dependents:** 0
- **Priority Score:** 162208.3
- **Functions:** 5/21 matched (target 7)
- **Missing functions:** `decode_utf_16le`, `decode_utf_16be`, `encode_utf_16le`, `encode_utf_16be`, `test_utf_16_decode`, `test_utf_16_encode`, `test_utf_16be_decode_one_by_one`, `test_utf_16le_decode_one_by_one`, `test_utf_16be_decode_three_at_a_time`, `test_utf_16le_decode_three_at_a_time`, `test_utf_16le_decode_bom_prefixed_split_byte_pair`, `test_utf_16be_decode_bom_prefixed_split_byte_pair`, `test_utf_16le_decode_bom_prefix`, `test_utf_16be_decode_bom_prefix`, `test_utf_16le_decode_near_end`, `test_utf_16be_decode_near_end`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/12 matched

### 7. big5

- **Target:** `encodingrs.Big5`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 141708.1
- **Functions:** 3/15 matched (target 5)
- **Missing functions:** `in_neutral_state`, `plus_one_if_lead`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`, `decode_big5`, `encode_big5`, `test_big5_decode`, `test_big5_encode`, `test_big5_decode_all`, `test_big5_encode_all`, `test_big5_encode_from_two_low_surrogates`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `Big5Decoder`, `Big5Encoder`
- **Tests:** 0/2 matched

### 8. data

- **Target:** `encodingrs.Data`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 114804.1
- **Functions:** 37/47 matched (target 40)
- **Missing functions:** `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_hanzi_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `mul_94`
- **Types:** 0/1 matched
- **Missing types:** `SingleByteData`

### 9. single_byte

- **Target:** `encodingrs.SingleByte`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 102306.3
- **Functions:** 11/21 matched (target 15)
- **Missing functions:** `test_windows_1255_ca`, `test_ascii_punctuation`, `test_decode_malformed`, `test_encode_unmappables`, `test_encode_unpaired_surrogates`, `decode_single_byte`, `encode_single_byte`, `test_single_byte_from_two_low_surrogates`, `test_single_byte_decode`, `test_single_byte_encode`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Tests:** 0/8 matched

### 10. iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 92605.5
- **Functions:** 13/22 matched (target 16)
- **Missing functions:** `decode_iso_2022_jp`, `encode_iso_2022_jp`, `test_iso_2022_jp_decode`, `test_iso_2022_jp_encode`, `test_iso_2022_jp_decode_all`, `test_iso_2022_jp_encode_all`, `test_iso_2022_jp_half_width_katakana_length`, `test_iso_2022_jp_length_after_escape`, `test_iso_2022_jp_encode_from_two_low_surrogates`
- **Types:** 4/4 matched
- **Missing types:** _none_
- **Tests:** 0/5 matched

### 11. handles

- **Target:** `encodingrs.Handles`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 87302.8
- **Functions:** 37/45 matched (target 125)
- **Missing functions:** `new`, `simd_at`, `len`, `copy_unaligned_basic_latin_to_ascii_alu`, `swap_if_opposite_endian`, `copy_unaligned_basic_latin_to_ascii`, `convert_unaligned_utf16_to_utf8`, `copy_utf16_from`
- **Types:** 28/28 matched (target 36)
- **Missing types:** _none_

### 12. euc_jp

- **Target:** `encodingrs.EucJp`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 82106.1
- **Functions:** 10/18 matched (target 13)
- **Missing functions:** `is_none`, `decode_euc_jp`, `encode_euc_jp`, `test_euc_jp_decode`, `test_euc_jp_encode`, `test_jis0208_decode_all`, `test_jis0208_encode_all`, `test_jis0212_decode_all`
- **Types:** 3/3 matched (target 8)
- **Missing types:** _none_
- **Tests:** 0/2 matched

### 13. euc_kr

- **Target:** `encodingrs.EucKr`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 72005.5
- **Functions:** 11/18 matched (target 14)
- **Missing functions:** `decode_euc_kr`, `encode_euc_kr`, `test_euc_kr_decode`, `test_euc_kr_encode`, `test_euc_kr_decode_all`, `test_euc_kr_encode_all`, `test_euc_kr_encode_from_two_low_surrogates`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Tests:** 0/3 matched

### 14. shift_jis

- **Target:** `encodingrs.ShiftJis`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 71805.5
- **Functions:** 9/16 matched (target 12)
- **Missing functions:** `decode_shift_jis`, `encode_shift_jis`, `test_shift_jis_decode`, `test_shift_jis_encode`, `test_shift_jis_decode_all`, `test_shift_jis_encode_all`, `test_shift_jis_half_width_katakana_length`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Tests:** 0/3 matched

### 15. x_user_defined

- **Target:** `encodingrs.XUserDefined`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 51405.6
- **Functions:** 7/12 matched (target 10)
- **Missing functions:** `decode_x_user_defined`, `encode_x_user_defined`, `test_x_user_defined_decode`, `test_x_user_defined_encode`, `test_x_user_defined_from_two_low_surrogates`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Tests:** 0/3 matched

### 16. replacement

- **Target:** `encodingrs.Replacement`
- **Similarity:** 0.48
- **Dependents:** 0
- **Priority Score:** 41105.2
- **Functions:** 6/10 matched (target 6)
- **Missing functions:** `decode_replacement`, `encode_replacement`, `test_replacement_decode`, `test_replacement_encode`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/2 matched

### 17. test_labels_names

- **Target:** `encodingrs.TestLabelsNames [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `test_all_labels`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/1 matched

### 18. variant

- **Target:** `encodingrs.Variant`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 1702.8
- **Functions:** 14/14 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 16)
- **Missing types:** _none_

### 19. testing

- **Target:** `encodingrs.Testing`
- **Similarity:** 0.81
- **Dependents:** 0
- **Priority Score:** 1601.9
- **Functions:** 16/16 matched (target 17)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 20. ascii

- **Target:** `encodingrs.Ascii`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 8)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 21. gb18030_2022

- **Target:** `encodingrs.Gb180302022 [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 3)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 22. macros

- **Target:** `encodingrs.Macros [STUB]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

