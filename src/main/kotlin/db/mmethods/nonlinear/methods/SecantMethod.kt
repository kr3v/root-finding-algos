package db.mmethods.nonlinear.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.allExtremaValues
import db.mmethods.nonlinear.utils.*
import kotlin.math.absoluteValue
import kotlin.math.sign

object SecantMethod : IterationMethod {

    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (!fn.differential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (!fn.secondDifferential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE.invalid()
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()
        if (checkIfRangeIsSmall(range, eps, fn)) return range.middle().indexed(0).valid()

        val x0 = listOf(range.start, range.middle(), range.endInclusive).minBy(fn)!!
        val x1 = if (fn(x0).sign == fn(range.endInclusive).sign) range.start else range.endInclusive

        val fnDifferentialMin = fn.differential.allExtremaValues(range).map { it.absoluteValue }.min
        val result = generateSequence(x0, phi(fn, x1))
            .withIndex()
            .first { (_, x) -> fn(x).absoluteValue / fnDifferentialMin < eps || x !in range }

        return if (result.value in range) result.valid()
        else Errors.DIVERGES.invalid()
    }

    private fun phi(fn: DifferentiableTwiceFunction, c: Double) = { x: Double -> x - (x - c) * fn(x) / (fn(x) - fn(c)) }

}