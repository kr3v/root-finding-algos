package db.mmethods.nonlinear.equation.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.equation.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.equation.function.allExtremaValues
import db.mmethods.nonlinear.equation.utils.DoubleRange
import db.mmethods.nonlinear.equation.utils.indexed
import db.mmethods.nonlinear.equation.utils.min
import db.mmethods.nonlinear.equation.utils.nonZeroAndHasSameSign
import db.mmethods.nonlinear.equation.utils.splitByFour
import kotlin.math.absoluteValue
import kotlin.math.sign

object SecantMethod : IterationMethod {

    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (!fn.differential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid()
        if (!fn.secondDifferential.allExtremaValues(range).nonZeroAndHasSameSign()) return Errors.NOT_APPLICABLE_SECOND_DIFFERENTIAL_INVALID.invalid()
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()
        if (checkIfRangeIsSmall(range, eps, fn)) return listOf(range.start, range.endInclusive).minBy(fn)!!.indexed(0).valid()

        val (x0, x1) = initialApproximations(range, fn)

        val fnDifferentialMin = fn.differential.allExtremaValues(range).map { it.absoluteValue }.min
        val result = generateSequence(x0, phi(fn, x1))
            .withIndex()
            .first { (_, x) -> fn(x).absoluteValue / fnDifferentialMin < eps || x !in range }

        return if (result.value in range) result.valid()
        else Errors.DIVERGES.invalid()
    }

    private fun initialApproximations(range: DoubleRange, fn: DifferentiableTwiceFunction): Pair<Double, Double> {
        val l = splitByFour(range)
        val x0 = l.filter { fn(it).sign == fn.secondDifferential(it).sign }.minBy { fn(it).absoluteValue }!!
        val fx0sign = fn(x0).sign
        val x1 = l.filter { fn(it).sign != fx0sign }.minBy { fn(it).absoluteValue }!!
        return Pair(x0, x1)
    }

    private fun phi(fn: DifferentiableTwiceFunction, c: Double) = { x: Double -> x - (x - c) * fn(x) / (fn(x) - fn(c)) }

}