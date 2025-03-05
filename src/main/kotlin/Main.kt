package com.professionalowo

fun main() {
    println(numberParser().parse("123abc").getOrThrow())
    println(jsonNull().parse("null").getOrThrow())
}

fun jsonNull(): Parser<JSON.JSONNull> =
    ((token('n').map { listOf(it) }) andList token('u') andList token('l') andList token('l')).map { JSON.JSONNull }

fun numberParser(): Parser<JSON.JSONNumber> {
    val raw =
        token('1') orLift token('2') orLift token('3') orLift token('4') orLift token('5') orLift token('6') orLift token(
            '7'
        ) orLift token('8') orLift token('9') orLift token('0')
    return many1(raw).map { listOf(it.first) + it.second }.map { it.joinToString("").toInt() }
        .map { JSON.JSONNumber(it) }
}

sealed interface JSON {
    data object JSONNull : JSON
    data class JSONString(val literal: String) : JSON
    data class JSONNumber(val number: Int) : JSON
    enum class JSONBoolean : JSON {
        TRUE,
        FALSE
    }

    data class JSONArray(val values: List<JSON>) : JSON
    data class JSONObject(val values: Map<String, JSON>) : JSON
}