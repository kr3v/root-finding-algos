package db.mmethods.nonlinear.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.allExtremaValues
import db.mmethods.nonlinear.utils.*
import kotlin.math.absoluteValue
import kotlin.math.sign

object CombinedNewtonAndSecantMethod : IterationMethod {
    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (!fn.differential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (!fn.secondDifferential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()
        if (checkIfRangeIsSmall(range, eps, fn)) return range.middle().indexed(0).valid()

        val sign = fn.differential(range.start).sign == fn.secondDifferential(range.start).sign
        val (xLeft, xRight) = splitByFour(range)
            .partition { fn(it).sign < 0 }
            .let { (negative, positive) ->
                val x0 = negative.maxBy(fn)!!
                val x1 = positive.minBy(fn)!!
                if (sign) x0 to x1 else x1 to x0
            }

        val (f1, f2) = if (sign) {
            val f1 = { x1: Double, x2: Double -> x1 - fn(x1) * (x2 - x1) / (fn(x2) - fn(x1)) }
            val f2 = { x1: Double, x2: Double -> x2 - fn(x2) / fn.differential(x2) }
            f1 to f2
        } else {
            val f1 = { x1: Double, x2: Double -> x1 - fn(x1) / fn.differential(x1) }
            val f2 = { x1: Double, x2: Double -> x2 - fn(x2) * (x2 - x1) / (fn(x2) - fn(x1)) }
            f1 to f2
        }

        val f: (Pair<Double, Double>) -> Pair<Double, Double> = { (x1, x2) -> f1(x1, x2) to f2(x1, x2) }
        val (idx, pair) = generateSequence(xLeft to xRight, f)
            .withIndex()
            .first {
                val x1 = it.value.first
                val x2 = it.value.second
                (x2 - x1).absoluteValue < eps || x1 !in range || x2 !in range
            }
        val (x1, x2) = pair

        return if (x1 !in range || x2 !in range) Errors.DIVERGES.invalid()
        else IndexedValue(idx, (x1 + x2) / 2.0).valid()
    }
}