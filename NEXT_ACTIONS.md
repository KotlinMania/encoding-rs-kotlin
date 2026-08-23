# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/22 (40.9%)
- **Function parity:** 117/669 matched (target 163) — 17.5%
- **Class/type parity:** 16/69 matched (target 48) — 23.2%
- **Combined symbol parity:** 133/738 matched (target 211) — 18.0%
- **Average inline-code cosine:** 0.44 (function body across 9 matched files)
- **Average documentation cosine:** 0.14 (doc text across 9 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 5 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 932608.4
- **Functions:** 27/115 matched (target 44)
- **Missing functions:** `is_potentially_borrowable`, `decode_with_bom_removal`, `utf8_valid_up_to`, `ascii_valid_up_to`, `iso_2022_jp_ascii_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `max_utf8_buffer_length_without_replacement`, `decode_to_utf8`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `latin1_byte_compatible_up_to`, `has_pending_state`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`, `sniff_to_utf16`, `test_bom_sniffing`, `test_output_encoding`, `test_label_resolution`, `test_decode_valid_windows_1257_to_cow`, `test_decode_invalid_windows_1257_to_cow`, `test_decode_ascii_only_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_invalid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow`, `test_decode_bomful_invalid_utf8_as_utf_8_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow_with_bom_removal`, `test_decode_valid_windows_1257_to_cow_with_bom_removal`, `test_decode_invalid_windows_1257_to_cow_with_bom_removal`, `test_decode_ascii_only_windows_1257_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling`, `test_decode_valid_windows_1257_to_cow_without_bom_handling`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_valid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_encode_ascii_only_windows_1257_to_cow`, `test_encode_valid_windows_1257_to_cow`, `test_utf16_space_with_one_bom_byte`, `test_utf8_space_with_one_bom_byte`, `test_utf16_space_with_two_bom_bytes`, `test_utf8_space_with_two_bom_bytes`, `test_utf16_space_with_one_bom_byte_and_a_second_byte_in_same_call`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf8`, `test_buffer_end_iso_2022_jp_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf16`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf16`, `test_buffer_end_iso_2022_jp_from_utf16`, `test_buffer_end_utf16be`, `test_hash`, `test_iso_2022_jp_ncr_extra_from_utf16`, `test_iso_2022_jp_ncr_extra_from_utf8`, `test_max_length_with_bom_to_utf8`, `test_serde`, `test_is_single_byte`, `test_latin1_byte_compatible_up_to`
- **Types:** 7/11 matched (target 29)
- **Missing types:** `EncodingVisitor`, `Value`, `DecoderLifeCycle`, `Demo`
- **Tests:** 0/46 matched

### 2. data

- **Target:** `encodingrs.Data [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 484810.0
- **Functions:** 0/47 matched (target 0)
- **Missing functions:** `map_with_ranges`, `map_with_unsorted_ranges`, `position`, `gb18030_range_decode`, `gb18030_range_encode`, `gbk_top_ideograph_decode`, `gbk_top_ideograph_encode`, `gbk_left_ideograph_decode`, `gbk_left_ideograph_encode`, `cp949_top_hangul_decode`, `cp949_left_hangul_decode`, `cp949_top_hangul_encode`, `cp949_left_hangul_encode`, `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_other_decode`, `gbk_other_encode`, `gb2312_other_decode`, `gb2312_other_encode`, `gbk_hanzi_encode`, `gb2312_level1_hanzi_encode`, `gb2312_level2_hanzi_encode`, `ksx1001_other_decode`, `ksx1001_other_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `jis0208_level1_kanji_shift_jis_encode`, `jis0208_level1_kanji_euc_jp_encode`, `jis0208_level1_kanji_iso_2022_jp_encode`, `jis0208_level2_and_additional_kanji_encode`, `jis0208_symbol_decode`, `jis0208_symbol_encode`, `ibm_symbol_encode`, `jis0208_range_decode`, `jis0208_range_encode`, `jis0212_accented_decode`, `big5_is_astral`, `big5_low_bits`, `big5_astral_encode`, `big5_level1_hanzi_encode`, `big5_box_encode`, `big5_other_encode`, `mul_94`
- **Types:** 0/1 matched
- **Missing types:** `SingleByteData`

### 3. utf_8

- **Target:** `encodingrs.Utf8`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 43104.0
- **Functions:** 24/28 matched
- **Missing functions:** `new_inner`, `extra_from_state`, `convert_utf16_to_utf8_partial_inner`, `convert_utf16_to_utf8_partial_tail`
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Lint issues:** 2

### 4. single_byte

- **Target:** `encodingrs.SingleByte`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 2304.1
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Lint issues:** 2

### 5. utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched

### 6. x_user_defined

- **Target:** `encodingrs.XUserDefined`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 1403.9
- **Functions:** 12/12 matched (target 15)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Lint issues:** 3

### 7. replacement

- **Target:** `encodingrs.Replacement`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1102.9
- **Functions:** 10/10 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Lint issues:** 2

### 8. ascii

- **Target:** `encodingrs.Ascii`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 9. gb18030_2022

- **Target:** `encodingrs.Gb180302022 [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 4)
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

