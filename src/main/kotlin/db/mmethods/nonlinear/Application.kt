package db.mmethods.nonlinear

import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.EmptyRangeToBoolean
import db.mmethods.nonlinear.function.Function
import db.mmethods.nonlinear.methods.MPI

fun main(args: Array<String>) {
    print(
        MPI.apply(
            DifferentiableTwiceFunction(
                Function({ x -> x * x + 3.0 * x - 4.0 }),
                Function({ x -> 2.0 * x + 3.0 }),
                Function(
                    { x -> 2.0 },
                    EmptyRangeToBoolean()
                )
            ),
            -8.0..-1.51,
            1e-3
        )
    )
}