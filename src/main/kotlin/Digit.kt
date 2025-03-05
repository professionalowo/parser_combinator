package com.professionalowo

enum class Digit {
    Zero, One, Two, Three, Four, Five, Six, Seven, Eight, Nine
}

fun digit(char: Char):Digit = when(char){
    '0' -> Digit.Zero
    '1' -> Digit.One
    '2' -> Digit.Two
    '3' -> Digit.Three
    '4' -> Digit.Four
    '5' -> Digit.Five
    '6' -> Digit.Six
    '7' -> Digit.Seven
    '8' -> Digit.Eight
    '9' -> Digit.Nine
    else -> throw IllegalArgumentException("Invalid character '$char'")
}