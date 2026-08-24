=== Deep Analysis: tmp/encoding_rs/src (rust) -> src/commonMain/kotlin (kotlin) ===

Scanning source codebase (rust)...
Codebase: tmp/encoding_rs/src (rust)
  Files: 22
  Total imports: 141

Scanning target codebase (kotlin)...
Codebase: src/commonMain/kotlin (kotlin)
  Files: 19
  Total imports: 19

Comparing codebases...
Computing AST similarities...

=== Codebase Comparison Report ===

Source: tmp/encoding_rs/src (22 files)
Target: src/commonMain/kotlin (19 files)
Scoring invariant: FnSim is required function body/parameter similarity. Class/type and symbol parity are reported beside it; whole-file shape is diagnostic only.

Matched:   9 files
Unmatched: 13 source, 1 target

=== Matched Files (by porting priority) ===

Source                        Target                        FnSim     Dependents FunctionParityTypeParity  Priority
 --------------------------------------------------------------------------------------------------------------------------
lib                           encodingrs.Results            0.15      0          26/115        7/11        942608.4  
data                          encodingrs.Data [ZERO]        0.00      0          0/47          0/1         484810.0  
utf_8                         encodingrs.Utf8               0.60      0          24/28         3/3         43104.0   
single_byte                   encodingrs.SingleByte         0.59      0          21/21         2/2         2304.1    
utf_16                        encodingrs.Utf16              0.56      0          21/21         1/1         2204.4    
x_user_defined                encodingrs.XUserDefined       0.61      0          12/12         2/2         1403.9    
replacement                   encodingrs.Replacement        0.71      0          10/10         1/1         1102.9    
ascii                         encodingrs.Ascii              0.73      0          2/2           0/0         202.7     
gb18030_2022                  encodingrs.Gb180302022 [ZERO] 0.00      0          0/0           0/0         10.0      

=== Function and Symbol Details ===

lib -> encodingrs.Results
  similarity: 0.15, priority: 942608.4, dependents: 0
  functions: 26/115 matched (target total: 43, required body score: 0.15)
  missing functions: name, is_potentially_borrowable, decode_with_bom_removal, utf8_valid_up_to, ascii_valid_up_to, iso_2022_jp_ascii_valid_up_to, eq, partial_cmp, cmp, hash, fmt, serialize, expecting, visit_str, deserialize, new, max_utf8_buffer_length_without_replacement, decode_to_utf8, decode_to_str, decode_to_string, decode_to_str_without_replacement, decode_to_string_without_replacement, latin1_byte_compatible_up_to, has_pending_state, max_buffer_length_from_utf8_if_no_unmappables, encode_from_utf8, encode_from_utf8_to_vec, encode_from_utf8_to_vec_without_replacement, max_buffer_length_from_utf16_if_no_unmappables, write_ncr, in_range16, in_range32, in_inclusive_range8, in_inclusive_range16, in_inclusive_range32, in_inclusive_range, checked_add, checked_add_opt, checked_mul, checked_div, checked_next_power_of_two, checked_min, sniff_to_utf16, test_bom_sniffing, test_output_encoding, test_label_resolution, test_decode_valid_windows_1257_to_cow, test_decode_invalid_windows_1257_to_cow, test_decode_ascii_only_windows_1257_to_cow, test_decode_bomful_valid_utf8_as_windows_1257_to_cow, test_decode_bomful_invalid_utf8_as_windows_1257_to_cow, test_decode_bomful_valid_utf8_as_utf_8_to_cow, test_decode_bomful_invalid_utf8_as_utf_8_to_cow, test_decode_bomful_valid_utf8_as_utf_8_to_cow_with_bom_removal, test_decode_bomful_valid_utf8_as_windows_1257_to_cow_with_bom_removal, test_decode_valid_windows_1257_to_cow_with_bom_removal, test_decode_invalid_windows_1257_to_cow_with_bom_removal, test_decode_ascii_only_windows_1257_to_cow_with_bom_removal, test_decode_bomful_valid_utf8_to_cow_without_bom_handling, test_decode_bomful_invalid_utf8_to_cow_without_bom_handling, test_decode_valid_windows_1257_to_cow_without_bom_handling, test_decode_invalid_windows_1257_to_cow_without_bom_handling, test_decode_ascii_only_windows_1257_to_cow_without_bom_handling, test_decode_bomful_valid_utf8_to_cow_without_bom_handling_and_without_replacement, test_decode_bomful_invalid_utf8_to_cow_without_bom_handling_and_without_replacement, test_decode_valid_windows_1257_to_cow_without_bom_handling_and_without_replacement, test_decode_invalid_windows_1257_to_cow_without_bom_handling_and_without_replacement, test_decode_ascii_only_windows_1257_to_cow_without_bom_handling_and_without_replacement, test_encode_ascii_only_windows_1257_to_cow, test_encode_valid_windows_1257_to_cow, test_utf16_space_with_one_bom_byte, test_utf8_space_with_one_bom_byte, test_utf16_space_with_two_bom_bytes, test_utf8_space_with_two_bom_bytes, test_utf16_space_with_one_bom_byte_and_a_second_byte_in_same_call, test_too_short_buffer_with_iso_2022_jp_ascii_from_utf8, test_too_short_buffer_with_iso_2022_jp_roman_from_utf8, test_buffer_end_iso_2022_jp_from_utf8, test_too_short_buffer_with_iso_2022_jp_ascii_from_utf16, test_too_short_buffer_with_iso_2022_jp_roman_from_utf16, test_buffer_end_iso_2022_jp_from_utf16, test_buffer_end_utf16be, test_hash, test_iso_2022_jp_ncr_extra_from_utf16, test_iso_2022_jp_ncr_extra_from_utf8, test_max_length_with_bom_to_utf8, test_serde, test_is_single_byte, test_latin1_byte_compatible_up_to
  types: 7/11 matched (target total: 29)
  missing types: EncodingVisitor, Value, DecoderLifeCycle, Demo
  tests: 0/46 matched

data -> encodingrs.Data [ZERO]
  similarity: 0.00, priority: 484810.0, dependents: 0
  functions: 0/47 matched (target total: 0, required body score: 0.00)
  missing functions: map_with_ranges, map_with_unsorted_ranges, position, gb18030_range_decode, gb18030_range_encode, gbk_top_ideograph_decode, gbk_top_ideograph_encode, gbk_left_ideograph_decode, gbk_left_ideograph_encode, cp949_top_hangul_decode, cp949_left_hangul_decode, cp949_top_hangul_encode, cp949_left_hangul_encode, cp949_hangul_encode, ksx1001_unified_hangul_encode, ksx1001_compatibility_hangul_encode, gbk_other_decode, gbk_other_encode, gb2312_other_decode, gb2312_other_encode, gbk_hanzi_encode, gb2312_level1_hanzi_encode, gb2312_level2_hanzi_encode, ksx1001_other_decode, ksx1001_other_encode, jis0208_kanji_shift_jis_encode, shift_jis_to_euc_jp, jis0208_kanji_euc_jp_encode, shift_jis_to_iso_2022_jp, jis0208_kanji_iso_2022_jp_encode, jis0208_level1_kanji_shift_jis_encode, jis0208_level1_kanji_euc_jp_encode, jis0208_level1_kanji_iso_2022_jp_encode, jis0208_level2_and_additional_kanji_encode, jis0208_symbol_decode, jis0208_symbol_encode, ibm_symbol_encode, jis0208_range_decode, jis0208_range_encode, jis0212_accented_decode, big5_is_astral, big5_low_bits, big5_astral_encode, big5_level1_hanzi_encode, big5_box_encode, big5_other_encode, mul_94
  types: 0/1 matched (target total: 1)
  missing types: SingleByteData
  *** CHEAT DETECTION / SCORING FAILURE ***
  function-by-function score forced to 0: no target functions found; report scoring is function-by-function only

utf_8 -> encodingrs.Utf8
  similarity: 0.60, priority: 43104.0, dependents: 0
  functions: 24/28 matched (target total: 28, required body score: 0.60)
  missing functions: new_inner, extra_from_state, convert_utf16_to_utf8_partial_inner, convert_utf16_to_utf8_partial_tail
  types: 3/3 matched (target total: 5)
  missing types: none
  tests: 8/8 matched

single_byte -> encodingrs.SingleByte
  similarity: 0.59, priority: 2304.1, dependents: 0
  functions: 21/21 matched (target total: 25, required body score: 0.59)
  missing functions: none
  types: 2/2 matched (target total: 3)
  missing types: none
  tests: 8/8 matched

utf_16 -> encodingrs.Utf16
  similarity: 0.56, priority: 2204.4, dependents: 0
  functions: 21/21 matched (target total: 23, required body score: 0.56)
  missing functions: none
  types: 1/1 matched (target total: 2)
  missing types: none
  tests: 12/12 matched

x_user_defined -> encodingrs.XUserDefined
  similarity: 0.61, priority: 1403.9, dependents: 0
  functions: 12/12 matched (target total: 15, required body score: 0.61)
  missing functions: none
  types: 2/2 matched (target total: 3)
  missing types: none
  tests: 3/3 matched

replacement -> encodingrs.Replacement
  similarity: 0.71, priority: 1102.9, dependents: 0
  functions: 10/10 matched (target total: 11, required body score: 0.71)
  missing functions: none
  types: 1/1 matched (target total: 2)
  missing types: none
  tests: 2/2 matched

ascii -> encodingrs.Ascii
  similarity: 0.73, priority: 202.7, dependents: 0
  functions: 2/2 matched (target total: 13, required body score: 0.73)
  missing functions: none
  types: 0/0 matched (target total: 2)
  missing types: none

gb18030_2022 -> encodingrs.Gb180302022 [ZERO]
  similarity: 0.00, priority: 10.0, dependents: 0
  functions: 0/0 matched (target total: 4, required body score: 0.00)
  missing functions: none
  types: 0/0 matched (target total: 1)
  missing types: none
  *** CHEAT DETECTION / SCORING FAILURE ***
  function-by-function score forced to 0: no source functions found; target defines functions; report scoring is function-by-function only


=== Scores Forced To 0 ===

  - data -> encodingrs.Data: no target functions found; report scoring is function-by-function only
  - gb18030_2022 -> encodingrs.Gb180302022: no source functions found; target defines functions; report scoring is function-by-function only

=== Missing from Target (need to port) ===

File                          Deps    Path
------------------------------------------------------------------------------
big5                          0       big5.rs
euc_jp                        0       euc_jp.rs
euc_kr                        0       euc_kr.rs
gb18030                       0       gb18030.rs
handles                       0       handles.rs
iso_2022_jp                   0       iso_2022_jp.rs
macros                        0       macros.rs
mem                           0       mem.rs
shift_jis                     0       shift_jis.rs
simd_funcs                    0       simd_funcs.rs
test_labels_names             0       test_labels_names.rs
testing                       0       testing.rs
variant                       0       variant.rs

=== Porting Quality Summary ===

Matched by exact header:          9 / 9
Matched by provenance fallback:   0 / 9
Matched by name:                  0 / 9
Total TODOs in target: 0
Total lint errors:    0
Stub files:           0

=== Big Picture ===

- Missing files: 13
- Incomplete ports (similarity < 60%): 5
- Stub files: 0
- Files missing functions: 3 (total deficit: 140 functions)
- Type definitions missing: 5
- Files missing tests: 1 (total deficit: 46 unported `#[test]` functions)
- Documentation coverage: 101 / 5492 lines (2%)

Primary focus: create missing files (highest deps first)

=== Files with Issues ===

File                          Similarity LineRatio  FunctionParityTests     TODOs Lint  Status
----------------------------------------------------------------------------------------------------
encodingrs.Results            0.15       0.00       26/115        0/46      0     0     LOW_SIM
  missing functions: `name`, `is_potentially_borrowable`, `decode_with_bom_removal`, `utf8_valid_up_to`, `ascii_valid_up_to`, `iso_2022_jp_ascii_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `max_utf8_buffer_length_without_replacement`, `decode_to_utf8`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `latin1_byte_compatible_up_to`, `has_pending_state`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`, `sniff_to_utf16`, `test_bom_sniffing`, `test_output_encoding`, `test_label_resolution`, `test_decode_valid_windows_1257_to_cow`, `test_decode_invalid_windows_1257_to_cow`, `test_decode_ascii_only_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_invalid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow`, `test_decode_bomful_invalid_utf8_as_utf_8_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow_with_bom_removal`, `test_decode_valid_windows_1257_to_cow_with_bom_removal`, `test_decode_invalid_windows_1257_to_cow_with_bom_removal`, `test_decode_ascii_only_windows_1257_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling`, `test_decode_valid_windows_1257_to_cow_without_bom_handling`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_valid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_encode_ascii_only_windows_1257_to_cow`, `test_encode_valid_windows_1257_to_cow`, `test_utf16_space_with_one_bom_byte`, `test_utf8_space_with_one_bom_byte`, `test_utf16_space_with_two_bom_bytes`, `test_utf8_space_with_two_bom_bytes`, `test_utf16_space_with_one_bom_byte_and_a_second_byte_in_same_call`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf8`, `test_buffer_end_iso_2022_jp_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf16`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf16`, `test_buffer_end_iso_2022_jp_from_utf16`, `test_buffer_end_utf16be`, `test_hash`, `test_iso_2022_jp_ncr_extra_from_utf16`, `test_iso_2022_jp_ncr_extra_from_utf8`, `test_max_length_with_bom_to_utf8`, `test_serde`, `test_is_single_byte`, `test_latin1_byte_compatible_up_to`
  missing types: `EncodingVisitor`, `Value`, `DecoderLifeCycle`, `Demo`
encodingrs.Data [ZERO]        0.00       0.00       0/47          -         0     0     LOW_SIM
  missing functions: `map_with_ranges`, `map_with_unsorted_ranges`, `position`, `gb18030_range_decode`, `gb18030_range_encode`, `gbk_top_ideograph_decode`, `gbk_top_ideograph_encode`, `gbk_left_ideograph_decode`, `gbk_left_ideograph_encode`, `cp949_top_hangul_decode`, `cp949_left_hangul_decode`, `cp949_top_hangul_encode`, `cp949_left_hangul_encode`, `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_other_decode`, `gbk_other_encode`, `gb2312_other_decode`, `gb2312_other_encode`, `gbk_hanzi_encode`, `gb2312_level1_hanzi_encode`, `gb2312_level2_hanzi_encode`, `ksx1001_other_decode`, `ksx1001_other_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `jis0208_level1_kanji_shift_jis_encode`, `jis0208_level1_kanji_euc_jp_encode`, `jis0208_level1_kanji_iso_2022_jp_encode`, `jis0208_level2_and_additional_kanji_encode`, `jis0208_symbol_decode`, `jis0208_symbol_encode`, `ibm_symbol_encode`, `jis0208_range_decode`, `jis0208_range_encode`, `jis0212_accented_decode`, `big5_is_astral`, `big5_low_bits`, `big5_astral_encode`, `big5_level1_hanzi_encode`, `big5_box_encode`, `big5_other_encode`, `mul_94`
  missing types: `SingleByteData`
encodingrs.Utf8               0.60       0.00       24/28         8/8       0     0     MISSING_FUNCS
  missing functions: `new_inner`, `extra_from_state`, `convert_utf16_to_utf8_partial_inner`, `convert_utf16_to_utf8_partial_tail`
encodingrs.SingleByte         0.59       0.00       21/21         8/8       0     0     
encodingrs.Utf16              0.56       0.00       21/21         12/12     0     0     
encodingrs.Gb180302022 [ZERO  0.00       0.00       -             -         0     0     LOW_SIM

=== Porting Recommendations ===

Incomplete ports (similarity < 60%): 5
Missing files: 13

Incomplete ports to complete:
  lib                            similarity=0.15 function_parity=26/115 dependents=0
    missing functions: `name`, `is_potentially_borrowable`, `decode_with_bom_removal`, `utf8_valid_up_to`, `ascii_valid_up_to`, `iso_2022_jp_ascii_valid_up_to`, `eq`, `partial_cmp`, `cmp`, `hash`, `fmt`, `serialize`, `expecting`, `visit_str`, `deserialize`, `new`, `max_utf8_buffer_length_without_replacement`, `decode_to_utf8`, `decode_to_str`, `decode_to_string`, `decode_to_str_without_replacement`, `decode_to_string_without_replacement`, `latin1_byte_compatible_up_to`, `has_pending_state`, `max_buffer_length_from_utf8_if_no_unmappables`, `encode_from_utf8`, `encode_from_utf8_to_vec`, `encode_from_utf8_to_vec_without_replacement`, `max_buffer_length_from_utf16_if_no_unmappables`, `write_ncr`, `in_range16`, `in_range32`, `in_inclusive_range8`, `in_inclusive_range16`, `in_inclusive_range32`, `in_inclusive_range`, `checked_add`, `checked_add_opt`, `checked_mul`, `checked_div`, `checked_next_power_of_two`, `checked_min`, `sniff_to_utf16`, `test_bom_sniffing`, `test_output_encoding`, `test_label_resolution`, `test_decode_valid_windows_1257_to_cow`, `test_decode_invalid_windows_1257_to_cow`, `test_decode_ascii_only_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_invalid_utf8_as_windows_1257_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow`, `test_decode_bomful_invalid_utf8_as_utf_8_to_cow`, `test_decode_bomful_valid_utf8_as_utf_8_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_as_windows_1257_to_cow_with_bom_removal`, `test_decode_valid_windows_1257_to_cow_with_bom_removal`, `test_decode_invalid_windows_1257_to_cow_with_bom_removal`, `test_decode_ascii_only_windows_1257_to_cow_with_bom_removal`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling`, `test_decode_valid_windows_1257_to_cow_without_bom_handling`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling`, `test_decode_bomful_valid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_bomful_invalid_utf8_to_cow_without_bom_handling_and_without_replacement`, `test_decode_valid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_invalid_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_decode_ascii_only_windows_1257_to_cow_without_bom_handling_and_without_replacement`, `test_encode_ascii_only_windows_1257_to_cow`, `test_encode_valid_windows_1257_to_cow`, `test_utf16_space_with_one_bom_byte`, `test_utf8_space_with_one_bom_byte`, `test_utf16_space_with_two_bom_bytes`, `test_utf8_space_with_two_bom_bytes`, `test_utf16_space_with_one_bom_byte_and_a_second_byte_in_same_call`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf8`, `test_buffer_end_iso_2022_jp_from_utf8`, `test_too_short_buffer_with_iso_2022_jp_ascii_from_utf16`, `test_too_short_buffer_with_iso_2022_jp_roman_from_utf16`, `test_buffer_end_iso_2022_jp_from_utf16`, `test_buffer_end_utf16be`, `test_hash`, `test_iso_2022_jp_ncr_extra_from_utf16`, `test_iso_2022_jp_ncr_extra_from_utf8`, `test_max_length_with_bom_to_utf8`, `test_serde`, `test_is_single_byte`, `test_latin1_byte_compatible_up_to`
    missing types: `EncodingVisitor`, `Value`, `DecoderLifeCycle`, `Demo`
  data                           similarity=0.00 function_parity=0/47 dependents=0
    missing functions: `map_with_ranges`, `map_with_unsorted_ranges`, `position`, `gb18030_range_decode`, `gb18030_range_encode`, `gbk_top_ideograph_decode`, `gbk_top_ideograph_encode`, `gbk_left_ideograph_decode`, `gbk_left_ideograph_encode`, `cp949_top_hangul_decode`, `cp949_left_hangul_decode`, `cp949_top_hangul_encode`, `cp949_left_hangul_encode`, `cp949_hangul_encode`, `ksx1001_unified_hangul_encode`, `ksx1001_compatibility_hangul_encode`, `gbk_other_decode`, `gbk_other_encode`, `gb2312_other_decode`, `gb2312_other_encode`, `gbk_hanzi_encode`, `gb2312_level1_hanzi_encode`, `gb2312_level2_hanzi_encode`, `ksx1001_other_decode`, `ksx1001_other_encode`, `jis0208_kanji_shift_jis_encode`, `shift_jis_to_euc_jp`, `jis0208_kanji_euc_jp_encode`, `shift_jis_to_iso_2022_jp`, `jis0208_kanji_iso_2022_jp_encode`, `jis0208_level1_kanji_shift_jis_encode`, `jis0208_level1_kanji_euc_jp_encode`, `jis0208_level1_kanji_iso_2022_jp_encode`, `jis0208_level2_and_additional_kanji_encode`, `jis0208_symbol_decode`, `jis0208_symbol_encode`, `ibm_symbol_encode`, `jis0208_range_decode`, `jis0208_range_encode`, `jis0212_accented_decode`, `big5_is_astral`, `big5_low_bits`, `big5_astral_encode`, `big5_level1_hanzi_encode`, `big5_box_encode`, `big5_other_encode`, `mul_94`
    missing types: `SingleByteData`
  single_byte                    similarity=0.59 function_parity=21/21 dependents=0
  utf_16                         similarity=0.56 function_parity=21/21 dependents=0
  gb18030_2022                   similarity=0.00 function_parity=- dependents=0

=== Missing Files (by Dependents) ===

Source File                   Expected Target                       Dependents Path
-----------------------------------------------------------------------------------------------------------------------
big5                          Big5                                  0          big5.rs
euc_jp                        EucJp                                 0          euc_jp.rs
euc_kr                        EucKr                                 0          euc_kr.rs
gb18030                       Gb18030                               0          gb18030.rs
handles                       Handles                               0          handles.rs
iso_2022_jp                   Iso2022Jp                             0          iso_2022_jp.rs
macros                        Macros                                0          macros.rs
mem                           Mem                                   0          mem.rs
shift_jis                     ShiftJis                              0          shift_jis.rs
simd_funcs                    SimdFuncs                             0          simd_funcs.rs
test_labels_names             TestLabelsNames                       0          test_labels_names.rs
testing                       Testing                               0          testing.rs
variant                       Variant                               0          variant.rs

=== Documentation Gaps ===

There is missing documentation that is hurting overall scoring.
Documentation coverage: 101 / 5492 lines (2%)
Files with >20% doc gap: 2

File                          Src Docs    Tgt Docs    Gap %     DocSim    DocAmt    DocEq     
----------------------------------------------------------------------------------------------
lib                           5314        45          99%       0.20      0.01      0.11      
ascii                         172         43          75%       0.28      0.25      0.26      

=== Generating Reports ===

✅ Generated: port_status_report.md
✅ Generated: high_priority_ports.md
✅ Generated: NEXT_ACTIONS.md
✅ Generated: port_lint_proposed_changes.md

📁 All reports generated successfully!
