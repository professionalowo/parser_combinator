package com.professionalowo

fun main() {
    val raw =
        token('1') orLift token('2') orLift token('3') orLift token('4') orLift token('5') orLift token('6') orLift token(
            '7'
        ) orLift token('8') orLift token('9') orLift token('0')
    val digitParser = raw.map { digit(it) }
    val digitsParser = many1(digitParser).map { listOf(it.first) + it.second }
    println(digitsParser.parse("123abc"))
}

fun interface Parser<T> {
    fun parse(value: String): Pair<T, String>?
    fun <D> map(mapper: (T) -> D): Parser<D> = Parser {
        parse(it)?.let { p -> Pair(mapper(p.first), p.second) }
    }
}

class Many0<T>(private val parser: Parser<T>) : Parser<List<T>> {
    override fun parse(value: String): Pair<List<T>, String> {
        val accumulator = mutableListOf<T>()
        while (true) {
            val slice = value.slice(accumulator.size..<value.length)
            val token = parser.parse(slice) ?: return accumulator to slice
            accumulator.add(token.first)
        }
    }
}

infix fun <P1, P2, U, V> P1.or(other: P2): Parser<Either<out U, out V>> where P1 : Parser<U>, P2 : Parser<V> =
    OrParser(this, other)

infix fun <U> Parser<U>.orLift(other: Parser<U>): Parser<U> = OrParser(this, other).map { it.lift() }

class OrParser<P1, P2, U, V>(private val p1: Parser<U>, private val p2: Parser<V>) :
    Parser<Either<out U, out V>> where P1 : Parser<U>, P2 : Parser<V> {
    override fun parse(value: String): Pair<Either<out U, out V>, String>? {
        val p = p1.parse(value)?.let { Either.left(it.first) } ?: p2.parse(value)?.let { Either.right(it.first) }
        ?: return null
        return Pair(p, value.slice(1..<value.length))
    }
}

infix fun <V, U> Parser<V>.and(other: Parser<U>) = AndParser(this, other)

class AndParser<P1, P2, U, V>(private val p1: Parser<U>, private val p2: Parser<V>) :
    Parser<Pair<U, V>> where P1 : Parser<U>, P2 : Parser<V> {
    override fun parse(value: String): Pair<Pair<U, V>, String>? =
        p1.parse(value)?.let { pair ->
            val x = pair.first
            val y = p2.parse(pair.second) ?: return null
            return Pair(Pair(x, y.first), y.second)
        }
}

fun <T> many1(parser: Parser<T>): AndParser<Parser<T>, Parser<List<T>>, T, List<T>> = parser and Many0(parser)

fun token(char: Char): Parser<Char> = Parser {
    if (it.firstOrNull() != char) return@Parser null
    it.firstOrNull()?.let { c -> Pair(c, it.slice(1..<it.length)) }
}