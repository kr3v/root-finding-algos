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
                Function({ x -> x * x + 3.0 * x - 4.0 }),
                Function({ x -> 2.0 * x + 3.0 }),
                Function({ x -> 2.0 }, EmptyRangeToBoolean(), EmptyRangeToBoolean())
            ),
            -100.0..-1.501,
            1e-6
        )
    )
}