package db.mmethods.nonlinear.equation.function

import db.mmethods.nonlinear.equation.utils.DoubleRange

typealias DoubleToDouble = (Double) -> Double
typealias DoubleRangeToBoolean = RangeToBoolean<Double>

data class Function(
    val fn: DoubleToDouble,
    val signs: DoubleRangeToBoolean = NotImplementedRangeToBoolean(),
    val grows: DoubleRangeToBoolean = NotImplementedRangeToBoolean()
) : (Double) -> Double by fn

data class DifferentiableTwiceFunction private constructor(
    val function: Function,
    val differential: Function,
    val secondDifferential: Function
) : DoubleToDouble by function {
    companion object {
        operator fun invoke(function: Function, differential: Function, secondDifferential: Function) =
            DifferentiableTwiceFunction(
                function.copy(grows = differential.signs),
                differential.copy(grows = secondDifferential.signs),
                secondDifferential
            )
    }
}

fun Function.allExtremaValues(range: DoubleRange) =
    (sequenceOf(range.start) + grows.pointsIn(range) + sequenceOf(range.endInclusive)).map(this)