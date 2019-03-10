package db.mmethods.nonlinear.utils

import java.util.Comparator

object DoubleComparator : Comparator<Double> {
    override fun compare(o1: Double, o2: Double): Int = o1.compareTo(o2)
}