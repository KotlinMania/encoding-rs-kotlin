# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/22 (100.0%)
- **Function parity:** 463/545 matched (target 629) — 85.0%
- **Class/type parity:** 62/69 matched (target 123) — 89.9%
- **Combined symbol parity:** 525/614 matched (target 752) — 85.5%
- **Average inline-code cosine:** 0.59 (function body across 19 matched files)
- **Average documentation cosine:** 0.10 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 10 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results`
- **Similarity:** 0.43
- **Dependents:** 0
- **Priority Score:** 382605.7
- **Functions:** 81/115 matched (target 95)
- **Missing functions:** `name`, `new_variant_decoder`, `utf8_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`
- **Types:** 8/11 matched (target 15)
- **Missing types:** `EncodingVisitor`, `Value`, `Demo`
- **Tests:** 46/46 matched

### 2. simd_funcs

- **Target:** `encodingrs.SimdFuncs [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 132210.0
- **Functions:** 9/22 matched (target 9)
- **Missing functions:** `load16_unaligned`, `load16_aligned`, `store16_unaligned`, `store16_aligned`, `load8_unaligned`, `load8_aligned`, `store8_unaligned`, `store8_aligned`, `simd_byte_swap`, `to_u16_lanes`, `contains_surrogates`, `is_u16x8_bidi`, `simd_unpack`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 3. gb18030

- **Target:** `encodingrs.Gb18030`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 102406.1
- **Functions:** 13/21 matched (target 15)
- **Missing functions:** `is_none`, `in_neutral_state`, `extra_from_state`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`, `gbk_encode_non_unified`, `encode_hanzi`
- **Types:** 1/3 matched (target 5)
- **Missing types:** `Gb18030Decoder`, `Gb18030Encoder`
- **Tests:** 4/4 matched

### 4. data

- **Target:** `encodingrs.Data`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 94803.9
- **Functions:** 38/47 matched (target 41)
- **Missing functions:** `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_hanzi_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 5. handles

- **Target:** `encodingrs.Handles`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 87302.8
- **Functions:** 37/45 matched (target 130)
- **Missing functions:** `new`, `simd_at`, `len`, `copy_unaligned_basic_latin_to_ascii_alu`, `swap_if_opposite_endian`, `copy_unaligned_basic_latin_to_ascii`, `convert_unaligned_utf16_to_utf8`, `copy_utf16_from`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_

### 6. big5

- **Target:** `encodingrs.Big5`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 71705.3
- **Functions:** 10/15 matched (target 12)
- **Missing functions:** `in_neutral_state`, `plus_one_if_lead`, `max_utf16_buffer_length`, `max_utf8_buffer_length_without_replacement`, `max_utf8_buffer_length`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `Big5Decoder`, `Big5Encoder`
- **Tests:** 2/2 matched

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
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 12104.1
- **Functions:** 17/18 matched (target 20)
- **Missing functions:** `is_none`
- **Types:** 3/3 matched (target 9)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 9. mem

- **Target:** `encodingrs.Mem`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 8203.9
- **Functions:** 81/81 matched (target 83)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 35/35 matched

### 10. iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2603.3
- **Functions:** 22/22 matched (target 25)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 5/5 matched

### 11. single_byte

- **Target:** `encodingrs.SingleByte`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 2304.1
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 12. utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched

### 13. euc_kr

- **Target:** `encodingrs.EucKr`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2003.3
- **Functions:** 18/18 matched (target 21)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 14. shift_jis

- **Target:** `encodingrs.ShiftJis`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1802.9
- **Functions:** 16/16 matched (target 19)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

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

