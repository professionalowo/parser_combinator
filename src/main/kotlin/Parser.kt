package com.professionalowo

fun interface Parser<T> {
    fun parse(value: String): Result<Pair<T, String>>
    fun <D> map(mapper: (T) -> D): Parser<D> = Parser {
        parse(it).map { p -> Pair(mapper(p.first), p.second) }
    }
}

class Many0<T>(private val parser: Parser<T>) : Parser<List<T>> {
    override fun parse(value: String): Result<Pair<List<T>, String>> {
        val accumulator = mutableListOf<T>()
        while (true) {
            val slice = value.slice(accumulator.size..<value.length)
            val token = parser.parse(slice)
            if (token.isFailure)
                return Result.success(accumulator to slice)
            accumulator.add(token.getOrNull()!!.first)
        }
    }
}

infix fun <P1, P2, U, V> P1.or(other: P2): Parser<Either<out U, out V>> where P1 : Parser<U>, P2 : Parser<V> =
    OrParser(this, other)

infix fun <U> Parser<U>.orLift(other: Parser<U>): Parser<U> = OrParser(this, other).map { it.lift() }

class OrParser<P1, P2, U, V>(private val p1: Parser<U>, private val p2: Parser<V>) :
    Parser<Either<out U, out V>> where P1 : Parser<U>, P2 : Parser<V> {
    override fun parse(value: String): Result<Pair<Either<out U, out V>, String>> {
        val res1 = p1.parse(value).map { Either.left<U, V>(it.first) }
        val res2 = p2.parse(value).map { Either.right<U, V>(it.first) }
        if (res1.isFailure && res2.isFailure) return Result.failure(Exception("Parsing failed."))

        val res3 = res1.getOrElse { res2.getOrThrow() }

        return Result.success(Pair(res3, value.slice(1..<value.length)))
    }
}

infix fun <V, U> Parser<V>.and(other: Parser<U>) = AndParser(this, other)

class AndParser<P1, P2, U, V>(private val p1: Parser<U>, private val p2: Parser<V>) :
    Parser<Pair<U, V>> where P1 : Parser<U>, P2 : Parser<V> {
    override fun parse(value: String): Result<Pair<Pair<U, V>, String>> =
        p1.parse(value).map { pair ->
            val x = pair.first
            val result = p2.parse(pair.second)
            if (result.isFailure) return Result.failure(Exception("Parsing failed."))
            val y = result.getOrThrow()
            return Result.success(Pair(Pair(x, y.first), y.second))
        }
}

fun <T> many1(parser: Parser<T>): AndParser<Parser<T>, Parser<List<T>>, T, List<T>> = parser and Many0(parser)

fun token(char: Char): Parser<Char> = Parser {
    it.firstOrNull()?.takeIf { c -> c == char }?.let { c -> Result.success(Pair(c, it.slice(1..<it.length))) }
        ?: Result.failure(Exception("Could not find $char"))
}