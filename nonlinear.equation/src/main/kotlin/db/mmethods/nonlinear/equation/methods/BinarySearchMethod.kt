package db.mmethods.nonlinear.equation.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.nonlinear.equation.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.equation.utils.DoubleRange
import kotlin.math.absoluteValue
import kotlin.math.sign

object BinarySearchMethod : IterationMethod {
    override fun apply(
        fn: DifferentiableTwiceFunction,
        range: DoubleRange,
        eps: Double
    ): Validated<Errors, IndexedValue<Double>> {
        if (fn(range.start) * fn(range.endInclusive) > 0) return Errors.NO_ROOT.invalid()

        var l = range.start
        var r = range.endInclusive
        var m = (l + r) / 2.0
        var fnL = fn(l)
        var fnR = fn(r)
        var fnM = fn(m)

        var iterations = 1
        while ((r - l).absoluteValue > eps) {
            if (fnM.sign != fnR.sign) {
                l = m
                fnL = fnM
            } else if (fnM.sign != fnL.sign) {
                r = m
                fnR = fnM
            }
            m = (l + r) / 2.0
            fnM = fn(m)
            iterations++
        }
        return IndexedValue(iterations, m).valid()
    }
}