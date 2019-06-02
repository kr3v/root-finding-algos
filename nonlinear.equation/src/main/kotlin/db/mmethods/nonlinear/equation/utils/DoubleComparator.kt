package db.mmethods.nonlinear.equation.utils

import java.util.Comparator
import kotlin.math.absoluteValue

object DoubleComparator : Comparator<Double> {
    const val EPS = 1e-8

    override fun compare(o1: Double, o2: Double): Int = o1.compareTo(o2)
}

fun Double.isNotZero() = absoluteValue > DoubleComparator.EPS