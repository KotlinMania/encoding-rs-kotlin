# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 0/22 (0.0%)
- **Function parity:** 0/565 matched — 0.0%
- **Class/type parity:** 0/58 matched — 0.0%
- **Combined symbol parity:** 0/623 matched — 0.0%
- **Average inline-code cosine:** 0.00 (function body across 0 matched files)
- **Average documentation cosine:** 0.00 (doc text across 0 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 0 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

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
| `encoding_rs.gb18030_2022` | `encodingrs.src.Gb180302022` | 0 | `encoding_rs/src/gb18030_2022.rs` | `encodingrs/src/Gb180302022.kt` |
| `encoding_rs.lib` | `encodingrs.src.Lib` | 0 | `encoding_rs/src/lib.rs` | `encodingrs/src/Lib.kt` |
| `encoding_rs.macros` | `encodingrs.src.Macros` | 0 | `encoding_rs/src/macros.rs` | `encodingrs/src/Macros.kt` |

