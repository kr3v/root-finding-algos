package db.mmethods.nonlinear.equation.utils

import java.util.*
import kotlin.math.sign

typealias DoubleRange = ClosedFloatingPointRange<Double>

val Sequence<Double>.min: Double get() = min()!!
val Sequence<Double>.max: Double get() = max()!!

fun Sequence<Double>.nonZeroAndHasSameSign() =
    zipWithNext().all { (a, b) -> a.sign * b.sign > 0 && a.isNotZero() && b.isNotZero() }

fun DoubleRange.middle() = (endInclusive + start) / 2.0
fun Double.indexed(idx: Int) = IndexedValue(idx, this)

fun splitByFour(range: DoubleRange): List<Double> {
    return listOf(
        range.start,
        (range.start..range.middle()).middle(),
        range.middle(),
        (range.middle()..range.endInclusive).middle(),
        range.endInclusive
    )
}

fun <K : Comparable<K>, V> List<Pair<K, V>>.toTreeMap(comparator: Comparator<K>) =
    TreeMap(toMap().toSortedMap(comparator))