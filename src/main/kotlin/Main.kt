package com.professionalowo

fun main() {
    val raw =
        token('1') orLift token('2') orLift token('3') orLift token('4') orLift token('5') orLift token('6') orLift token(
            '7'
        ) orLift token('8') orLift token('9') orLift token('0')
    val digitParser = raw.map { digit(it) }
    val digitsParser = many1(digitParser).map { listOf(it.first) + it.second }
    println(digitsParser.parse("123abc").getOrThrow())
}

sealed interface JSON {
    data object JSONNull : JSON
    class JSONString(val literal: String) : JSON
    class JSONNumber(val number: Float) : JSON
    enum class JSONBoolean : JSON {
        TRUE,
        FALSE
    }
    class JSONArray(val values: List<JSON>) : JSON
    class JSONObject(val values: Map<String, JSON>) : JSON
}