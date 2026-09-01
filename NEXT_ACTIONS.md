# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/22 (100.0%)
- **Function parity:** 543/545 matched (target 717) — 99.6%
- **Class/type parity:** 69/69 matched (target 130) — 100.0%
- **Combined symbol parity:** 612/614 matched (target 847) — 99.7%
- **Average inline-code cosine:** 0.61 (function body across 20 matched files)
- **Average documentation cosine:** 0.12 (doc text across 20 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `encodingrs.Results [PROVENANCE-FALLBACK]`
- **Similarity:** 0.63
- **Dependents:** 0
- **Priority Score:** 22603.7
- **Functions:** 114/115 matched (target 131)
- **Missing functions:** `name`
- **Types:** 11/11 matched (target 18)
- **Missing types:** _none_
- **Tests:** 46/46 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests lib.rs`)
- **Lint issues:** 3

### 2. handles

- **Target:** `encodingrs.Handles [PROVENANCE-FALLBACK]`
- **Similarity:** 0.74
- **Dependents:** 0
- **Priority Score:** 17302.6
- **Functions:** 44/45 matched (target 137)
- **Missing functions:** `len`
- **Types:** 28/28 matched (target 37)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handles.rs` vs expected `handles.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handles.rs` vs expected `handles.rs`
- **Proposed provenance header:** `// port-lint: source handles.rs` (current: `// port-lint: source handles.rs`)
- **Proposed provenance header:** `// port-lint: tests handles.rs` (current: `// port-lint: tests handles.rs`)
- **Lint issues:** 2

### 3. mem

- **Target:** `encodingrs.Mem [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 8203.9
- **Functions:** 81/81 matched (target 83)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Tests:** 35/35 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `mem.rs` vs expected `mem.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:mem.rs` vs expected `mem.rs`
- **Proposed provenance header:** `// port-lint: source mem.rs` (current: `// port-lint: source mem.rs`)
- **Proposed provenance header:** `// port-lint: tests mem.rs` (current: `// port-lint: tests mem.rs`)
- **Lint issues:** 2

### 4. data

- **Target:** `encodingrs.Data [PROVENANCE-FALLBACK]`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 4803.1
- **Functions:** 47/47 matched (target 50)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Lint issues:** 5

### 5. utf_8

- **Target:** `encodingrs.Utf8 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 3103.1
- **Functions:** 28/28 matched (target 33)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 5)
- **Missing types:** _none_
- **Tests:** 8/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `utf_8.rs` vs expected `utf_8.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:utf_8.rs` vs expected `utf_8.rs`
- **Proposed provenance header:** `// port-lint: source utf_8.rs` (current: `// port-lint: source utf_8.rs`)
- **Proposed provenance header:** `// port-lint: tests utf_8.rs` (current: `// port-lint: tests utf_8.rs`)
- **Lint issues:** 2

### 6. iso_2022_jp

- **Target:** `encodingrs.Iso2022Jp [PROVENANCE-FALLBACK]`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2603.3
- **Functions:** 22/22 matched (target 25)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `iso_2022_jp.rs` vs expected `iso_2022_jp.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:iso_2022_jp.rs` vs expected `iso_2022_jp.rs`
- **Proposed provenance header:** `// port-lint: source iso_2022_jp.rs` (current: `// port-lint: source iso_2022_jp.rs`)
- **Proposed provenance header:** `// port-lint: tests iso_2022_jp.rs` (current: `// port-lint: tests iso_2022_jp.rs`)
- **Lint issues:** 2

### 7. gb18030

- **Target:** `encodingrs.Gb18030 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 2404.0
- **Functions:** 21/21 matched (target 25)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 8)
- **Missing types:** _none_
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gb18030.rs` vs expected `gb18030.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:gb18030.rs` vs expected `gb18030.rs`
- **Proposed provenance header:** `// port-lint: source gb18030.rs` (current: `// port-lint: source gb18030.rs`)
- **Proposed provenance header:** `// port-lint: tests gb18030.rs` (current: `// port-lint: tests gb18030.rs`)
- **Lint issues:** 2

### 8. single_byte

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

### 9. simd_funcs

- **Target:** `encodingrs.SimdFuncs [PROVENANCE-FALLBACK]`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 2207.3
- **Functions:** 22/22 matched (target 28)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 9/9 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `simd_funcs.rs` vs expected `simd_funcs.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:simd_funcs.rs` vs expected `simd_funcs.rs`
- **Proposed provenance header:** `// port-lint: source simd_funcs.rs` (current: `// port-lint: source simd_funcs.rs`)
- **Proposed provenance header:** `// port-lint: tests simd_funcs.rs` (current: `// port-lint: tests simd_funcs.rs`)
- **Lint issues:** 2

### 10. utf_16

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

### 11. euc_jp

- **Target:** `encodingrs.EucJp [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 2104.0
- **Functions:** 18/18 matched (target 21)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 9)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `euc_jp.rs` vs expected `euc_jp.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:euc_jp.rs` vs expected `euc_jp.rs`
- **Proposed provenance header:** `// port-lint: source euc_jp.rs` (current: `// port-lint: source euc_jp.rs`)
- **Proposed provenance header:** `// port-lint: tests euc_jp.rs` (current: `// port-lint: tests euc_jp.rs`)
- **Lint issues:** 2

### 12. euc_kr

- **Target:** `encodingrs.EucKr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 2003.3
- **Functions:** 18/18 matched (target 21)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `euc_kr.rs` vs expected `euc_kr.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:euc_kr.rs` vs expected `euc_kr.rs`
- **Proposed provenance header:** `// port-lint: source euc_kr.rs` (current: `// port-lint: source euc_kr.rs`)
- **Proposed provenance header:** `// port-lint: tests euc_kr.rs` (current: `// port-lint: tests euc_kr.rs`)
- **Lint issues:** 2

### 13. shift_jis

- **Target:** `encodingrs.ShiftJis [PROVENANCE-FALLBACK]`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 1802.9
- **Functions:** 16/16 matched (target 19)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `shift_jis.rs` vs expected `shift_jis.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:shift_jis.rs` vs expected `shift_jis.rs`
- **Proposed provenance header:** `// port-lint: source shift_jis.rs` (current: `// port-lint: source shift_jis.rs`)
- **Proposed provenance header:** `// port-lint: tests shift_jis.rs` (current: `// port-lint: tests shift_jis.rs`)
- **Lint issues:** 2

### 14. big5

- **Target:** `encodingrs.Big5 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 1703.2
- **Functions:** 15/15 matched (target 18)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `big5.rs` vs expected `big5.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:big5.rs` vs expected `big5.rs`
- **Proposed provenance header:** `// port-lint: source big5.rs` (current: `// port-lint: source big5.rs`)
- **Proposed provenance header:** `// port-lint: tests big5.rs` (current: `// port-lint: tests big5.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `variant.rs` vs expected `variant.rs`
- **Proposed provenance header:** `// port-lint: source variant.rs` (current: `// port-lint: source variant.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `testing.rs` vs expected `testing.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:testing.rs` vs expected `testing.rs`
- **Proposed provenance header:** `// port-lint: source testing.rs` (current: `// port-lint: source testing.rs`)
- **Proposed provenance header:** `// port-lint: tests testing.rs` (current: `// port-lint: tests testing.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `x_user_defined.rs` vs expected `x_user_defined.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:x_user_defined.rs` vs expected `x_user_defined.rs`
- **Proposed provenance header:** `// port-lint: source x_user_defined.rs` (current: `// port-lint: source x_user_defined.rs`)
- **Proposed provenance header:** `// port-lint: tests x_user_defined.rs` (current: `// port-lint: tests x_user_defined.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `replacement.rs` vs expected `replacement.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:replacement.rs` vs expected `replacement.rs`
- **Proposed provenance header:** `// port-lint: source replacement.rs` (current: `// port-lint: source replacement.rs`)
- **Proposed provenance header:** `// port-lint: tests replacement.rs` (current: `// port-lint: tests replacement.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ascii.rs` vs expected `ascii.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:ascii.rs` vs expected `ascii.rs`
- **Proposed provenance header:** `// port-lint: source ascii.rs` (current: `// port-lint: source ascii.rs`)
- **Proposed provenance header:** `// port-lint: tests ascii.rs` (current: `// port-lint: tests ascii.rs`)
- **Lint issues:** 2

### 20. test_labels_names

- **Target:** `encodingrs.TestLabelsNames [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 103.1
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `test_labels_names.rs` vs expected `test_labels_names.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:test_labels_names.rs` vs expected `test_labels_names.rs`
- **Proposed provenance header:** `// port-lint: source test_labels_names.rs` (current: `// port-lint: source test_labels_names.rs`)
- **Proposed provenance header:** `// port-lint: tests test_labels_names.rs` (current: `// port-lint: tests test_labels_names.rs`)
- **Lint issues:** 2

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
| `gb18030_2022` | `encodingrs.Gb180302022` | `src/gb18030_2022` |
| `macros` | `encodingrs.Macros` | `src/macros` |

