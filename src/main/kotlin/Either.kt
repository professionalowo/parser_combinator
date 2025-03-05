package com.professionalowo

data class Either<U, V>(val left: U?, val right: V?) {
    companion object {
        fun <U,V> left(value: U) = Either<U,V>(value, null)
        fun <U,V> right(value: V) = Either<U,V>(null, value)
    }

    fun isLeft() = left != null
    fun isRight() = right != null
}

fun <U> Either<out U, out U>.lift(): U = if (isLeft()) left!! else right!!
