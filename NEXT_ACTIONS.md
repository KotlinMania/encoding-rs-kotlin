# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 21/22 (95.5%)
- **Function parity:** 526/565 matched (target 586) — 93.1%
- **Class/type parity:** 58/58 matched (target 112) — 100.0%
- **Combined symbol parity:** 584/623 matched (target 698) — 93.7%
- **Average inline-code cosine:** 0.22 (function body across 21 matched files)
- **Average documentation cosine:** 0.10 (doc text across 21 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 20 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 1 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. handles

- **Target:** `encodingrs.Handles`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 258407.3
- **Functions:** 132/156 matched (target 137)
- **Missing functions:** `len`, `copy_bmp_to`, `copy_unaligned_basic_latin_to_ascii`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `copy_utf16_from`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `new`, `new`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_

### 2. data

- **Target:** `encodingrs.Data`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 75507.0
- **Functions:** 47/54 matched (target 50)
- **Missing functions:** `gb2312_level1_hanzi_encode`, `jis0208_level1_kanji_shift_jis_encode`, `jis0208_level1_kanji_euc_jp_encode`, `jis0208_level1_kanji_iso_2022_jp_encode`, `big5_level1_hanzi_encode`, `big5_level1_hanzi_encode`, `big5_other_encode`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 3. iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 22907.9
- **Functions:** 23/25 matched
- **Missing functions:** `is_kanji_mapped`, `encode_kanji`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 5/5 matched

### 4. euc_kr

- **Target:** `encodingrs.EucKr`
- **Similarity:** 0.20
- **Dependents:** 0
- **Priority Score:** 22308.0
- **Functions:** 19/21 matched
- **Missing functions:** `ksx1001_encode_hangul`, `ksx1001_encode_hanja`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 5. gb18030

- **Target:** `encodingrs.Gb18030`
- **Similarity:** 0.17
- **Dependents:** 0
- **Priority Score:** 12608.3
- **Functions:** 22/23 matched (target 25)
- **Missing functions:** `encode_hanzi`
- **Types:** 3/3 matched (target 8)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 6. euc_jp

- **Target:** `encodingrs.EucJp`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 12308.6
- **Functions:** 19/20 matched (target 21)
- **Missing functions:** `encode_kanji`
- **Types:** 3/3 matched (target 9)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 7. shift_jis

- **Target:** `encodingrs.ShiftJis`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 12008.1
- **Functions:** 17/18 matched (target 19)
- **Missing functions:** `encode_kanji`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 8. x_user_defined

- **Target:** `encodingrs.XUserDefined`
- **Similarity:** 0.12
- **Dependents:** 0
- **Priority Score:** 11608.8
- **Functions:** 13/14 matched (target 15)
- **Missing functions:** `decode_to_utf16_raw`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 9. mem

- **Target:** `encodingrs.Mem`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 8208.5
- **Functions:** 81/81 matched (target 83)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 35/35 matched

### 10. utf_8

- **Target:** `encodingrs.Utf8`
- **Similarity:** 0.20
- **Dependents:** 0
- **Priority Score:** 3208.0
- **Functions:** 29/29 matched (target 33)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 11. single_byte

- **Target:** `encodingrs.SingleByte`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 2408.7
- **Functions:** 22/22 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 12. simd_funcs

- **Target:** `encodingrs.SimdFuncs`
- **Similarity:** 0.07
- **Dependents:** 0
- **Priority Score:** 2209.3
- **Functions:** 22/22 matched (target 28)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 13. utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 2208.7
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched

### 14. big5

- **Target:** `encodingrs.Big5`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 1808.5
- **Functions:** 16/16 matched (target 18)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 15. variant

- **Target:** `encodingrs.Variant`
- **Similarity:** 0.17
- **Dependents:** 0
- **Priority Score:** 1708.3
- **Functions:** 14/14 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 16)
- **Missing types:** _none_

### 16. testing

- **Target:** `encodingrs.Testing`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 1605.9
- **Functions:** 16/16 matched (target 23)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 17. replacement

- **Target:** `encodingrs.Replacement`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 1107.9
- **Functions:** 10/10 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 18. ascii

- **Target:** `encodingrs.Ascii`
- **Similarity:** 0.31
- **Dependents:** 0
- **Priority Score:** 206.9
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 19. test_labels_names

- **Target:** `encodingrs.TestLabelsNames`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 108.7
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Matched

| Source | Target | Path |
|--------|--------|------|
| `gb18030_2022` | `encodingrs.Gb180302022` | `gb18030_2022` |
| `macros` | `encodingrs.Macros` | `macros` |

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

