# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/22 (100.0%)
- **Function parity:** 437/545 matched (target 603) — 80.2%
- **Class/type parity:** 61/69 matched (target 121) — 88.4%
- **Combined symbol parity:** 498/614 matched (target 724) — 81.1%
- **Average inline-code cosine:** 0.57 (function body across 19 matched files)
- **Average documentation cosine:** 0.10 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 11 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 412605.8
- **Functions:** 78/115 matched (target 92)
- **Missing functions:** `name`, `new_variant_decoder`, `utf8_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`, `test_iso_2022_jp_ncr_extra_from_utf16`, `test_iso_2022_jp_ncr_extra_from_utf8`, `test_serde`
- **Types:** 8/11 matched (target 15)
- **Missing types:** `EncodingVisitor`, `Value`, `Demo`
- **Tests:** 43/46 matched

### 2. simd_funcs

- **Target:** `encodingrs.SimdFuncs [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 222210.0
- **Functions:** 0/22 matched (target 0)
- **Missing functions:** `load16_unaligned`, `load16_aligned`, `store16_unaligned`, `store16_aligned`, `load8_unaligned`, `load8_aligned`, `store8_unaligned`, `store8_aligned`, `simd_byte_swap`, `to_u16_lanes`, `contains_surrogates`, `is_u16x8_bidi`, `simd_unpack`, `test_unpack`, `test_simd_is_basic_latin_success`, `test_simd_is_basic_latin_c0`, `test_simd_is_basic_latin_0fff`, `test_simd_is_basic_latin_ffff`, `test_simd_is_ascii_success`, `test_simd_is_ascii_failure`, `test_check_ascii`, `test_alu`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/9 matched

### 3. gb18030

- **Target:** `encodingrs.Gb18030`
- **Similarity:** 0.35
- **Dependents:** 0
- **Priority Score:** 122406.5
- **Functions:** 11/21 matched (target 13)
- **Missing functions:** `is_none`, `in_neutral_state`, `extra_from_state`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`, `gbk_encode_non_unified`, `encode_hanzi`, `test_gb18030_decode_all`, `test_gb18030_encode_all`
- **Types:** 1/3 matched (target 5)
- **Missing types:** `Gb18030Decoder`, `Gb18030Encoder`
- **Tests:** 4/4 matched

### 4. data

- **Target:** `encodingrs.Data`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 114804.1
- **Functions:** 37/47 matched (target 40)
- **Missing functions:** `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_hanzi_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `mul_94`
- **Types:** 0/1 matched
- **Missing types:** `SingleByteData`

### 5. big5

- **Target:** `encodingrs.Big5`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 91705.9
- **Functions:** 8/15 matched (target 10)
- **Missing functions:** `in_neutral_state`, `plus_one_if_lead`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`, `test_big5_decode_all`, `test_big5_encode_all`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `Big5Decoder`, `Big5Encoder`
- **Tests:** 2/2 matched

### 6. handles

- **Target:** `encodingrs.Handles`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 87302.8
- **Functions:** 37/45 matched (target 130)
- **Missing functions:** `new`, `simd_at`, `len`, `copy_unaligned_basic_latin_to_ascii_alu`, `swap_if_opposite_endian`, `copy_unaligned_basic_latin_to_ascii`, `convert_unaligned_utf16_to_utf8`, `copy_utf16_from`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_

### 7. utf_8

- **Target:** `encodingrs.Utf8`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 43104.0
- **Functions:** 24/28 matched (target 29)
- **Missing functions:** `new_inner`, `extra_from_state`, `convert_utf16_to_utf8_partial_inner`, `convert_utf16_to_utf8_partial_tail`
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 8. euc_jp

- **Target:** `encodingrs.EucJp`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 42104.8
- **Functions:** 14/18 matched (target 17)
- **Missing functions:** `is_none`, `test_jis0208_decode_all`, `test_jis0208_encode_all`, `test_jis0212_decode_all`
- **Types:** 3/3 matched (target 9)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 9. iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp`
- **Similarity:** 0.63
- **Dependents:** 0
- **Priority Score:** 22603.7
- **Functions:** 20/22 matched (target 23)
- **Missing functions:** `test_iso_2022_jp_decode_all`, `test_iso_2022_jp_encode_all`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 5/5 matched

### 10. euc_kr

- **Target:** `encodingrs.EucKr`
- **Similarity:** 0.62
- **Dependents:** 0
- **Priority Score:** 22003.8
- **Functions:** 16/18 matched (target 19)
- **Missing functions:** `test_euc_kr_decode_all`, `test_euc_kr_encode_all`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 11. shift_jis

- **Target:** `encodingrs.ShiftJis`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 21803.5
- **Functions:** 14/16 matched (target 17)
- **Missing functions:** `test_shift_jis_decode_all`, `test_shift_jis_encode_all`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 12. mem

- **Target:** `encodingrs.Mem`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 8203.9
- **Functions:** 81/81 matched (target 83)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 35/35 matched

### 13. single_byte

- **Target:** `encodingrs.SingleByte`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 2304.1
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 14. utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched

### 15. variant

- **Target:** `encodingrs.Variant`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 1702.8
- **Functions:** 14/14 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 16)
- **Missing types:** _none_

### 16. testing

- **Target:** `encodingrs.Testing`
- **Similarity:** 0.82
- **Dependents:** 0
- **Priority Score:** 1601.8
- **Functions:** 16/16 matched (target 23)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 17. x_user_defined

- **Target:** `encodingrs.XUserDefined`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 1403.9
- **Functions:** 12/12 matched (target 15)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 18. replacement

- **Target:** `encodingrs.Replacement`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1102.9
- **Functions:** 10/10 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 19. ascii

- **Target:** `encodingrs.Ascii`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 20. test_labels_names

- **Target:** `encodingrs.TestLabelsNames [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 21. gb18030_2022

- **Target:** `encodingrs.Gb180302022 [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
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

