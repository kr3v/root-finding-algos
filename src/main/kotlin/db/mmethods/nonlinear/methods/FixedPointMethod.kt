package db.mmethods.nonlinear.methods

import arrow.data.Invalid
import arrow.data.Validated
import arrow.data.valid
import db.mmethods.nonlinear.function.*
import db.mmethods.nonlinear.utils.DoubleRange
import db.mmethods.nonlinear.utils.max
import db.mmethods.nonlinear.utils.min
import db.mmethods.nonlinear.utils.nonZeroAndHasSameSign
import kotlin.math.absoluteValue

object FixedPointMethod : IterationMethod {
    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        val seq = fn.differential.allExtremaValues(range)
        if (!seq.nonZeroAndHasSameSign()) {
            return Invalid(Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID)
        }
        if (fn(range.start) * fn(range.endInclusive) > 0) {
            return Invalid(Errors.NO_ROOT)
        }

        // fn.differential must keep sign => both seq.max and seq.min have same sign as fn.differential => next lines are equivalent
        // val k = -2.0 / (seq.map { it.absoluteValue }.max + seq.map { it.absoluteValue }.min) * fn.differential(range.start).sign
        val k = -2.0 / (seq.max + seq.min)

        val phi = xFn + fn * k
        val q = (fn.differential * k + 1.0).allExtremaValues(range).map { it.absoluteValue }.max
        val `q div (1-q)` = q / (1.0 - q)

        return generateSequence((range.start + range.endInclusive) / 2.0, phi)
            .withIndex()
            .zipWithNext()
            .first { (x1, x2) -> `q div (1-q)` * (x2.value - x1.value).absoluteValue < eps }.second
            .valid()
    }
}

