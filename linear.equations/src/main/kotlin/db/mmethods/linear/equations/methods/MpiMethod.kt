package db.mmethods.linear.equations.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.linear.equations.matrix.Matrix
import db.mmethods.linear.equations.matrix.Vector
import db.mmethods.linear.equations.matrix.minus
import db.mmethods.linear.equations.matrix.plus
import db.mmethods.linear.equations.matrix.times
import db.mmethods.linear.equations.methods.SystemOfLinearEquationsSolverMethod.*
import kotlin.math.absoluteValue

object MpiMethod : SystemOfLinearEquationsSolverMethod {
    override fun apply(matrix: Matrix, eps: Double): Validated<Errors, IndexedValue<Vector>> {
        if (!diagonallyDominated(matrix)) return Errors.NOT_DIAGONALLY_DOMINATED.invalid()
        val b = Array(matrix.rows) { DoubleArray(matrix.columns - 1) }
        val g = DoubleArray(matrix.rows) { i -> matrix[i].last() / matrix[i][i] }
        for (i in b.indices) {
            for (j in b[i].indices) {
                if (i == j) b[i][j] = 0.0
                else b[i][j] = -matrix[i][j] / matrix[i][i]
            }
        }

        val q = b.map { it.map { it.absoluteValue }.sum() }.max()!!
        val `q div (1-q)` = q / (1.0 - q)

        return generateSequence(g) { x -> b * x + g }
            .withIndex()
            .zipWithNext()
            .first { (x1, x2) -> `q div (1-q)` * (x2.value - x1.value).max()!!.absoluteValue < eps }.second
            .valid()
    }

    private fun diagonallyDominated(matrix: Matrix): Boolean {
        for (i in matrix.values.indices) {
            for (j in 0 until (matrix[i].size - 1)) {
                if (i != j && matrix[i][j] > matrix[i][i]) {
                    return false
                }
            }
        }
        return true
    }
}