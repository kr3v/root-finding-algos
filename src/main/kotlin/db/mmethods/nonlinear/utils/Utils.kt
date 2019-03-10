package db.mmethods.nonlinear.utils

import java.util.*

typealias DoubleRange = ClosedFloatingPointRange<Double>

fun <K : Comparable<K>, V> List<Pair<K, V>>.toTreeMap(comparator: Comparator<K>) =
    TreeMap(toMap().toSortedMap(comparator))