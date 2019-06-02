package db.mmethods.nonlinear.equation.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.equation.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.equation.function.allExtremaValues
import db.mmethods.nonlinear.equation.utils.DoubleRange
import db.mmethods.nonlinear.equation.utils.indexed
import db.mmethods.nonlinear.equation.utils.middle
import db.mmethods.nonlinear.equation.utils.min
import db.mmethods.nonlinear.equation.utils.nonZeroAndHasSameSign
import kotlin.math.absoluteValue
import kotlin.math.sign

object NewtonMethod : IterationMethod {
    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (!fn.differential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid()
        if (!fn.secondDifferential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE_SECOND_DIFFERENTIAL_INVALID.invalid()
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()
        if (checkIfRangeIsSmall(range, eps, fn)) return listOf(range.start, range.endInclusive).minBy(fn)!!.indexed(0).valid()

        val x0 = range.middle()
        val secondDifferentialSign = fn.secondDifferential(range.start).sign

        val predicate = if (fn(x0).sign == secondDifferentialSign) {
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