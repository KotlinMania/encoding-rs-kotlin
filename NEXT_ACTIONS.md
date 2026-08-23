# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 5/22 (22.7%)
- **Function parity:** 45/563 matched (target 63) — 8.0%
- **Class/type parity:** 4/58 matched (target 10) — 6.9%
- **Combined symbol parity:** 49/621 matched (target 73) — 7.9%
- **Average inline-code cosine:** 0.52 (function body across 5 matched files)
- **Average documentation cosine:** 0.21 (doc text across 5 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. utf_16

- **Target:** `encodingrs.Utf16`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 2204.4
- **Functions:** 21/21 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 12/12 matched

### 2. x_user_defined

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

### 3. replacement

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

### 4. ascii

- **Target:** `encodingrs.Ascii`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 202.7
- **Functions:** 2/2 matched (target 13)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 5. gb18030_2022

- **Target:** `encodingrs.Gb180302022 [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 1)
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

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

