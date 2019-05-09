package db.mmethods.nonlinear

import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.EmptyRangeToBoolean
import db.mmethods.nonlinear.function.Function
import db.mmethods.nonlinear.function.SimpleRangeToBoolean
import db.mmethods.nonlinear.methods.SecantMethod

fun main(args: Array<String>) {
    println(
        SecantMethod.apply(
            DifferentiableTwiceFunction(
                Function({ x -> 2.0 * x * x * x - (6.0 + Math.sqrt(2.0)) * x * x + (3.0 * Math.sqrt(2.0) - 2.0) * x + 6 }),
                Function({ x -> 6.0 * x * x - 2.0 * (6.0 + Math.sqrt(2.0)) * x + (3.0 * Math.sqrt(2.0) - 2.0) }),
                Function(
                    { x -> 12.0 * x - 2.0 * (6.0 + Math.sqrt(2.0)) },
                    SimpleRangeToBoolean(1.0 + Math.sqrt(2.0) / 6.0),
                    EmptyRangeToBoolean()
                )
            ),
            2.4..4.0,
            1e-6
        )
    )
}