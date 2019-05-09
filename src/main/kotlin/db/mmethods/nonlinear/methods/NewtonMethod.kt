package db.mmethods.nonlinear.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.allExtremaValues
import db.mmethods.nonlinear.utils.*
import kotlin.math.absoluteValue
import kotlin.math.sign

object NewtonMethod : IterationMethod {
    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (!fn.differential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (!fn.secondDifferential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()
        if (checkIfRangeIsSmall(range, eps, fn)) return range.middle().indexed(0).valid()

        val x0 = range.middle()
        val secondDifferentialSign = fn.secondDifferential(range.start).sign

        val predicate = if (fn(x0) == secondDifferentialSign) {
            val minFnDifferential = fn.differential.allExtremaValues(range).map { it.absoluteValue }.min
            { _: Double, x2: Double -> fn(x2).absoluteValue / minFnDifferential < eps }
        } else {
            { x1: Double, x2: Double -> (x2 - x1).absoluteValue < eps }
        }

        val result = generateSequence(x0, phi(fn))
            .withIndex()
            .zipWithNext()
            .first { (x1, x2) -> predicate(x1.value, x2.value) || x2.value !in range }.second

        return if (result.value in range) result.valid()
        else Errors.DIVERGES.invalid()
    }

    private fun phi(fn: DifferentiableTwiceFunction) = { x: Double -> x - fn(x) / fn.differential(x) }
}