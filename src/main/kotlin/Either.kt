package com.professionalowo

data class Either<U, V>(val left: U?, val right: V?) {
    companion object {
        fun <U> left(value: U) = Either(value, null)
        fun <V> right(value: V) = Either(null, value)
    }

    fun isLeft() = left != null
    fun isRight() = right != null
}

fun <U> Either<out U, out U>.lift(): U = if (isLeft()) left!! else right!!
