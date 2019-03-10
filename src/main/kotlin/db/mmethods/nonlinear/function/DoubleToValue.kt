package db.mmethods.nonlinear.function

import db.mmethods.nonlinear.function.Value.*
import db.mmethods.nonlinear.utils.DoubleComparator
import db.mmethods.nonlinear.utils.DoubleRange
import db.mmethods.nonlinear.utils.toTreeMap
import java.util.*

enum class Value {
    POSITIVE,
    NEGATIVE,
    ZERO
}

operator fun Value.not() = when (this) {
    ZERO -> ZERO
    POSITIVE -> NEGATIVE
    NEGATIVE -> POSITIVE
}

data class SimpleRangeToBoolean<T : Comparable<T>>(
    val definingPoints: NavigableMap<T, Value>,
    val comparator: Comparator<T>
) : (T) -> Value by {
    val lessOrEqual = definingPoints.ceilingEntry(it)
    when {
        lessOrEqual == null -> definingPoints.lastEntry().value.not()
        comparator.compare(it, lessOrEqual.key) == 0 -> Value.ZERO
        else -> lessOrEqual.value
    }
} {
    companion object {
        operator fun invoke(doubles: List<Double>, value: Value): SimpleRangeToBoolean<Double> =
            SimpleRangeToBoolean(
                doubles.mapIndexed { idx, point -> point to if (idx % 2 == 0) value else value.not() }
                    .toTreeMap(DoubleComparator), DoubleComparator
            )
    }
}

data class PeriodicRangeToBoolean(
    val range: DoubleRange,
    val value: Value,
    val comparator: Comparator<Double> = DoubleComparator
) : DoubleToValue by {
    when {
        it in range -> value
        it < range.start -> eval(
            value,
            comparator,
            (range.start - it) / (range.endInclusive - range.start)
        )
        else -> eval(
            value,
            comparator,
            (it - range.endInclusive) / (range.endInclusive - range.start)
        )
    }
} {
    private companion object {
        fun eval(value: Value, comparator: Comparator<Double>, diff: Double) = when {
            comparator.compare(diff.toInt().toDouble(), diff) == 0 -> Value.ZERO
            diff.toInt() % 2 == 0 -> value.not()
            else -> value
        }
    }
}