# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/22 (100.0%)
- **Function parity:** 543/545 matched (target 717) — 99.6%
- **Class/type parity:** 69/69 matched (target 130) — 100.0%
- **Combined symbol parity:** 612/614 matched (target 847) — 99.7%
- **Average inline-code cosine:** 0.61 (function body across 19 matched files)
- **Average documentation cosine:** 0.11 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 8 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. encoding_rs.lib

- **Target:** `encodingrs.Results [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 22610.0
- **Functions:** 114/115 matched (target 131)
- **Missing functions:** `name`
- **Types:** 11/11 matched (target 18)
- **Missing types:** _none_
- **Tests:** 46/46 matched

### 2. encoding_rs.handles

- **Target:** `encodingrs.Handles`
- **Similarity:** 0.74
- **Dependents:** 0
- **Priority Score:** 17302.6
- **Functions:** 44/45 matched (target 137)
- **Missing functions:** `len`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_

### 3. encoding_rs.mem

- **Target:** `encodingrs.Mem`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 8203.9
- **Functions:** 81/81 matched (target 83)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 35/35 matched

### 4. encoding_rs.data

- **Target:** `encodingrs.Data`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 4803.1
- **Functions:** 47/47 matched (target 50)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 5. encoding_rs.utf_8

- **Target:** `encodingrs.Utf8`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 3103.1
- **Functions:** 28/28 matched (target 33)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 6. encoding_rs.iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2603.3
- **Functions:** 22/22 matched (target 25)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 5/5 matched

### 7. encoding_rs.gb18030

- **Target:** `encodingrs.Gb18030`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 2404.0
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 8)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 8. encoding_rs.single_byte

- **Target:** `encodingrs.SingleByte`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 2304.1
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 8/8 matched

### 9. encoding_rs.simd_funcs

- **Target:** `encodingrs.SimdFuncs`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 2207.3
- **Functions:** 22/22 matched (target 28)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 10. encoding_rs.utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched

### 11. encoding_rs.euc_jp

- **Target:** `encodingrs.EucJp`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 2104.0
- **Functions:** 18/18 matched (target 21)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 9)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 12. encoding_rs.euc_kr

- **Target:** `encodingrs.EucKr`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2003.3
- **Functions:** 18/18 matched (target 21)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 13. encoding_rs.shift_jis

- **Target:** `encodingrs.ShiftJis`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1802.9
- **Functions:** 16/16 matched (target 19)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 14. encoding_rs.big5

- **Target:** `encodingrs.Big5`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 1703.2
- **Functions:** 15/15 matched (target 18)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 15. encoding_rs.variant

- **Target:** `encodingrs.Variant`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 1702.8
- **Functions:** 14/14 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 16)
- **Missing types:** _none_

### 16. encoding_rs.testing

- **Target:** `encodingrs.Testing`
- **Similarity:** 0.82
- **Dependents:** 0
- **Priority Score:** 1601.8
- **Functions:** 16/16 matched (target 23)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 17. encoding_rs.x_user_defined

- **Target:** `encodingrs.XUserDefined`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 1403.9
- **Functions:** 12/12 matched (target 15)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 18. encoding_rs.replacement

- **Target:** `encodingrs.Replacement`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1102.9
- **Functions:** 10/10 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 19. encoding_rs.ascii

- **Target:** `encodingrs.Ascii`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 20. encoding_rs.test_labels_names

- **Target:** `encodingrs.TestLabelsNames [STUB]`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 103.1
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
| `encoding_rs.gb18030_2022` | `encodingrs.Gb180302022` | `encoding_rs/src/gb18030_2022` |
| `encoding_rs.macros` | `encodingrs.Macros` | `encoding_rs/src/macros` |

