// port-lint: source lib.rs
package io.github.kotlinmania.encodingrs

/**
 * An encoding as defined in the WHATWG Encoding Standard.
 */
public class Encoding internal constructor(
    public val name: String,
    internal val variant: VariantEncoding,
) {
    public fun isSingleByte(): Boolean =
        variant is VariantEncoding.SingleByte || variant is VariantEncoding.UserDefined

    public fun isAsciiCompatible(): Boolean =
        this !== REPLACEMENT && this !== UTF_16BE && this !== UTF_16LE && this !== ISO_2022_JP

    public fun canEncodeEverything(): Boolean = outputEncoding() === UTF_8

    public fun outputEncoding(): Encoding =
        if (this === REPLACEMENT || this === UTF_16BE || this === UTF_16LE) {
            UTF_8
        } else {
            this
        }

    public fun newDecoder(): Decoder =
        Decoder(this, variant.newVariantDecoder(), BomHandling.Sniff)

    public fun newDecoderWithBomRemoval(): Decoder =
        Decoder(this, variant.newVariantDecoder(), BomHandling.Remove)

    public fun newDecoderWithoutBomHandling(): Decoder =
        Decoder(this, variant.newVariantDecoder(), BomHandling.Off)

    public fun newEncoder(): Encoder =
        variant.newVariantEncoder(this)

    public fun decode(bytes: ByteArray): Triple<String, Encoding, Boolean> {
        val bomMatch = forBom(bytes)
        val (actualEncoding, withoutBom) =
            if (bomMatch != null) {
                Pair(bomMatch.first, bytes.copyOfRange(bomMatch.second, bytes.size))
            } else {
                Pair(this, bytes)
            }
        val (str, hadErrors) = actualEncoding.decodeWithoutBomHandling(withoutBom)
        return Triple(str, actualEncoding, hadErrors)
    }

    public fun decodeWithoutBomHandling(bytes: ByteArray): Pair<String, Boolean> {
        if (this === UTF_8) {
            val validUpTo =
                io.github.kotlinmania.encodingrs.Utf8
                    .utf8ValidUpTo(bytes)
            if (validUpTo == bytes.size) {
                return Pair(bytes.decodeToString(), false)
            }
        }
        val decoder = newDecoderWithoutBomHandling()
        val maxLen = decoder.maxUtf16BufferLength(bytes.size) ?: bytes.size
        val dst = CharArray(maxLen)
        val (result, read, written) = decoder.decodeToUtf16Raw(bytes, dst, true)
        val hadErrors = result is DecoderResult.Malformed
        val str =
            if (result is DecoderResult.Malformed) {
                val sb = StringBuilder()
                var srcPos = 0
                var d = newDecoderWithoutBomHandling()
                val tempDst = CharArray(128)
                while (srcPos < bytes.size) {
                    val (r, inRead, outWritten) = d.decodeToUtf16Raw(bytes.copyOfRange(srcPos, bytes.size), tempDst, true)
                    for (i in 0 until outWritten) {
                        sb.append(tempDst[i])
                    }
                    srcPos += inRead
                    if (r is DecoderResult.Malformed) {
                        sb.append('\uFFFD')
                    }
                }
                sb.toString()
            } else {
                dst.concatToString(0, written)
            }
        return Pair(str, hadErrors)
    }

    public fun decodeWithoutBomHandlingAndWithoutReplacement(bytes: ByteArray): String? {
        val decoder = newDecoderWithoutBomHandling()
        val maxLen = decoder.maxUtf16BufferLength(bytes.size) ?: bytes.size
        val dst = CharArray(maxLen)
        val (result, _, written) = decoder.decodeToUtf16Raw(bytes, dst, true)
        return if (result is DecoderResult.InputEmpty) {
            dst.concatToString(0, written)
        } else {
            null
        }
    }

    public fun encode(string: String): Triple<ByteArray, Encoding, Boolean> {
        val outputEnc = outputEncoding()
        if (outputEnc === UTF_8) {
            return Triple(string.encodeToByteArray(), outputEnc, false)
        }
        val encoder = outputEnc.newEncoder()
        val chars = string.toCharArray()
        val maxLen = encoder.maxBufferLengthFromUtf16WithoutReplacement(chars.size) ?: chars.size
        val dst = ByteArray(maxLen)
        val (result, read, written) = encoder.encodeFromUtf16Raw(chars, dst, true)
        return if (result is EncoderResult.InputEmpty) {
            Triple(dst.copyOf(written), outputEnc, false)
        } else {
            val sb = mutableListOf<Byte>()
            var srcPos = 0
            var enc = outputEnc.newEncoder()
            val tempDst = ByteArray(128)
            while (srcPos < chars.size) {
                val (r, inRead, outWritten) = enc.encodeFromUtf16Raw(chars.copyOfRange(srcPos, chars.size), tempDst, true)
                for (i in 0 until outWritten) {
                    sb.add(tempDst[i])
                }
                srcPos += inRead
                if (r is EncoderResult.Unmappable) {
                    val codePoint = r.codePoint
                    val ref = "&#$codePoint;".encodeToByteArray()
                    for (b in ref) {
                        sb.add(b)
                    }
                }
            }
            Triple(sb.toByteArray(), outputEnc, true)
        }
    }

    public fun encodeFromUtf16(chars: CharArray): Triple<ByteArray, Encoding, Boolean> {
        val outputEnc = outputEncoding()
        val encoder = outputEnc.newEncoder()
        val maxLen = (encoder.maxBufferLengthFromUtf16WithoutReplacement(chars.size) ?: chars.size) + 64
        val dst = ByteArray(maxLen)
        val res = encoder.encodeFromUtf16(chars, dst, true)
        return Triple(dst.copyOf(res.written), outputEnc, res.hadErrors)
    }

    override fun toString(): String = name

    public companion object {
        private const val LONGEST_LABEL_LENGTH: Int = 21

        public val BIG5: Encoding = Encoding("Big5", VariantEncoding.Big5)
        public val EUC_JP: Encoding = Encoding("EUC-JP", VariantEncoding.EucJp)
        public val EUC_KR: Encoding = Encoding("EUC-KR", VariantEncoding.EucKr)
        public val GBK: Encoding = Encoding("GBK", VariantEncoding.Gbk)
        public val IBM866: Encoding = Encoding("IBM866", VariantEncoding.SingleByte(Data.ibm866, 0x0440, 96, 16))
        public val ISO_2022_JP: Encoding = Encoding("ISO-2022-JP", VariantEncoding.Iso2022Jp)
        public val ISO_8859_10: Encoding = Encoding("ISO-8859-10", VariantEncoding.SingleByte(Data.iso885910, 0x00DA, 90, 6))
        public val ISO_8859_13: Encoding = Encoding("ISO-8859-13", VariantEncoding.SingleByte(Data.iso885913, 0x00DF, 95, 1))
        public val ISO_8859_14: Encoding = Encoding("ISO-8859-14", VariantEncoding.SingleByte(Data.iso885914, 0x00DF, 95, 17))
        public val ISO_8859_15: Encoding = Encoding("ISO-8859-15", VariantEncoding.SingleByte(Data.iso885915, 0x00BF, 63, 65))
        public val ISO_8859_16: Encoding = Encoding("ISO-8859-16", VariantEncoding.SingleByte(Data.iso885916, 0x00DF, 95, 4))
        public val ISO_8859_2: Encoding = Encoding("ISO-8859-2", VariantEncoding.SingleByte(Data.iso88592, 0x00DF, 95, 1))
        public val ISO_8859_3: Encoding = Encoding("ISO-8859-3", VariantEncoding.SingleByte(Data.iso88593, 0x00DF, 95, 4))
        public val ISO_8859_4: Encoding = Encoding("ISO-8859-4", VariantEncoding.SingleByte(Data.iso88594, 0x00DF, 95, 1))
        public val ISO_8859_5: Encoding = Encoding("ISO-8859-5", VariantEncoding.SingleByte(Data.iso88595, 0x040E, 46, 66))
        public val ISO_8859_6: Encoding = Encoding("ISO-8859-6", VariantEncoding.SingleByte(Data.iso88596, 0x0621, 65, 26))
        public val ISO_8859_7: Encoding = Encoding("ISO-8859-7", VariantEncoding.SingleByte(Data.iso88597, 0x03A3, 83, 44))
        public val ISO_8859_8: Encoding = Encoding("ISO-8859-8", VariantEncoding.SingleByte(Data.iso88598, 0x05D0, 96, 27))
        public val ISO_8859_8_I: Encoding = Encoding("ISO-8859-8-I", VariantEncoding.SingleByte(Data.iso88598, 0x05D0, 96, 27))
        public val KOI8_R: Encoding = Encoding("KOI8-R", VariantEncoding.SingleByte(Data.koi8r, 0x044E, 64, 1))
        public val KOI8_U: Encoding = Encoding("KOI8-U", VariantEncoding.SingleByte(Data.koi8u, 0x044E, 64, 1))
        public val MACINTOSH: Encoding = Encoding("macintosh", VariantEncoding.SingleByte(Data.macintosh, 0x00CD, 106, 3))
        public val REPLACEMENT: Encoding = Encoding("replacement", VariantEncoding.Replacement)
        public val SHIFT_JIS: Encoding = Encoding("Shift_JIS", VariantEncoding.ShiftJis)
        public val UTF_16BE: Encoding = Encoding("UTF-16BE", VariantEncoding.Utf16Be)
        public val UTF_16LE: Encoding = Encoding("UTF-16LE", VariantEncoding.Utf16Le)
        public val UTF_8: Encoding = Encoding("UTF-8", VariantEncoding.Utf8)
        public val WINDOWS_1250: Encoding = Encoding("windows-1250", VariantEncoding.SingleByte(Data.windows1250, 0x00DC, 92, 2))
        public val WINDOWS_1251: Encoding = Encoding("windows-1251", VariantEncoding.SingleByte(Data.windows1251, 0x0410, 64, 64))
        public val WINDOWS_1252: Encoding = Encoding("windows-1252", VariantEncoding.SingleByte(Data.windows1252, 0x00A0, 32, 96))
        public val WINDOWS_1253: Encoding = Encoding("windows-1253", VariantEncoding.SingleByte(Data.windows1253, 0x03A3, 83, 44))
        public val WINDOWS_1254: Encoding = Encoding("windows-1254", VariantEncoding.SingleByte(Data.windows1254, 0x00DF, 95, 17))
        public val WINDOWS_1255: Encoding = Encoding("windows-1255", VariantEncoding.SingleByte(Data.windows1255, 0x05D0, 96, 27))
        public val WINDOWS_1256: Encoding = Encoding("windows-1256", VariantEncoding.SingleByte(Data.windows1256, 0x0621, 65, 22))
        public val WINDOWS_1257: Encoding = Encoding("windows-1257", VariantEncoding.SingleByte(Data.windows1257, 0x00DF, 95, 1))
        public val WINDOWS_1258: Encoding = Encoding("windows-1258", VariantEncoding.SingleByte(Data.windows1258, 0x00DF, 95, 4))
        public val WINDOWS_874: Encoding = Encoding("windows-874", VariantEncoding.SingleByte(Data.windows874, 0x0E01, 33, 58))
        public val X_MAC_CYRILLIC: Encoding = Encoding("x-mac-cyrillic", VariantEncoding.SingleByte(Data.xMacCyrillic, 0x0430, 96, 31))
        public val X_USER_DEFINED: Encoding = Encoding("x-user-defined", VariantEncoding.UserDefined)
        public val GB18030: Encoding = Encoding("gb18030", VariantEncoding.Gb18030)

        private val LABELS_SORTED: Array<String> =
            arrayOf(
                "l1",
                "l2",
                "l3",
                "l4",
                "l5",
                "l6",
                "l9",
                "866",
                "mac",
                "koi",
                "gbk",
                "big5",
                "utf8",
                "koi8",
                "sjis",
                "ucs-2",
                "ms932",
                "cp866",
                "utf-8",
                "cp819",
                "ascii",
                "x-gbk",
                "greek",
                "cp1250",
                "cp1251",
                "latin1",
                "gb2312",
                "cp1252",
                "latin2",
                "cp1253",
                "latin3",
                "cp1254",
                "latin4",
                "cp1255",
                "csbig5",
                "latin5",
                "utf-16",
                "cp1256",
                "ibm866",
                "latin6",
                "cp1257",
                "cp1258",
                "greek8",
                "ibm819",
                "arabic",
                "visual",
                "korean",
                "euc-jp",
                "koi8-r",
                "koi8_r",
                "euc-kr",
                "x-sjis",
                "koi8-u",
                "hebrew",
                "tis-620",
                "gb18030",
                "ksc5601",
                "gb_2312",
                "dos-874",
                "cn-big5",
                "unicode",
                "chinese",
                "logical",
                "cskoi8r",
                "cseuckr",
                "koi8-ru",
                "x-cp1250",
                "ksc_5601",
                "x-cp1251",
                "iso88591",
                "csgb2312",
                "x-cp1252",
                "iso88592",
                "x-cp1253",
                "iso88593",
                "ecma-114",
                "x-cp1254",
                "iso88594",
                "x-cp1255",
                "iso88595",
                "x-x-big5",
                "x-cp1256",
                "csibm866",
                "iso88596",
                "x-cp1257",
                "iso88597",
                "asmo-708",
                "ecma-118",
                "elot_928",
                "x-cp1258",
                "iso88598",
                "iso88599",
                "cyrillic",
                "utf-16be",
                "utf-16le",
                "us-ascii",
                "ms_kanji",
                "x-euc-jp",
                "iso885910",
                "iso8859-1",
                "iso885911",
                "iso8859-2",
                "iso8859-3",
                "iso885913",
                "iso8859-4",
                "iso885914",
                "iso8859-5",
                "iso885915",
                "iso8859-6",
                "iso8859-7",
                "iso8859-8",
                "iso-ir-58",
                "iso8859-9",
                "csunicode",
                "macintosh",
                "shift-jis",
                "shift_jis",
                "iso-ir-100",
                "iso8859-10",
                "iso-ir-110",
                "gb_2312-80",
                "iso-8859-1",
                "iso_8859-1",
                "iso-ir-101",
                "iso8859-11",
                "iso-8859-2",
                "iso_8859-2",
                "hz-gb-2312",
                "iso-8859-3",
                "iso_8859-3",
                "iso8859-13",
                "iso-8859-4",
                "iso_8859-4",
                "iso8859-14",
                "iso-ir-144",
                "iso-8859-5",
                "iso_8859-5",
                "iso8859-15",
                "iso-8859-6",
                "iso_8859-6",
                "iso-ir-126",
                "iso-8859-7",
                "iso_8859-7",
                "iso-ir-127",
                "iso-ir-157",
                "iso-8859-8",
                "iso_8859-8",
                "iso-ir-138",
                "iso-ir-148",
                "iso-8859-9",
                "iso_8859-9",
                "iso-ir-109",
                "iso-ir-149",
                "big5-hkscs",
                "csshiftjis",
                "iso-8859-10",
                "iso-8859-11",
                "csisolatin1",
                "csisolatin2",
                "iso-8859-13",
                "csisolatin3",
                "iso-8859-14",
                "windows-874",
                "csisolatin4",
                "iso-8859-15",
                "iso_8859-15",
                "csisolatin5",
                "iso-8859-16",
                "csisolatin6",
                "windows-949",
                "csisolatin9",
                "csiso88596e",
                "csiso88598e",
                "unicodefffe",
                "unicodefeff",
                "csmacintosh",
                "csiso88596i",
                "csiso88598i",
                "windows-31j",
                "x-mac-roman",
                "iso-2022-cn",
                "iso-2022-jp",
                "csiso2022jp",
                "iso-2022-kr",
                "csiso2022kr",
                "replacement",
                "windows-1250",
                "windows-1251",
                "windows-1252",
                "windows-1253",
                "windows-1254",
                "windows-1255",
                "windows-1256",
                "windows-1257",
                "windows-1258",
                "iso-8859-6-e",
                "iso-8859-8-e",
                "iso-8859-6-i",
                "iso-8859-8-i",
                "sun_eu_greek",
                "csksc56011987",
                "unicode20utf8",
                "unicode11utf8",
                "ks_c_5601-1987",
                "ansi_x3.4-1968",
                "ks_c_5601-1989",
                "x-mac-cyrillic",
                "x-user-defined",
                "csiso58gb231280",
                "iso-10646-ucs-2",
                "iso_8859-1:1987",
                "iso_8859-2:1987",
                "iso_8859-6:1987",
                "iso_8859-7:1987",
                "iso_8859-3:1988",
                "iso_8859-4:1988",
                "iso_8859-5:1988",
                "iso_8859-8:1988",
                "x-unicode20utf8",
                "iso_8859-9:1989",
                "csisolatingreek",
                "x-mac-ukrainian",
                "iso-2022-cn-ext",
                "csisolatinarabic",
                "csisolatinhebrew",
                "unicode-1-1-utf-8",
                "csisolatincyrillic",
                "cseucpkdfmtjapanese",
            )

        private val ENCODINGS_IN_LABEL_SORT: Array<Encoding> by lazy {
            arrayOf(
                WINDOWS_1252,
                ISO_8859_2,
                ISO_8859_3,
                ISO_8859_4,
                WINDOWS_1254,
                ISO_8859_10,
                ISO_8859_15,
                IBM866,
                MACINTOSH,
                KOI8_R,
                GBK,
                BIG5,
                UTF_8,
                KOI8_R,
                SHIFT_JIS,
                UTF_16LE,
                SHIFT_JIS,
                IBM866,
                UTF_8,
                WINDOWS_1252,
                WINDOWS_1252,
                GBK,
                ISO_8859_7,
                WINDOWS_1250,
                WINDOWS_1251,
                WINDOWS_1252,
                GBK,
                WINDOWS_1252,
                ISO_8859_2,
                WINDOWS_1253,
                ISO_8859_3,
                WINDOWS_1254,
                ISO_8859_4,
                WINDOWS_1255,
                BIG5,
                WINDOWS_1254,
                UTF_16LE,
                WINDOWS_1256,
                IBM866,
                ISO_8859_10,
                WINDOWS_1257,
                WINDOWS_1258,
                ISO_8859_7,
                WINDOWS_1252,
                ISO_8859_6,
                ISO_8859_8,
                EUC_KR,
                EUC_JP,
                KOI8_R,
                KOI8_R,
                EUC_KR,
                SHIFT_JIS,
                KOI8_U,
                ISO_8859_8,
                WINDOWS_874,
                GB18030,
                EUC_KR,
                GBK,
                WINDOWS_874,
                BIG5,
                UTF_16LE,
                GBK,
                ISO_8859_8_I,
                KOI8_R,
                EUC_KR,
                KOI8_U,
                WINDOWS_1250,
                EUC_KR,
                WINDOWS_1251,
                WINDOWS_1252,
                GBK,
                WINDOWS_1252,
                ISO_8859_2,
                WINDOWS_1253,
                ISO_8859_3,
                ISO_8859_6,
                WINDOWS_1254,
                ISO_8859_4,
                WINDOWS_1255,
                ISO_8859_5,
                BIG5,
                WINDOWS_1256,
                IBM866,
                ISO_8859_6,
                WINDOWS_1257,
                ISO_8859_7,
                ISO_8859_6,
                ISO_8859_7,
                ISO_8859_7,
                WINDOWS_1258,
                ISO_8859_8,
                WINDOWS_1254,
                ISO_8859_5,
                UTF_16BE,
                UTF_16LE,
                WINDOWS_1252,
                SHIFT_JIS,
                EUC_JP,
                ISO_8859_10,
                WINDOWS_1252,
                WINDOWS_874,
                ISO_8859_2,
                ISO_8859_3,
                ISO_8859_13,
                ISO_8859_4,
                ISO_8859_14,
                ISO_8859_5,
                ISO_8859_15,
                ISO_8859_6,
                ISO_8859_7,
                ISO_8859_8,
                GBK,
                WINDOWS_1254,
                UTF_16LE,
                MACINTOSH,
                SHIFT_JIS,
                SHIFT_JIS,
                WINDOWS_1252,
                ISO_8859_10,
                ISO_8859_4,
                GBK,
                WINDOWS_1252,
                WINDOWS_1252,
                ISO_8859_2,
                WINDOWS_874,
                ISO_8859_2,
                ISO_8859_2,
                REPLACEMENT,
                ISO_8859_3,
                ISO_8859_3,
                ISO_8859_13,
                ISO_8859_4,
                ISO_8859_4,
                ISO_8859_14,
                ISO_8859_5,
                ISO_8859_5,
                ISO_8859_5,
                ISO_8859_15,
                ISO_8859_6,
                ISO_8859_6,
                ISO_8859_7,
                ISO_8859_7,
                ISO_8859_7,
                ISO_8859_6,
                ISO_8859_10,
                ISO_8859_8,
                ISO_8859_8,
                ISO_8859_8,
                WINDOWS_1254,
                WINDOWS_1254,
                WINDOWS_1254,
                ISO_8859_3,
                EUC_KR,
                BIG5,
                SHIFT_JIS,
                ISO_8859_10,
                WINDOWS_874,
                WINDOWS_1252,
                ISO_8859_2,
                ISO_8859_13,
                ISO_8859_3,
                ISO_8859_14,
                WINDOWS_874,
                ISO_8859_4,
                ISO_8859_15,
                ISO_8859_15,
                WINDOWS_1254,
                ISO_8859_16,
                ISO_8859_10,
                EUC_KR,
                ISO_8859_15,
                ISO_8859_6,
                ISO_8859_8,
                UTF_16BE,
                UTF_16LE,
                MACINTOSH,
                ISO_8859_6,
                ISO_8859_8_I,
                SHIFT_JIS,
                MACINTOSH,
                REPLACEMENT,
                ISO_2022_JP,
                ISO_2022_JP,
                REPLACEMENT,
                REPLACEMENT,
                REPLACEMENT,
                WINDOWS_1250,
                WINDOWS_1251,
                WINDOWS_1252,
                WINDOWS_1253,
                WINDOWS_1254,
                WINDOWS_1255,
                WINDOWS_1256,
                WINDOWS_1257,
                WINDOWS_1258,
                ISO_8859_6,
                ISO_8859_8,
                ISO_8859_6,
                ISO_8859_8_I,
                ISO_8859_7,
                EUC_KR,
                UTF_8,
                UTF_8,
                EUC_KR,
                WINDOWS_1252,
                EUC_KR,
                X_MAC_CYRILLIC,
                X_USER_DEFINED,
                GBK,
                UTF_16LE,
                WINDOWS_1252,
                ISO_8859_2,
                ISO_8859_6,
                ISO_8859_7,
                ISO_8859_3,
                ISO_8859_4,
                ISO_8859_5,
                ISO_8859_8,
                UTF_8,
                WINDOWS_1254,
                ISO_8859_7,
                X_MAC_CYRILLIC,
                REPLACEMENT,
                ISO_8859_6,
                ISO_8859_8,
                UTF_8,
                ISO_8859_5,
                EUC_JP,
            )
        }

        private fun isWhitespace(b: Int): Boolean =
            b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D || b == 0x20

        public fun forLabel(label: ByteArray): Encoding? {
            var i = 0
            while (i < label.size && isWhitespace(label[i].toInt() and 0xFF)) {
                i++
            }
            if (i >= label.size) {
                return null
            }
            val start = i
            val sb = StringBuilder()
            while (i < label.size) {
                val b = label[i].toInt() and 0xFF
                if (isWhitespace(b)) {
                    break
                }
                if (sb.length == LONGEST_LABEL_LENGTH) {
                    return null
                }
                when (b.toChar()) {
                    in 'A'..'Z' -> sb.append((b + 0x20).toChar())
                    in 'a'..'z', in '0'..'9', '-', '_', ':', '.' -> sb.append(b.toChar())
                    else -> return null
                }
                i++
            }
            while (i < label.size) {
                val b = label[i].toInt() and 0xFF
                if (!isWhitespace(b)) {
                    return null
                }
                i++
            }
            val candidate = sb.toString()

            var low = 0
            var high = LABELS_SORTED.size - 1
            while (low <= high) {
                val mid = (low + high) ushr 1
                val probe = LABELS_SORTED[mid]
                val cmp = compareLabel(probe, candidate)
                if (cmp < 0) {
                    low = mid + 1
                } else if (cmp > 0) {
                    high = mid - 1
                } else {
                    return ENCODINGS_IN_LABEL_SORT[mid]
                }
            }
            return null
        }

        private fun compareLabel(probe: String, candidate: String): Int {
            if (probe.length != candidate.length) {
                return probe.length.compareTo(candidate.length)
            }
            for (i in probe.length - 1 downTo 0) {
                val cmp = probe[i].compareTo(candidate[i])
                if (cmp != 0) return cmp
            }
            return 0
        }

        public fun forLabel(label: String): Encoding? = forLabel(label.encodeToByteArray())

        public fun forLabelNoReplacement(label: ByteArray): Encoding? {
            val enc = forLabel(label)
            return if (enc === REPLACEMENT) null else enc
        }

        public fun forLabelNoReplacement(label: String): Encoding? =
            forLabelNoReplacement(label.encodeToByteArray())

        public fun forBom(buffer: ByteArray): Pair<Encoding, Int>? {
            if (buffer.size >= 3 &&
                (buffer[0].toInt() and 0xFF) == 0xEF &&
                (buffer[1].toInt() and 0xFF) == 0xBB &&
                (buffer[2].toInt() and 0xFF) == 0xBF
            ) {
                return Pair(UTF_8, 3)
            } else if (buffer.size >= 2 &&
                (buffer[0].toInt() and 0xFF) == 0xFF &&
                (buffer[1].toInt() and 0xFF) == 0xFE
            ) {
                return Pair(UTF_16LE, 2)
            } else if (buffer.size >= 2 &&
                (buffer[0].toInt() and 0xFF) == 0xFE &&
                (buffer[1].toInt() and 0xFF) == 0xFF
            ) {
                return Pair(UTF_16BE, 2)
            }
            return null
        }
    }
}

internal enum class BomHandling {
    Off,
    Sniff,
    Remove,
}

internal sealed class VariantEncoding {
    data class SingleByte(
        val table: CharArray,
        val runBmpOffset: Int,
        val runByteOffset: Int,
        val runLength: Int,
    ) : VariantEncoding()

    data object Utf8 : VariantEncoding()

    data object Gb18030 : VariantEncoding()

    data object Gbk : VariantEncoding()

    data object Big5 : VariantEncoding()

    data object EucJp : VariantEncoding()

    data object Iso2022Jp : VariantEncoding()

    data object ShiftJis : VariantEncoding()

    data object EucKr : VariantEncoding()

    data object UserDefined : VariantEncoding()

    data object Replacement : VariantEncoding()

    data object Utf16Be : VariantEncoding()

    data object Utf16Le : VariantEncoding()

    fun newVariantDecoder(): VariantDecoder =
        when (this) {
            is SingleByte -> VariantDecoder.SingleByte(SingleByteDecoder.new(table))
            Utf8 -> VariantDecoder.Utf8(Utf8Decoder.new())
            Gb18030, Gbk -> VariantDecoder.Gb18030(Gb18030Decoder.new())
            Big5 -> VariantDecoder.Big5(Big5Decoder.new())
            EucJp -> VariantDecoder.EucJp(EucJpDecoder.new())
            Iso2022Jp -> VariantDecoder.Iso2022Jp(Iso2022JpDecoder.new())
            ShiftJis -> VariantDecoder.ShiftJis(ShiftJisDecoder.new())
            EucKr -> VariantDecoder.EucKr(EucKrDecoder.new())
            UserDefined -> VariantDecoder.UserDefined(UserDefinedDecoder.new())
            Replacement -> VariantDecoder.Replacement(ReplacementDecoder.new())
            Utf16Be -> VariantDecoder.Utf16Be(Utf16Decoder.new(false))
            Utf16Le -> VariantDecoder.Utf16Le(Utf16Decoder.new(true))
        }

    fun newVariantEncoder(encoding: Encoding): Encoder =
        when (this) {
            is SingleByte -> Encoder(encoding, VariantEncoder.SingleByte(SingleByteEncoder.new(table, runBmpOffset, runByteOffset, runLength)))
            Utf8 -> Encoder(encoding, VariantEncoder.Utf8)
            Gb18030 -> Encoder(encoding, VariantEncoder.Gb18030(Gb18030Encoder(extended = true)))
            Gbk -> Encoder(encoding, VariantEncoder.Gbk(Gb18030Encoder(extended = false)))
            Big5 -> Encoder(encoding, VariantEncoder.Big5(Big5Encoder()))
            EucJp -> Encoder(encoding, VariantEncoder.EucJp(EucJpEncoder()))
            Iso2022Jp -> Encoder(encoding, VariantEncoder.Iso2022Jp(Iso2022JpEncoder()))
            ShiftJis -> Encoder(encoding, VariantEncoder.ShiftJis(ShiftJisEncoder()))
            EucKr -> Encoder(encoding, VariantEncoder.EucKr(EucKrEncoder()))
            UserDefined -> Encoder(encoding, VariantEncoder.UserDefined(UserDefinedEncoder()))
            Replacement -> Encoder(encoding, VariantEncoder.Replacement)
            Utf16Be -> Encoder(encoding, VariantEncoder.Utf16Be)
            Utf16Le -> Encoder(encoding, VariantEncoder.Utf16Le)
        }
}

internal sealed class VariantDecoder {
    data class SingleByte(
        val decoder: SingleByteDecoder,
    ) : VariantDecoder()

    data class Utf8(
        val decoder: Utf8Decoder,
    ) : VariantDecoder()

    data class Gb18030(
        val decoder: Gb18030Decoder,
    ) : VariantDecoder()

    data class Big5(
        val decoder: Big5Decoder,
    ) : VariantDecoder()

    data class EucJp(
        val decoder: EucJpDecoder,
    ) : VariantDecoder()

    data class Iso2022Jp(
        val decoder: Iso2022JpDecoder,
    ) : VariantDecoder()

    data class ShiftJis(
        val decoder: ShiftJisDecoder,
    ) : VariantDecoder()

    data class EucKr(
        val decoder: EucKrDecoder,
    ) : VariantDecoder()

    data class UserDefined(
        val decoder: UserDefinedDecoder,
    ) : VariantDecoder()

    data class Replacement(
        val decoder: ReplacementDecoder,
    ) : VariantDecoder()

    data class Utf16Be(
        val decoder: Utf16Decoder,
    ) : VariantDecoder()

    data class Utf16Le(
        val decoder: Utf16Decoder,
    ) : VariantDecoder()

    fun maxUtf16BufferLength(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> decoder.maxUtf16BufferLength(byteLength)
            is Utf8 -> decoder.maxUtf16BufferLength(byteLength)
            is Gb18030 -> decoder.maxUtf16BufferLength(byteLength)
            is Big5 -> decoder.maxUtf16BufferLength(byteLength)
            is EucJp -> decoder.maxUtf16BufferLength(byteLength)
            is Iso2022Jp -> decoder.maxUtf16BufferLength(byteLength)
            is ShiftJis -> decoder.maxUtf16BufferLength(byteLength)
            is EucKr -> decoder.maxUtf16BufferLength(byteLength)
            is UserDefined -> decoder.maxUtf16BufferLength(byteLength)
            is Replacement -> decoder.maxUtf16BufferLength(byteLength)
            is Utf16Be -> decoder.maxUtf16BufferLength(byteLength)
            is Utf16Le -> decoder.maxUtf16BufferLength(byteLength)
        }

    fun maxUtf8BufferLength(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> decoder.maxUtf8BufferLength(byteLength)
            is Utf8 -> decoder.maxUtf8BufferLength(byteLength)
            is Gb18030 -> decoder.maxUtf8BufferLength(byteLength)
            is Big5 -> decoder.maxUtf8BufferLength(byteLength)
            is EucJp -> decoder.maxUtf8BufferLength(byteLength)
            is Iso2022Jp -> decoder.maxUtf8BufferLength(byteLength)
            is ShiftJis -> decoder.maxUtf8BufferLength(byteLength)
            is EucKr -> decoder.maxUtf8BufferLength(byteLength)
            is UserDefined -> decoder.maxUtf8BufferLength(byteLength)
            is Replacement -> decoder.maxUtf8BufferLength(byteLength)
            is Utf16Be -> decoder.maxUtf8BufferLength(byteLength)
            is Utf16Le -> decoder.maxUtf8BufferLength(byteLength)
        }

    fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        when (this) {
            is SingleByte -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Utf8 -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Gb18030 -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Big5 -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is EucJp -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Iso2022Jp -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is ShiftJis -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is EucKr -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is UserDefined -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Replacement -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Utf16Be -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
            is Utf16Le -> decoder.maxUtf8BufferLengthWithoutReplacement(byteLength)
        }

    fun decodeToUtf16Raw(src: ByteArray, dst: CharArray, last: Boolean): Triple<DecoderResult, Int, Int> =
        when (this) {
            is SingleByte -> decoder.decodeToUtf16Raw(src, dst, last)
            is Utf8 -> decoder.decodeToUtf16Raw(src, dst, last)
            is Gb18030 -> decoder.decodeToUtf16Raw(src, dst, last)
            is Big5 -> decoder.decodeToUtf16Raw(src, dst, last)
            is EucJp -> decoder.decodeToUtf16Raw(src, dst, last)
            is Iso2022Jp -> decoder.decodeToUtf16Raw(src, dst, last)
            is ShiftJis -> decoder.decodeToUtf16Raw(src, dst, last)
            is EucKr -> decoder.decodeToUtf16Raw(src, dst, last)
            is UserDefined -> decoder.decodeToUtf16Raw(src, dst, last)
            is Replacement -> decoder.decodeToUtf16Raw(src, dst, last)
            is Utf16Be -> decoder.decodeToUtf16Raw(src, dst, last)
            is Utf16Le -> decoder.decodeToUtf16Raw(src, dst, last)
        }
}

internal sealed class VariantEncoder {
    data class SingleByte(
        val encoder: SingleByteEncoder,
    ) : VariantEncoder()

    data object Utf8 : VariantEncoder()

    data class Gb18030(
        val encoder: Gb18030Encoder,
    ) : VariantEncoder()

    data class Gbk(
        val encoder: Gb18030Encoder,
    ) : VariantEncoder()

    data class Big5(
        val encoder: Big5Encoder,
    ) : VariantEncoder()

    data class EucJp(
        val encoder: EucJpEncoder,
    ) : VariantEncoder()

    data class Iso2022Jp(
        val encoder: Iso2022JpEncoder,
    ) : VariantEncoder()

    data class ShiftJis(
        val encoder: ShiftJisEncoder,
    ) : VariantEncoder()

    data class EucKr(
        val encoder: EucKrEncoder,
    ) : VariantEncoder()

    data class UserDefined(
        val encoder: UserDefinedEncoder,
    ) : VariantEncoder()

    data object Replacement : VariantEncoder()

    data object Utf16Be : VariantEncoder()

    data object Utf16Le : VariantEncoder()

    fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        when (this) {
            is SingleByte -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is UserDefined -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            Utf8 -> if (u16Length > Int.MAX_VALUE / 3) null else u16Length * 3
            is Gb18030 -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is Gbk -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is Big5 -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is EucJp -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is Iso2022Jp -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is ShiftJis -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            is EucKr -> encoder.maxBufferLengthFromUtf16WithoutReplacement(u16Length)
            Utf16Be, Utf16Le -> if (u16Length > Int.MAX_VALUE / 2) null else u16Length * 2
            Replacement -> u16Length
        }

    fun encodeFromUtf16Raw(src: CharArray, dst: ByteArray, last: Boolean): Triple<EncoderResult, Int, Int> =
        when (this) {
            is SingleByte -> encoder.encodeFromUtf16Raw(src, dst, last)
            is UserDefined -> encoder.encodeFromUtf16Raw(src, dst, last)
            Utf8 -> Utf8Encoder().encodeFromUtf16Raw(src, dst, last)
            is Gb18030 -> encoder.encodeFromUtf16Raw(src, dst, last)
            is Gbk -> encoder.encodeFromUtf16Raw(src, dst, last)
            is Big5 -> encoder.encodeFromUtf16Raw(src, dst, last)
            is EucJp -> encoder.encodeFromUtf16Raw(src, dst, last)
            is Iso2022Jp -> encoder.encodeFromUtf16Raw(src, dst, last)
            is ShiftJis -> encoder.encodeFromUtf16Raw(src, dst, last)
            is EucKr -> encoder.encodeFromUtf16Raw(src, dst, last)
            Utf16Be -> {
                var s = 0
                var d = 0
                while (s < src.size) {
                    if (d + 2 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                    val code = src[s++].code
                    dst[d++] = (code shr 8).toByte()
                    dst[d++] = (code and 0xFF).toByte()
                }
                Triple(EncoderResult.InputEmpty, s, d)
            }
            Utf16Le -> {
                var s = 0
                var d = 0
                while (s < src.size) {
                    if (d + 2 > dst.size) return Triple(EncoderResult.OutputFull, s, d)
                    val code = src[s++].code
                    dst[d++] = (code and 0xFF).toByte()
                    dst[d++] = (code shr 8).toByte()
                }
                Triple(EncoderResult.InputEmpty, s, d)
            }
            Replacement -> {
                var s = 0
                var d = 0
                while (s < src.size && d < dst.size) {
                    dst[d++] = src[s++].code.toByte()
                }
                Triple(EncoderResult.InputEmpty, s, d)
            }
        }

    fun encodeFromUtf8Raw(src: String, dst: ByteArray, last: Boolean): Triple<EncoderResult, Int, Int> =
        when (this) {
            Utf8 -> Utf8Encoder().encodeFromUtf8Raw(src, dst, last)
            else -> encodeFromUtf16Raw(src.toCharArray(), dst, last)
        }
}

public class Decoder internal constructor(
    private var encoding: Encoding,
    private var variant: VariantDecoder,
    private val sniffing: BomHandling,
) {
    public fun encoding(): Encoding = encoding

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        variant.maxUtf16BufferLength(byteLength)

    public fun maxUtf8BufferLength(byteLength: Int): Int? =
        variant.maxUtf8BufferLength(byteLength)

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        variant.maxUtf8BufferLengthWithoutReplacement(byteLength)

    public fun decodeToUtf8WithoutReplacement(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        val tempDst = CharArray(dst.size)
        val (result, read, written) = decodeToUtf16Raw(src, tempDst, last)
        when (result) {
            is DecoderResult.Malformed -> return Triple(result, read, 0)
            is DecoderResult.OutputFull -> return Triple(result, read, 0)
            is DecoderResult.InputEmpty -> {
                val str = tempDst.concatToString(0, written)
                val encoded = str.encodeToByteArray()
                if (encoded.size > dst.size) {
                    return Triple(DecoderResult.OutputFull, 0, 0)
                }
                encoded.copyInto(dst, 0, 0, encoded.size)
                return Triple(DecoderResult.InputEmpty, read, encoded.size)
            }
        }
    }

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var input = src
        var bomOffset = 0
        if (sniffing == BomHandling.Sniff) {
            val bom = Encoding.forBom(src)
            if (bom != null) {
                encoding = bom.first
                variant = encoding.variant.newVariantDecoder()
                bomOffset = bom.second
                input = src.copyOfRange(bomOffset, src.size)
            }
        } else if (sniffing == BomHandling.Remove) {
            val bom = Encoding.forBom(src)
            if (bom != null && bom.first === encoding) {
                bomOffset = bom.second
                input = src.copyOfRange(bomOffset, src.size)
            }
        }
        val (result, read, written) = variant.decodeToUtf16Raw(input, dst, last)
        return Triple(result, read + bomOffset, written)
    }

    public fun decodeToUtf16(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): DecodeResult {
        var totalRead = 0
        var totalWritten = 0
        var hadErrors = false
        var input = src
        var bomOffset = 0
        if (sniffing == BomHandling.Sniff) {
            val bom = Encoding.forBom(src)
            if (bom != null) {
                encoding = bom.first
                variant = encoding.variant.newVariantDecoder()
                bomOffset = bom.second
                input = src.copyOfRange(bomOffset, src.size)
                totalRead += bomOffset
            }
        } else if (sniffing == BomHandling.Remove) {
            val bom = Encoding.forBom(src)
            if (bom != null && bom.first === encoding) {
                bomOffset = bom.second
                input = src.copyOfRange(bomOffset, src.size)
                totalRead += bomOffset
            }
        }

        var inPos = 0
        while (true) {
            val currentSlice = input.copyOfRange(inPos, input.size)
            val currentDst = CharArray(dst.size - totalWritten)
            val (result, read, written) = variant.decodeToUtf16Raw(currentSlice, currentDst, last)
            currentDst.copyInto(dst, destinationOffset = totalWritten, startIndex = 0, endIndex = written)
            inPos += read
            totalRead += read
            totalWritten += written
            when (result) {
                is DecoderResult.InputEmpty -> {
                    return DecodeResult(CoderResult.InputEmpty, totalRead, totalWritten, hadErrors)
                }
                is DecoderResult.OutputFull -> {
                    return DecodeResult(CoderResult.OutputFull, totalRead, totalWritten, hadErrors)
                }
                is DecoderResult.Malformed -> {
                    hadErrors = true
                    if (totalWritten >= dst.size) {
                        return DecodeResult(CoderResult.OutputFull, totalRead, totalWritten, hadErrors)
                    }
                    dst[totalWritten++] = '\uFFFD'
                }
            }
        }
    }
}

public class Encoder internal constructor(
    private val encoding: Encoding,
    private val variant: VariantEncoder,
) {
    public fun encoding(): Encoding = encoding

    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        variant.maxBufferLengthFromUtf16WithoutReplacement(u16Length)

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        byteLength

    public fun encodeFromUtf16Raw(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> =
        variant.encodeFromUtf16Raw(src, dst, last)

    public fun encodeFromUtf8Raw(
        src: String,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> =
        variant.encodeFromUtf8Raw(src, dst, last)

    public fun encodeFromUtf16WithoutReplacement(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> =
        encodeFromUtf16Raw(src, dst, last)

    public fun encodeFromUtf8WithoutReplacement(
        src: String,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<EncoderResult, Int, Int> =
        encodeFromUtf8Raw(src, dst, last)

    public fun encodeFromUtf16(
        src: CharArray,
        dst: ByteArray,
        last: Boolean = false,
    ): EncodeResult {
        var totalRead = 0
        var totalWritten = 0
        var hadErrors = false
        while (totalRead < src.size) {
            val currentDst = ByteArray(dst.size - totalWritten)
            val (result, read, written) =
                encodeFromUtf16Raw(
                    src.copyOfRange(totalRead, src.size),
                    currentDst,
                    last,
                )
            currentDst.copyInto(dst, destinationOffset = totalWritten, startIndex = 0, endIndex = written)
            totalRead += read
            totalWritten += written
            when (result) {
                is EncoderResult.InputEmpty -> {
                    return EncodeResult(CoderResult.InputEmpty, totalRead, totalWritten, hadErrors)
                }
                is EncoderResult.OutputFull -> {
                    return EncodeResult(CoderResult.OutputFull, totalRead, totalWritten, hadErrors)
                }
                is EncoderResult.Unmappable -> {
                    hadErrors = true
                    val codePoint = result.codePoint
                    val ref = "&#$codePoint;".encodeToByteArray()
                    if (totalWritten + ref.size > dst.size) {
                        return EncodeResult(CoderResult.OutputFull, totalRead, totalWritten, hadErrors)
                    }
                    for (i in ref.indices) {
                        dst[totalWritten + i] = ref[i]
                    }
                    totalWritten += ref.size
                }
            }
        }
        return EncodeResult(CoderResult.InputEmpty, totalRead, totalWritten, hadErrors)
    }
}
