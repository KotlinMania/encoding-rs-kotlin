package io.github.kotlinmania.encodingrs

fun gb180302022OverrideCount(): Int = GB18030_2022_OVERRIDE_PUA.size

fun gb180302022OverridePuaAt(index: Int): Int =
    GB18030_2022_OVERRIDE_PUA[index].toInt()

fun gb180302022OverrideByteAt(index: Int, byteIndex: Int): Int =
    GB18030_2022_OVERRIDE_BYTES[index][byteIndex].toInt()
