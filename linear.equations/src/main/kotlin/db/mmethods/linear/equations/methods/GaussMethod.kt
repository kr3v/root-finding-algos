package db.mmethods.linear.equations.methods

import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import db.mmethods.linear.equations.matrix.Matrix
import db.mmethods.linear.equations.matrix.Vector
import db.mmethods.linear.equations.matrix.divAssign
import db.mmethods.linear.equations.matrix.minusAssign
import db.mmethods.linear.equations.matrix.times
import db.mmethods.linear.equations.methods.SystemOfLinearEquationsSolverMethod.*
import kotlin.math.absoluteValue

object GaussMethod: SystemOfLinearEquationsSolverMethod {

    override fun apply(matrix: Matrix, eps: Double): Validated<Errors, IndexedValue<Vector>> {
        if (matrix.determinant.absoluteValue <= eps) return Errors.DETERMINANT_IS_ZERO.invalid()
        direct(matrix)
        return IndexedValue(0, matrix.reverseMove(inverse(matrix))).valid()
    }

    private fun inverse(matrix: Matrix): DoubleArray {
        val ans = DoubleArray(matrix.rows)
        for (i in matrix.values.indices.reversed()) {
            val otherCoefficients = ((i + 1) until matrix.rows).sumByDouble { j -> matrix[i][j] * ans[j] }
            ans[i] = matrix[i].last() - otherCoefficients
        }
        return ans
    }

    private fun direct(matrix: Matrix) {
        for (i in matrix.values.indices) {
            matrix.swapColumns(i, matrix[i].indexOf(matrix[i].dropLast(1).maxBy { it.absoluteValue }!!))
            matrix[i] /= matrix[i][i]
            for (j in (i + 1) until matrix.values.size) {
                matrix[j] -= matrix[i] * matrix[j][i]
            }
        }
    }
}