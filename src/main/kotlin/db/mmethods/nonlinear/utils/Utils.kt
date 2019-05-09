package db.mmethods.nonlinear.utils

import java.util.*
import kotlin.math.sign

typealias DoubleRange = ClosedFloatingPointRange<Double>

val Sequence<Double>.min: Double get() = min()!!
val Sequence<Double>.max: Double get() = max()!!

fun Sequence<Double>.nonZeroAndHasSameSign() =
    zipWithNext().all { (a, b) -> a.sign * b.sign > 0 && a.isNotZero() && b.isNotZero() }

fun <K : Comparable<K>, V> List<Pair<K, V>>.toTreeMap(comparator: Comparator<K>) =
    TreeMap(toMap().toSortedMap(comparator))