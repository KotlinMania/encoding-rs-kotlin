// port-lint: source lib.rs
package io.github.kotlinmania.encodingrs

/**
 * An encoding as defined in the WHATWG Encoding Standard.
 */
class Encoding internal constructor(
    public val name: String,
    internal val variant: VariantEncoding,
) {
    internal fun newVariantDecoder(): VariantDecoder = variant.newVariantDecoder()

    public override fun equals(other: Any?): Boolean =
        other is Encoding && name == other.name

    public override fun hashCode(): Int = name.hashCode()

    public override fun toString(): String = name

    public fun fmt(): String = name

    public fun serialize(): String = name

    public fun deserialize(s: String): Encoding? = Encoding.forLabel(s.encodeToByteArray())

    public fun cmp(other: Encoding): Int = name.compareTo(other.name)

    public fun partialCmp(other: Encoding): Int = cmp(other)

    public fun eq(other: Encoding): Boolean = equals(other)

    public fun hash(): Int = hashCode()

    public fun isSingleByte(): Boolean =
        variant is VariantEncoding.SingleByte || variant is VariantEncoding.UserDefined

    public fun isAsciiCompatible(): Boolean =
        when (variant) {
            is VariantEncoding.Replacement,
            is VariantEncoding.Utf16Be,
            is VariantEncoding.Utf16Le,
            is VariantEncoding.Iso2022Jp,
            -> false
            else -> true
        }

    public fun isPotentiallyBorrowable(): Boolean =
        when (variant) {
            is VariantEncoding.Replacement,
            is VariantEncoding.Utf16Be,
            is VariantEncoding.Utf16Le,
            is VariantEncoding.Iso2022Jp,
            -> false
            else -> true
        }

    public fun canEncodeEverything(): Boolean = outputEncoding().name == "UTF-8"

    public fun outputEncoding(): Encoding =
        when (variant) {
            is VariantEncoding.Replacement,
            is VariantEncoding.Utf16Be,
            is VariantEncoding.Utf16Le,
            -> UTF_8
            else -> this
        }

    public fun newDecoder(): Decoder =
        Decoder(this, variant.newVariantDecoder(), BomHandling.Sniff)

    public fun newDecoderWithBomRemoval(): Decoder =
        Decoder(this, variant.newVariantDecoder(), BomHandling.Remove)

    public fun newDecoderWithoutBomHandling(): Decoder =
        Decoder(this, variant.newVariantDecoder(), BomHandling.Off)

    public fun newEncoder(): Encoder {
        val enc = outputEncoding()
        return enc.variant.newEncoder(enc)
    }

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

    public fun decodeWithBomRemoval(bytes: ByteArray): Pair<String, Boolean> {
        val withoutBom =
            if (this === UTF_8 && bytes.size >= 3 && (bytes[0].toInt() and 0xFF) == 0xEF && (bytes[1].toInt() and 0xFF) == 0xBB && (bytes[2].toInt() and 0xFF) == 0xBF) {
                bytes.copyOfRange(3, bytes.size)
            } else if ((this === UTF_16LE && bytes.size >= 2 && (bytes[0].toInt() and 0xFF) == 0xFF && (bytes[1].toInt() and 0xFF) == 0xFE) ||
                (this === UTF_16BE && bytes.size >= 2 && (bytes[0].toInt() and 0xFF) == 0xFE && (bytes[1].toInt() and 0xFF) == 0xFF)
            ) {
                bytes.copyOfRange(2, bytes.size)
            } else {
                bytes
            }
        return decodeWithoutBomHandling(withoutBom)
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

        public fun asciiValidUpTo(bytes: ByteArray): Int = Ascii.asciiValidUpTo(bytes)

        public fun utf8ValidUpTo(bytes: ByteArray): Int =
            Utf8.convertUtf8ToUtf16UpToInvalid(bytes, CharArray(bytes.size)).first

        public fun iso2022JpAsciiValidUpTo(bytes: ByteArray): Int = Ascii.iso2022JpAsciiValidUpTo(bytes)
    }
}

internal enum class BomHandling {
    Off,
    Sniff,
    Remove,
}

internal enum class DecoderLifeCycle {
    AtStart,
    AtUtf8Start,
    AtUtf16BeStart,
    AtUtf16LeStart,
    SeenUtf8First,
    SeenUtf8Second,
    SeenUtf16BeFirst,
    SeenUtf16LeFirst,
    ConvertingWithPendingBB,
    Converting,
    Finished,
}

class Decoder internal constructor(
    private var encoding: Encoding,
    private var variant: VariantDecoder,
    sniffing: BomHandling,
) {
    private var lifeCycle: DecoderLifeCycle =
        when (sniffing) {
            BomHandling.Off -> DecoderLifeCycle.Converting
            BomHandling.Sniff -> DecoderLifeCycle.AtStart
            BomHandling.Remove ->
                when (encoding.name) {
                    "UTF-8" -> DecoderLifeCycle.AtUtf8Start
                    "UTF-16BE" -> DecoderLifeCycle.AtUtf16BeStart
                    "UTF-16LE" -> DecoderLifeCycle.AtUtf16LeStart
                    else -> DecoderLifeCycle.Converting
                }
        }

    public fun encoding(): Encoding = encoding

    public fun maxUtf16BufferLength(byteLength: Int): Int? =
        when (lifeCycle) {
            DecoderLifeCycle.Converting,
            DecoderLifeCycle.AtUtf8Start,
            DecoderLifeCycle.AtUtf16LeStart,
            DecoderLifeCycle.AtUtf16BeStart,
            -> variant.maxUtf16BufferLength(byteLength)
            DecoderLifeCycle.AtStart -> {
                val utf8Bom = byteLength + 1
                val utf16Bom = 1 + (byteLength + 1) / 2
                val utfBom = maxOf(utf8Bom, utf16Bom)
                val enc = encoding()
                if (enc.name == "UTF-8" || enc.name == "UTF-16LE" || enc.name == "UTF-16BE") {
                    utfBom
                } else {
                    val nonBom = variant.maxUtf16BufferLength(byteLength) ?: return null
                    maxOf(utfBom, nonBom)
                }
            }
            DecoderLifeCycle.SeenUtf8First,
            DecoderLifeCycle.SeenUtf8Second,
            -> {
                val sum = byteLength + 2
                val utf8Bom = sum + 1
                if (encoding().name == "UTF-8") {
                    utf8Bom
                } else {
                    val nonBom = variant.maxUtf16BufferLength(sum) ?: return null
                    maxOf(utf8Bom, nonBom)
                }
            }
            DecoderLifeCycle.ConvertingWithPendingBB -> variant.maxUtf16BufferLength(byteLength + 2)
            DecoderLifeCycle.SeenUtf16LeFirst,
            DecoderLifeCycle.SeenUtf16BeFirst,
            -> {
                val sum = byteLength + 2
                val utf16Bom = 1 + (sum + 1) / 2
                if (encoding().name == "UTF-16LE" || encoding().name == "UTF-16BE") {
                    utf16Bom
                } else {
                    val nonBom = variant.maxUtf16BufferLength(sum) ?: return null
                    maxOf(utf16Bom, nonBom)
                }
            }
            DecoderLifeCycle.Finished -> error("Must not use a decoder that has finished.")
        }

    public fun maxUtf8BufferLength(byteLength: Int): Int? =
        when (lifeCycle) {
            DecoderLifeCycle.Converting,
            DecoderLifeCycle.AtUtf8Start,
            DecoderLifeCycle.AtUtf16LeStart,
            DecoderLifeCycle.AtUtf16BeStart,
            -> variant.maxUtf8BufferLength(byteLength)
            DecoderLifeCycle.AtStart -> {
                val utf8Bom = 3 + byteLength * 3
                val utf16Bom = 1 + 3 * ((byteLength + 1) / 2)
                val utfBom = maxOf(utf8Bom, utf16Bom)
                val enc = encoding()
                if (enc.name == "UTF-8" || enc.name == "UTF-16LE" || enc.name == "UTF-16BE") {
                    utfBom
                } else {
                    val nonBom = variant.maxUtf8BufferLength(byteLength) ?: return null
                    maxOf(utfBom, nonBom)
                }
            }
            DecoderLifeCycle.SeenUtf8First,
            DecoderLifeCycle.SeenUtf8Second,
            -> {
                val sum = byteLength + 2
                val utf8Bom = 3 + sum * 3
                if (encoding().name == "UTF-8") {
                    utf8Bom
                } else {
                    val nonBom = variant.maxUtf8BufferLength(sum) ?: return null
                    maxOf(utf8Bom, nonBom)
                }
            }
            DecoderLifeCycle.ConvertingWithPendingBB -> variant.maxUtf8BufferLength(byteLength + 2)
            DecoderLifeCycle.SeenUtf16LeFirst,
            DecoderLifeCycle.SeenUtf16BeFirst,
            -> {
                val sum = byteLength + 2
                val utf16Bom = 1 + 3 * ((sum + 1) / 2)
                if (encoding().name == "UTF-16LE" || encoding().name == "UTF-16BE") {
                    utf16Bom
                } else {
                    val nonBom = variant.maxUtf8BufferLength(sum) ?: return null
                    maxOf(utf16Bom, nonBom)
                }
            }
            DecoderLifeCycle.Finished -> error("Must not use a decoder that has finished.")
        }

    public fun maxUtf8BufferLengthWithoutReplacement(byteLength: Int): Int? =
        when (lifeCycle) {
            DecoderLifeCycle.Converting,
            DecoderLifeCycle.AtUtf8Start,
            DecoderLifeCycle.AtUtf16LeStart,
            DecoderLifeCycle.AtUtf16BeStart,
            -> variant.maxUtf8BufferLengthWithoutReplacement(byteLength)
            DecoderLifeCycle.AtStart -> {
                val utf8Bom = byteLength + 3
                val utf16Bom = 1 + 3 * ((byteLength + 1) / 2)
                val utfBom = maxOf(utf8Bom, utf16Bom)
                val enc = encoding()
                if (enc.name == "UTF-8" || enc.name == "UTF-16LE" || enc.name == "UTF-16BE") {
                    utfBom
                } else {
                    val nonBom = variant.maxUtf8BufferLengthWithoutReplacement(byteLength) ?: return null
                    maxOf(utfBom, nonBom)
                }
            }
            DecoderLifeCycle.SeenUtf8First,
            DecoderLifeCycle.SeenUtf8Second,
            -> {
                val sum = byteLength + 2
                val utf8Bom = sum + 3
                if (encoding().name == "UTF-8") {
                    utf8Bom
                } else {
                    val nonBom = variant.maxUtf8BufferLengthWithoutReplacement(sum) ?: return null
                    maxOf(utf8Bom, nonBom)
                }
            }
            DecoderLifeCycle.ConvertingWithPendingBB -> variant.maxUtf8BufferLengthWithoutReplacement(byteLength + 2)
            DecoderLifeCycle.SeenUtf16LeFirst,
            DecoderLifeCycle.SeenUtf16BeFirst,
            -> {
                val sum = byteLength + 2
                val utf16Bom = 1 + 3 * ((sum + 1) / 2)
                if (encoding().name == "UTF-16LE" || encoding().name == "UTF-16BE") {
                    utf16Bom
                } else {
                    val nonBom = variant.maxUtf8BufferLengthWithoutReplacement(sum) ?: return null
                    maxOf(utf16Bom, nonBom)
                }
            }
            DecoderLifeCycle.Finished -> error("Must not use a decoder that has finished.")
        }

    public fun decodeToUtf16WithoutReplacement(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        var offset = 0
        while (true) {
            when (lifeCycle) {
                DecoderLifeCycle.Converting -> {
                    val currentSlice = if (offset == 0) src else src.copyOfRange(offset, src.size)
                    val (res, read, written) = variant.decodeToUtf16Raw(currentSlice, dst, last)
                    return Triple(res, read + offset, written)
                }
                DecoderLifeCycle.AtStart -> {
                    if (src.isEmpty()) {
                        return Triple(DecoderResult.InputEmpty, 0, 0)
                    }
                    when (src[0].toInt() and 0xFF) {
                        0xEF -> {
                            lifeCycle = DecoderLifeCycle.SeenUtf8First
                            offset += 1
                            continue
                        }
                        0xFE -> {
                            lifeCycle = DecoderLifeCycle.SeenUtf16BeFirst
                            offset += 1
                            continue
                        }
                        0xFF -> {
                            lifeCycle = DecoderLifeCycle.SeenUtf16LeFirst
                            offset += 1
                            continue
                        }
                        else -> {
                            lifeCycle = DecoderLifeCycle.Converting
                            continue
                        }
                    }
                }
                DecoderLifeCycle.AtUtf8Start -> {
                    if (src.isEmpty()) {
                        return Triple(DecoderResult.InputEmpty, 0, 0)
                    }
                    if ((src[0].toInt() and 0xFF) == 0xEF) {
                        lifeCycle = DecoderLifeCycle.SeenUtf8First
                        offset += 1
                        continue
                    } else {
                        lifeCycle = DecoderLifeCycle.Converting
                        continue
                    }
                }
                DecoderLifeCycle.AtUtf16BeStart -> {
                    if (src.isEmpty()) {
                        return Triple(DecoderResult.InputEmpty, 0, 0)
                    }
                    if ((src[0].toInt() and 0xFF) == 0xFE) {
                        lifeCycle = DecoderLifeCycle.SeenUtf16BeFirst
                        offset += 1
                        continue
                    } else {
                        lifeCycle = DecoderLifeCycle.Converting
                        continue
                    }
                }
                DecoderLifeCycle.AtUtf16LeStart -> {
                    if (src.isEmpty()) {
                        return Triple(DecoderResult.InputEmpty, 0, 0)
                    }
                    if ((src[0].toInt() and 0xFF) == 0xFF) {
                        lifeCycle = DecoderLifeCycle.SeenUtf16LeFirst
                        offset += 1
                        continue
                    } else {
                        lifeCycle = DecoderLifeCycle.Converting
                        continue
                    }
                }
                DecoderLifeCycle.SeenUtf8First -> {
                    if (offset >= src.size) {
                        if (last) {
                            return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, offset, 0xEF.toByte())
                        }
                        return Triple(DecoderResult.InputEmpty, offset, 0)
                    }
                    if ((src[offset].toInt() and 0xFF) == 0xBB) {
                        lifeCycle = DecoderLifeCycle.SeenUtf8Second
                        offset += 1
                        continue
                    }
                    return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, offset, 0xEF.toByte())
                }
                DecoderLifeCycle.SeenUtf8Second -> {
                    if (offset >= src.size) {
                        if (last) {
                            return decodeToUtf16AfterTwoPotentialBomBytes(src, dst, last, offset)
                        }
                        return Triple(DecoderResult.InputEmpty, offset, 0)
                    }
                    if ((src[offset].toInt() and 0xFF) == 0xBF) {
                        lifeCycle = DecoderLifeCycle.Converting
                        offset += 1
                        if (encoding.name != "UTF-8") {
                            encoding = UTF_8
                            variant = UTF_8.variant.newVariantDecoder()
                        }
                        continue
                    }
                    return decodeToUtf16AfterTwoPotentialBomBytes(src, dst, last, offset)
                }
                DecoderLifeCycle.SeenUtf16BeFirst -> {
                    if (offset >= src.size) {
                        if (last) {
                            return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, offset, 0xFE.toByte())
                        }
                        return Triple(DecoderResult.InputEmpty, offset, 0)
                    }
                    if ((src[offset].toInt() and 0xFF) == 0xFF) {
                        lifeCycle = DecoderLifeCycle.Converting
                        offset += 1
                        if (encoding.name != "UTF-16BE") {
                            encoding = UTF_16BE
                            variant = UTF_16BE.variant.newVariantDecoder()
                        }
                        continue
                    }
                    return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, offset, 0xFE.toByte())
                }
                DecoderLifeCycle.SeenUtf16LeFirst -> {
                    if (offset >= src.size) {
                        if (last) {
                            return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, offset, 0xFF.toByte())
                        }
                        return Triple(DecoderResult.InputEmpty, offset, 0)
                    }
                    if ((src[offset].toInt() and 0xFF) == 0xFE) {
                        lifeCycle = DecoderLifeCycle.Converting
                        offset += 1
                        if (encoding.name != "UTF-16LE") {
                            encoding = UTF_16LE
                            variant = UTF_16LE.variant.newVariantDecoder()
                        }
                        continue
                    }
                    return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, offset, 0xFF.toByte())
                }
                DecoderLifeCycle.ConvertingWithPendingBB -> {
                    return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, 0, 0xBB.toByte())
                }
                DecoderLifeCycle.Finished -> error("Must not use a decoder that has finished.")
            }
        }
    }

    private fun decodeToUtf16AfterOnePotentialBomByte(
        src: ByteArray,
        dst: CharArray,
        last: Boolean,
        offset: Int,
        firstByte: Byte,
    ): Triple<DecoderResult, Int, Int> {
        lifeCycle = DecoderLifeCycle.Converting
        if (offset == 0) {
            val first = byteArrayOf(firstByte)
            var (firstResult, _, firstWritten) = variant.decodeToUtf16Raw(first, dst, false)
            var outRead = 0
            when (firstResult) {
                is DecoderResult.InputEmpty -> {
                    val subDst = dst.copyOfRange(firstWritten, dst.size)
                    val (result, read, written) = variant.decodeToUtf16Raw(src, subDst, last)
                    subDst.copyInto(dst, destinationOffset = firstWritten, startIndex = 0, endIndex = written)
                    firstResult = result
                    outRead = read
                    firstWritten += written
                }
                is DecoderResult.Malformed -> {
                    // Not read from src
                }
                is DecoderResult.OutputFull -> {
                    error("Output buffer must have been too small.")
                }
            }
            return Triple(firstResult, outRead, firstWritten)
        }
        return variant.decodeToUtf16Raw(src, dst, last)
    }

    private fun decodeToUtf16AfterTwoPotentialBomBytes(
        src: ByteArray,
        dst: CharArray,
        last: Boolean,
        offset: Int,
    ): Triple<DecoderResult, Int, Int> {
        lifeCycle = DecoderLifeCycle.Converting
        if (offset == 0) {
            val efBb = byteArrayOf(0xEF.toByte(), 0xBB.toByte())
            var (firstResult, firstRead, firstWritten) = variant.decodeToUtf16Raw(efBb, dst, false)
            var outRead = 0
            when (firstResult) {
                is DecoderResult.InputEmpty -> {
                    val subDst = dst.copyOfRange(firstWritten, dst.size)
                    val (result, read, written) = variant.decodeToUtf16Raw(src, subDst, last)
                    subDst.copyInto(dst, destinationOffset = firstWritten, startIndex = 0, endIndex = written)
                    firstResult = result
                    outRead = read
                    firstWritten += written
                }
                is DecoderResult.Malformed -> {
                    if (firstRead == 1) {
                        lifeCycle = DecoderLifeCycle.ConvertingWithPendingBB
                    }
                    outRead = 0
                }
                is DecoderResult.OutputFull -> {
                    error("Output buffer must have been too small.")
                }
            }
            return Triple(firstResult, outRead, firstWritten)
        }
        if (offset == 1) {
            return decodeToUtf16AfterOnePotentialBomByte(src, dst, last, 0, 0xEF.toByte())
        }
        return variant.decodeToUtf16Raw(src, dst, last)
    }

    public fun decodeToUtf16Raw(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> = decodeToUtf16WithoutReplacement(src, dst, last)

    public fun decodeToUtf8WithoutReplacement(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        val tempDst = CharArray(dst.size)
        val (result, read, written) = decodeToUtf16WithoutReplacement(src, tempDst, last)
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

    public fun decodeToUtf16(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): DecodeResult {
        var totalRead = 0
        var totalWritten = 0
        var hadErrors = false
        while (true) {
            val currentSlice = if (totalRead == 0) src else src.copyOfRange(totalRead, src.size)
            val currentDst = CharArray(dst.size - totalWritten)
            val (result, read, written) = decodeToUtf16WithoutReplacement(currentSlice, currentDst, last)
            currentDst.copyInto(dst, destinationOffset = totalWritten, startIndex = 0, endIndex = written)
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

    public fun decodeToUtf8(
        src: ByteArray,
        dst: ByteArray,
        last: Boolean = false,
    ): DecodeResult {
        var totalRead = 0
        var totalWritten = 0
        var hadErrors = false
        while (true) {
            val currentSlice = if (totalRead == 0) src else src.copyOfRange(totalRead, src.size)
            val currentDst = ByteArray(dst.size - totalWritten)
            val (result, read, written) = decodeToUtf8WithoutReplacement(currentSlice, currentDst, last)
            currentDst.copyInto(dst, destinationOffset = totalWritten, startIndex = 0, endIndex = written)
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
                    val replacement = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
                    if (totalWritten + 3 > dst.size) {
                        return DecodeResult(CoderResult.OutputFull, totalRead, totalWritten, hadErrors)
                    }
                    replacement.copyInto(dst, destinationOffset = totalWritten)
                    totalWritten += 3
                }
            }
        }
    }

    public fun decodeToStr(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): DecodeResult {
        val bytes = dst.concatToString().encodeToByteArray()
        return decodeToUtf8(src, bytes, last)
    }

    public fun decodeToString(
        src: ByteArray,
        dst: StringBuilder,
        last: Boolean = false,
    ): DecodeResult {
        val out = ByteArray(maxUtf8BufferLength(src.size) ?: (src.size * 3 + 4))
        val result = decodeToUtf8(src, out, last)
        dst.append(out.decodeToString(0, result.written))
        return result
    }

    public fun decodeToStrWithoutReplacement(
        src: ByteArray,
        dst: CharArray,
        last: Boolean = false,
    ): Triple<DecoderResult, Int, Int> {
        val bytes = dst.concatToString().encodeToByteArray()
        return decodeToUtf8WithoutReplacement(src, bytes, last)
    }

    public fun decodeToStringWithoutReplacement(
        src: ByteArray,
        dst: StringBuilder,
        last: Boolean = false,
    ): Pair<DecoderResult, Int> {
        val out = ByteArray(maxUtf8BufferLengthWithoutReplacement(src.size) ?: (src.size * 3 + 4))
        val (res, read, written) = decodeToUtf8WithoutReplacement(src, out, last)
        dst.append(out.decodeToString(0, written))
        return Pair(res, read)
    }

    public fun latin1ByteCompatibleUpTo(buffer: ByteArray): Int? =
        when (lifeCycle) {
            DecoderLifeCycle.Converting -> variant.latin1ByteCompatibleUpTo(buffer)
            DecoderLifeCycle.Finished -> error("Must not use a decoder that has finished.")
            else -> null
        }

    public companion object {
        internal fun new(
            encoding: Encoding,
            decoder: VariantDecoder,
            sniffing: BomHandling,
        ): Decoder = Decoder(encoding, decoder, sniffing)
    }
}

class Encoder internal constructor(
    private val encoding: Encoding,
    private val variant: VariantEncoder,
) {
    public fun encoding(): Encoding = encoding

    public fun hasPendingState(): Boolean = variant.hasPendingState()

    public fun maxBufferLengthFromUtf16WithoutReplacement(u16Length: Int): Int? =
        variant.maxBufferLengthFromUtf16WithoutReplacement(u16Length)

    public fun maxBufferLengthFromUtf8WithoutReplacement(byteLength: Int): Int? =
        byteLength

    public fun maxBufferLengthFromUtf8IfNoUnmappables(byteLength: Int): Int? =
        checkedAdd(
            if (encoding.canEncodeEverything()) 0 else 12,
            maxBufferLengthFromUtf8WithoutReplacement(byteLength),
        )

    public fun maxBufferLengthFromUtf16IfNoUnmappables(u16Length: Int): Int? =
        checkedAdd(
            if (encoding.canEncodeEverything()) 0 else 12,
            maxBufferLengthFromUtf16WithoutReplacement(u16Length),
        )

    public fun encodeFromUtf8ToVec(
        src: String,
        dst: MutableList<Byte>,
        last: Boolean = false,
    ): Triple<CoderResult, Int, Boolean> {
        val out = ByteArray(maxBufferLengthFromUtf8IfNoUnmappables(src.length) ?: (src.length * 3 + 4))
        val res = encodeFromUtf8(src, out, last)
        for (i in 0 until res.written) {
            dst.add(out[i])
        }
        return Triple(res.result, res.read, res.hadErrors)
    }

    public fun encodeFromUtf8ToVecWithoutReplacement(
        src: String,
        dst: MutableList<Byte>,
        last: Boolean = false,
    ): Pair<EncoderResult, Int> {
        val out = ByteArray(maxBufferLengthFromUtf8WithoutReplacement(src.length) ?: (src.length * 3 + 4))
        val (res, read, written) = encodeFromUtf8WithoutReplacement(src, out, last)
        for (i in 0 until written) {
            dst.add(out[i])
        }
        return Pair(res, read)
    }

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

    public fun encodeFromUtf8(
        src: String,
        dst: ByteArray,
        last: Boolean = false,
    ): EncodeResult {
        val chars = src.toCharArray()
        return encodeFromUtf16(chars, dst, last)
    }

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

    public companion object {
        internal fun new(
            encoding: Encoding,
            encoder: VariantEncoder,
        ): Encoder = Encoder(encoding, encoder)
    }
}

public val BIG5: Encoding = Encoding.BIG5
public val EUC_JP: Encoding = Encoding.EUC_JP
public val EUC_KR: Encoding = Encoding.EUC_KR
public val GB18030: Encoding = Encoding.GB18030
public val GBK: Encoding = Encoding.GBK
public val IBM866: Encoding = Encoding.IBM866
public val ISO_2022_JP: Encoding = Encoding.ISO_2022_JP
public val ISO_8859_10: Encoding = Encoding.ISO_8859_10
public val ISO_8859_13: Encoding = Encoding.ISO_8859_13
public val ISO_8859_14: Encoding = Encoding.ISO_8859_14
public val ISO_8859_15: Encoding = Encoding.ISO_8859_15
public val ISO_8859_16: Encoding = Encoding.ISO_8859_16
public val ISO_8859_2: Encoding = Encoding.ISO_8859_2
public val ISO_8859_3: Encoding = Encoding.ISO_8859_3
public val ISO_8859_4: Encoding = Encoding.ISO_8859_4
public val ISO_8859_5: Encoding = Encoding.ISO_8859_5
public val ISO_8859_6: Encoding = Encoding.ISO_8859_6
public val ISO_8859_7: Encoding = Encoding.ISO_8859_7
public val ISO_8859_8: Encoding = Encoding.ISO_8859_8
public val ISO_8859_8_I: Encoding = Encoding.ISO_8859_8_I
public val KOI8_R: Encoding = Encoding.KOI8_R
public val KOI8_U: Encoding = Encoding.KOI8_U
public val MACINTOSH: Encoding = Encoding.MACINTOSH
public val REPLACEMENT: Encoding = Encoding.REPLACEMENT
public val SHIFT_JIS: Encoding = Encoding.SHIFT_JIS
public val UTF_16BE: Encoding = Encoding.UTF_16BE
public val UTF_16LE: Encoding = Encoding.UTF_16LE
public val UTF_8: Encoding = Encoding.UTF_8
public val WINDOWS_1250: Encoding = Encoding.WINDOWS_1250
public val WINDOWS_1251: Encoding = Encoding.WINDOWS_1251
public val WINDOWS_1252: Encoding = Encoding.WINDOWS_1252
public val WINDOWS_1253: Encoding = Encoding.WINDOWS_1253
public val WINDOWS_1254: Encoding = Encoding.WINDOWS_1254
public val WINDOWS_1255: Encoding = Encoding.WINDOWS_1255
public val WINDOWS_1256: Encoding = Encoding.WINDOWS_1256
public val WINDOWS_1257: Encoding = Encoding.WINDOWS_1257
public val WINDOWS_1258: Encoding = Encoding.WINDOWS_1258
public val WINDOWS_874: Encoding = Encoding.WINDOWS_874
public val X_MAC_CYRILLIC: Encoding = Encoding.X_MAC_CYRILLIC
public val X_USER_DEFINED: Encoding = Encoding.X_USER_DEFINED

/**
 * Visitor for deserializing Encoding references.
 */
public class EncodingVisitor {
    public typealias Value = Encoding

    public fun expecting(): String = "a valid encoding label"

    public fun visitStr(value: String): Encoding? = Encoding.forLabel(value.encodeToByteArray())
}

/**
 * Demo structure matching upstream serde tests.
 */
public data class Demo(
    public val num: UInt,
    public val name: String,
    public val enc: Encoding,
)

/**
 * Format an unmappable as NCR without heap allocation.
 */
internal fun writeNcr(unmappable: Char, dst: ByteArray, dstOffset: Int = 0): Int {
    var number = unmappable.code
    val len =
        when {
            number >= 1_000_000 -> 10
            number >= 100_000 -> 9
            number >= 10_000 -> 8
            number >= 1_000 -> 7
            number >= 100 -> 6
            else -> 5
        }
    var pos = dstOffset + len - 1
    dst[pos--] = ';'.code.toByte()
    while (true) {
        val rightmost = number % 10
        dst[pos--] = (rightmost + '0'.code).toByte()
        if (number < 10) break
        number /= 10
    }
    dst[dstOffset + 1] = '#'.code.toByte()
    dst[dstOffset] = '&'.code.toByte()
    return len
}

internal fun inRange16(i: Int, start: Int, end: Int): Boolean =
    ((i - start) and 0xFFFF) < (end - start)

internal fun inRange32(i: Long, start: Long, end: Long): Boolean =
    ((i - start) and 0xFFFFFFFFL) < (end - start)

internal fun inInclusiveRange8(i: Byte, start: Byte, end: Byte): Boolean =
    ((i.toInt() - start.toInt()) and 0xFF) <= ((end.toInt() - start.toInt()) and 0xFF)

internal fun inInclusiveRange16(i: Int, start: Int, end: Int): Boolean =
    ((i - start) and 0xFFFF) <= (end - start)

internal fun inInclusiveRange32(i: Long, start: Long, end: Long): Boolean =
    ((i - start) and 0xFFFFFFFFL) <= (end - start)

internal fun inInclusiveRange(i: Int, start: Int, end: Int): Boolean =
    (i - start) in 0..(end - start)

internal fun checkedAdd(num: Int, opt: Int?): Int? =
    opt?.let { if (Int.MAX_VALUE - num < it) null else it + num }

internal fun checkedAddOpt(one: Int?, other: Int?): Int? =
    one?.let { checkedAdd(it, other) }

internal fun checkedMul(num: Int, opt: Int?): Int? =
    opt?.let { if (num != 0 && it > Int.MAX_VALUE / num) null else it * num }

internal fun checkedDiv(opt: Int?, num: Int): Int? =
    opt?.let { it / num }

internal fun checkedNextPowerOfTwo(opt: Int?): Int? =
    opt?.let {
        var v = it - 1
        v = v or (v ushr 1)
        v = v or (v ushr 2)
        v = v or (v ushr 4)
        v = v or (v ushr 8)
        v = v or (v ushr 16)
        v + 1
    }

internal fun checkedMin(one: Int?, other: Int?): Int? =
    when {
        one != null && other != null -> minOf(one, other)
        one != null -> one
        else -> other
    }
