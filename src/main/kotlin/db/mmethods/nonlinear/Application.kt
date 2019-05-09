package db.mmethods.nonlinear

import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.EmptyRangeToBoolean
import db.mmethods.nonlinear.function.Function
import db.mmethods.nonlinear.methods.FixedPointMethod
import db.mmethods.nonlinear.methods.NewtonMethod

fun main(args: Array<String>) {
    println(
        NewtonMethod.apply(
            DifferentiableTwiceFunction(
                Function({ x -> x * x + 3.0 * x - 4.0 }),
                Function({ x -> 2.0 * x + 3.0 }),
                Function({ x -> 2.0 }, EmptyRangeToBoolean())
            ),
            -12.0..-1.51,
            1e-3
        )
    )
}