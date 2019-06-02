package db.mmethods.linear.equations.matrix

typealias Vector = DoubleArray

operator fun Vector.plusAssign(rhs: Vector) = this.mutate(rhs, Double::plus)
operator fun Vector.minusAssign(rhs: Vector) = this.mutate(rhs, Double::minus)
operator fun Vector.timesAssign(rhs: Vector) = this.mutate(rhs, Double::times)
operator fun Vector.divAssign(rhs: Vector) = this.mutate(rhs, Double::div)
operator fun Vector.timesAssign(d: Double) = this.mutate { a -> a * d }
operator fun Vector.divAssign(d: Double) = this.mutate { a -> a / d }
operator fun Vector.plus(rhs: Vector) = this.combine(rhs, Double::plus)
operator fun Vector.minus(rhs: Vector) = this.combine(rhs, Double::minus)
operator fun Vector.times(rhs: Double) = this.apply(rhs::times)
operator fun Vector.div(rhs: Vector) = this.combine(rhs, Double::div)
operator fun Vector.unaryMinus() = this.apply(Double::unaryMinus)

operator fun Array<DoubleArray>.times(v: Vector): Vector {
    val r = Vector(v.size)
    for (i in r.indices) {
        for (j in r.indices) {
            r[i] += this[i][j] * v[j]
        }
    }
    return r
}

private fun Vector.combine(d2: Vector, mutator: (Double, Double) -> Double): Vector =
    zip(d2).map(mutator.paired()).toDoubleArray()

private fun Vector.apply(mutator: (Double) -> Double): Vector =
    map(mutator).toDoubleArray()

private fun Vector.mutate(d2: Vector, mutator: (Double, Double) -> Double) {
    for (i in indices) {
        this[i] = mutator(this[i], d2[i])
    }
}

private fun Vector.mutate(mutator: (Double) -> Double) {
    for (i in indices) {
        this[i] = mutator(this[i])
    }
}

private fun <A, B, C> ((A, B) -> C).paired(): (Pair<A, B>) -> C = { (a, b) -> this(a, b) }