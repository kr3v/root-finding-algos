package db.mmethods.nonlinear.methods

import arrow.data.Validated
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.utils.DoubleRange
import kotlin.math.sign

interface IterationMethod {
    fun apply(fn: DifferentiableTwiceFunction, range: DoubleRange, eps: Double): Validated<Errors, IndexedValue<Double>>

    fun checkIfRangeIsSmall(range: DoubleRange, eps: Double, fn: DifferentiableTwiceFunction) =
        range.endInclusive - range.start < eps ||
            fn(range.endInclusive - eps).sign == fn(range.start).sign ||
            fn(range.start + eps).sign == fn(range.endInclusive).sign
}