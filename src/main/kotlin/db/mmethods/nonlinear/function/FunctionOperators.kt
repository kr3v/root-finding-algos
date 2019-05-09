package db.mmethods.nonlinear.function

import arrow.core.andThen
import db.mmethods.nonlinear.utils.DoubleComparator
import kotlin.math.absoluteValue
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction as Fn

operator fun Function.plus(c: Double) = Function(fn.andThen { it + c }, NotImplementedRangeToBoolean(), grows)
operator fun Fn.plus(c: Double) = copy(function = function + c)

operator fun Function.plus(f: Function) =
    Function({ fn(it) + f(it) }, NotImplementedRangeToBoolean(), NotImplementedRangeToBoolean())
operator fun Fn.plus(f: Fn) = copy(
    function = function + f.function,
    differential = differential + f.differential,
    secondDifferential = secondDifferential + f.secondDifferential
)

operator fun Function.minus(c: Double) = Function(fn.andThen { it - c }, NotImplementedRangeToBoolean(), grows)
operator fun Fn.minus(c: Double) = copy(function = function - c)

operator fun Function.unaryMinus() = copy(fn = fn.andThen { -it })
operator fun Fn.unaryMinus() = Fn(
    function = -function,
    differential = -differential,
    secondDifferential = -secondDifferential
)

operator fun Function.times(t: Double): Function = when {
    t > 0 -> copy(fn = fn.andThen { t * it })
    t.absoluteValue < DoubleComparator.EPS -> zero
    else -> unaryMinus() * t.absoluteValue
}
operator fun Fn.times(t: Double): Fn = Fn(function * t, differential * t, secondDifferential * t)

fun const(c: Double) = Function({ c }, SimpleRangeToBoolean(), SimpleRangeToBoolean())
fun constFn(c: Double): Fn = Fn(const(c), zero, zero)

val zero = const(0.0)
val zeroFn = constFn(0.0)

val x = Fn(Function({ it }, SimpleRangeToBoolean(0.0), SimpleRangeToBoolean(0.0)), const(1.0), const(0.0))