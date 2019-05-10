package db.mmethods.nonlinear.methods

import arrow.data.Valid
import arrow.data.Validated
import arrow.data.invalid
import db.mmethods.nonlinear.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.function.EmptyRangeToBoolean
import db.mmethods.nonlinear.function.Function
import db.mmethods.nonlinear.function.SimpleRangeToBoolean
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.util.stream.Stream
import kotlin.math.absoluteValue

class MethodsTest {

    private val eps = (-8..-2).map { pow(10.0, it.toDouble()) }
    private val methods: List<IterationMethod> = listOf(
        NewtonMethod,
        FixedPointMethod,
        SecantMethod,
        BinarySearchMethod,
        CombinedNewtonAndSecantMethod
    )

    @TestFactory
    fun quadraticFunction(): Stream<DynamicTest> {
        val function = DifferentiableTwiceFunction(
            Function({ x -> x * x + 3.0 * x - 4.0 }),
            Function({ x -> 2.0 * x + 3.0 }),
            Function({ x -> 2.0 }, EmptyRangeToBoolean(), EmptyRangeToBoolean())
        )
        val roots = listOf(-4.0, 1.0)
        val tests = listOf(
            -4.5..1.5 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            -4.5..-1.51 to Valid(-4.0),
            -4.5..-1.49 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            -4.6..-3.6 to Valid(-4.0),
            -6.6..-2.6 to Valid(-4.0),
            -8.0..-1.5 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            -8.0..-1.51 to Valid(-4.0),
            -8.0..-1.501 to Valid(-4.0),
            -100.0..-1.501 to Valid(-4.0),
            -8.0..-1.6 to Valid(-4.0),
            -3.5..-2.5 to Errors.NO_ROOT.invalid(),
            -3.9..-2.0 to Errors.NO_ROOT.invalid(),
            -8.0..-5.0 to Errors.NO_ROOT.invalid(),
            -8.0..-4.01 to Errors.NO_ROOT.invalid(),
            -8.0..-1.0 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            -1.5..2.0 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            -1.51..2.0 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            -1.49..2.0 to Valid(1.0),
            -1.01..2.0 to Valid(1.0),
            0.0..2.0 to Valid(1.0),
            0.0..10.0 to Valid(1.0),
            0.0..20.0 to Valid(1.0),
            0.9..20.0 to Valid(1.0),
            0.9..1.1 to Valid(1.0)
        )
        return test(tests, function)
    }

    @TestFactory
    fun cubicFunction(): Stream<DynamicTest> {
        val function = DifferentiableTwiceFunction(
            Function({ x -> 2.0 * x * x * x - (6.0 + sqrt(2.0)) * x * x + (3.0 * sqrt(2.0) - 2.0) * x + 6 }),
            Function({ x -> 6.0 * x * x - 2.0 * (6.0 + sqrt(2.0)) * x + (3.0 * sqrt(2.0) - 2.0) }),
            Function(
                { x -> 12.0 * x - 2.0 * (6.0 + sqrt(2.0)) },
                SimpleRangeToBoolean(1.0 + sqrt(2.0) / 6.0),
                EmptyRangeToBoolean()
            )
        )
        val roots = listOf(-1.0 / sqrt(2.0), sqrt(2.0), 3.0)
        val tests = listOf(
            2.4..2.9 to Errors.NO_ROOT.invalid(),
            2.1..3.1 to Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid(),
            2.4..3.1 to Valid(3.0),
            2.4..4.0 to Valid(3.0),
            2.4..31.0 to Valid(3.0),
            2.4..310.0 to Valid(3.0),
            2.9..3.1 to Valid(3.0),
            2.9..31.0 to Valid(3.0),
            2.9..310.0 to Valid(3.0)
        )
        return test(tests, function)
    }

    @TestFactory
    fun squareRootFunction(): Stream<DynamicTest> {
        val function = DifferentiableTwiceFunction(
            Function({ x -> 2.0 * sqrt(x) - 32.0 }),
            Function({ x -> pow(x, -1.0 / 2.0) }),
            Function({ x -> -1.0 / 2.0 * pow(x, -3.0 / 2.0) }, EmptyRangeToBoolean(), EmptyRangeToBoolean())
        )
        val roots = listOf(256.0)
        val tests = listOf(
            255.0..255.5 to Errors.NO_ROOT.invalid(),
            255.0..257.0 to Valid(256.0),
            255.0..300.0 to Valid(256.0),
            10.0..256.5 to Valid(256.0),
            245.0..267.0 to Valid(256.0),
            200.0..300.0 to Valid(256.0),
            10.0..1000.0 to Valid(256.0)
        )
        return test(tests, function)
    }

    private fun test(
        tests: List<Pair<ClosedFloatingPointRange<Double>, Validated<Errors, Double>>>,
        function: DifferentiableTwiceFunction
    ): Stream<DynamicTest> =
        eps.map { eps ->
            methods.map { method ->
                tests
                    .filterNot { (_, expected) -> method == BinarySearchMethod && expected == Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID.invalid() }
                    .map { (range, expected) ->
                        val given = method.apply(function, range, eps)
                        val idx = given.fold({ "-" }, { it.index.toString() })
                        val name = method.javaClass.simpleName
                        dynamicTest("($range, $eps) -> $idx on $name (expected = $expected, given = $given)") {
                            dynamicTest(expected, given.map { it.value }, eps)
                        }
                    }
            }.flatten()
        }.flatten().stream()

    private fun dynamicTest(
        expected: Validated<Errors, Double>,
        given: Validated<Errors, Double>,
        eps: Double
    ) = expected.fold(
        { expectedError ->
            given.fold(
                { givenError -> assertThat(expectedError).isEqualTo(givenError) },
                { fail<Unit> { "expected = $expectedError, given=$it" } }
            )
        },
        { expectedSuccess ->
            given.fold(
                { fail<Unit> { "expected = $expectedSuccess, given=$it" } },
                { givenSuccess -> assertThat(expectedSuccess - givenSuccess).matches { it.absoluteValue <= eps } }
            )
        }
    )
}
