package db.mmethods.utils

import java.util.Comparator
import kotlin.math.absoluteValue

object DoubleComparator : Comparator<Double> {
    override fun compare(o1: Double, o2: Double): Int = o1.compareTo(o2)
}