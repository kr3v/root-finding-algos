package db.mmethods.nonlinear.equation.function

import db.mmethods.nonlinear.equation.utils.DoubleComparator
import db.mmethods.nonlinear.equation.utils.DoubleRange
import java.util.*

interface RangeToBoolean<T : Comparable<T>> {
    operator fun contains(range: ClosedRange<T>): Boolean
    fun pointsIn(range: ClosedRange<T>): Sequence<T>
}

class NotImplementedRangeToBoolean<T : Comparable<T>> : RangeToBoolean<T> {
    override fun contains(range: ClosedRange<T>) = throw UnsupportedOperationException()
    override fun pointsIn(range: ClosedRange<T>) = throw UnsupportedOperationException()
}

class EmptyRangeToBoolean<T : Comparable<T>> : RangeToBoolean<T> {
    override fun contains(range: ClosedRange<T>) = false
    override fun pointsIn(range: ClosedRange<T>) = emptySequence<T>()
}

data class SimpleRangeToBoolean<T : Comparable<T>>(
    val points: NavigableSet<T>,
    val comparator: Comparator<T>
) : RangeToBoolean<T> {

    override fun contains(range: ClosedRange<T>) =
        points.floor(range.start) == points.floor(range.endInclusive) &&
            points.ceiling(range.start) == points.ceiling(range.endInclusive)

    override fun pointsIn(range: ClosedRange<T>) = points.subSet(range.start, range.endInclusive).asSequence()

    companion object {
        operator fun invoke(vararg doubles: Double) =
            SimpleRangeToBoolean(TreeSet(doubles.toList()), DoubleComparator)
    }
}

data class PeriodicRangeToBoolean(
    val range: DoubleRange,
    val comparator: Comparator<Double> = DoubleComparator,
    val period: Double = range.endInclusive - range.start
) : RangeToBoolean<Double> {

    override fun contains(range: ClosedRange<Double>) = eval(range.start) == eval(range.endInclusive)

    override fun pointsIn(range: ClosedRange<Double>) = sequence {
        val left = eval(range.start)
        val right = eval(range.endInclusive)
        var start = range.start + (left + 1) * period
        val end = range.start + right * period
        while (start < end) {
            yield(start)
            start += period
        }
    }

    private fun eval(it: Double) = when {
        it in range -> 0
        it < range.start -> ((range.start - it) / period).toInt() + 1
        else -> ((range.endInclusive - it) / period).toInt() - 1
    }
}