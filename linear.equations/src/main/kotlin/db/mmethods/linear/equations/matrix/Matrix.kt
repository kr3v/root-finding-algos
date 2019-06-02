package db.mmethods.linear.equations.matrix

import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils

class Matrix(
    val rows: Int,
    val columns: Int,
    val values: Array<Vector> = Array(rows) { Vector(columns) }
) {
    operator fun get(i: Int): Vector = values[i]
    operator fun get(i: Int, j: Int): Double = values[i][j]
    override fun toString(): String = values.asString()
    val determinant
        get() = values.map { it.dropLast(1).toDoubleArray() }.toTypedArray()
            .let(MatrixUtils::createRealMatrix)
            .let(::LUDecomposition)
            .determinant

    private val swaps: MutableList<Pair<Int, Int>> = mutableListOf()

    fun swapColumns(i: Int, j: Int) {
        for (k in 0 until rows) {
            swap(values[k], i, j)
        }
        swaps += i to j
    }

    private fun swap(arr: DoubleArray, i: Int, j: Int) {
        val temp = arr[i]
        arr[i] = arr[j]
        arr[j] = temp
    }

    fun reverseMove(v: Vector): Vector {
        swaps.reversed().forEach { (i, j) -> swap(v, i, j) }
        return v
    }
}
