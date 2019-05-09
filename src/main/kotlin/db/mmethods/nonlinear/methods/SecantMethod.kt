package db.mmethods.nonlinear.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.allExtremaValues
import db.mmethods.nonlinear.utils.*
import kotlin.math.absoluteValue

object SecantMethod : IterationMethod {

    @Suppress("NAME_SHADOWING")
    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (!fn.differential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (!fn.secondDifferential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()
        if (checkIfRangeIsSmall(range, eps, fn)) return range.middle().indexed(0).valid()

        val middle = range.middle()

        val x0 = (range.start..middle).middle()
        val x1 = (middle..range.endInclusive).middle()

        val predicate = if (fn(x0) == fn(x1)) {
            val minFnDifferential = fn.differential.allExtremaValues(range).map { it.absoluteValue }.min
            { _: Double, x2: Double -> fn(x2).absoluteValue / minFnDifferential < eps }
        } else {
            { x1: Double, x2: Double -> (x2 - x1).absoluteValue < eps }
        }

        val result = generateSequence(x1, phi(fn, x0))
            .withIndex()
            .zipWithNext()
            .first { (x1, x2) -> predicate(x1.value, x2.value) || x2.value !in range }.second

        return if (result.value in range) result.valid()
        else Errors.DIVERGES.invalid()
    }

    private fun phi(fn: DifferentiableTwiceFunction, c: Double) = { x: Double -> x - (x - c) * fn(x) / (fn(x) - fn(c)) }

}