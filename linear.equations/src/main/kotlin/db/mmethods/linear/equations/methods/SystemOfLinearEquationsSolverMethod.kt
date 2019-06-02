package db.mmethods.linear.equations.methods

import arrow.data.Validated
import db.mmethods.linear.equations.matrix.Matrix
import db.mmethods.linear.equations.matrix.Vector

interface SystemOfLinearEquationsSolverMethod {
    fun apply(matrix: Matrix, eps: Double): Validated<Errors, IndexedValue<Vector>>

    enum class Errors {
        DETERMINANT_IS_ZERO,
        NOT_DIAGONALLY_DOMINATED
    }
}
