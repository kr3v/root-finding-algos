package db.mmethods.nonlinear

import db.mmethods.nonlinear.function.SimpleRangeToBoolean
import db.mmethods.nonlinear.function.Value.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

class SimpleRangeToBooleanTest {
    @TestFactory
    fun testInvoke(): Stream<DynamicTest> {
        val plain = SimpleRangeToBoolean(listOf(0.0, 1.0, 10.0), NEGATIVE)
        return Stream.of(
            Double.NEGATIVE_INFINITY to NEGATIVE,
            -100.0 to NEGATIVE,
            -10.0 to NEGATIVE,
            -1.0 to NEGATIVE,
            -0.1 to NEGATIVE,
            -1e-5 to NEGATIVE,
            1e-5 to POSITIVE,
            1e-2 to POSITIVE,
            1e-1 to POSITIVE,
            9e-1 to POSITIVE,
            1 - 1e-5 to POSITIVE,
            1 + 1e-5 to NEGATIVE,
            1 + 1e-2 to NEGATIVE,
            2.0 to NEGATIVE,
            3.0 to NEGATIVE,
            5.0 to NEGATIVE,
            10.0 - 1e-4 to NEGATIVE,
            10.0 + 1e-4 to POSITIVE,
            10.0 + 1e-2 to POSITIVE,
            Double.POSITIVE_INFINITY to POSITIVE
        ).map { (double, value) ->
            dynamicTest("test $double") {
                assertThat(plain(double)).isEqualTo(value)
            }
        }
    }

    @TestFactory
    fun testBuilder(): Stream<DynamicTest> = Stream.of(
        Triple(NEGATIVE, listOf(-1.0, 0.0, 1.0), mapOf(-1.0 to NEGATIVE, 0.0 to POSITIVE, 1.0 to NEGATIVE)),
        Triple(POSITIVE, listOf(-1.0, 0.0, 1.0), mapOf(-1.0 to POSITIVE, 0.0 to NEGATIVE, 1.0 to POSITIVE)),
        Triple(NEGATIVE, listOf(-1.0, 1.0), mapOf(-1.0 to NEGATIVE, 1.0 to POSITIVE))
    ).map { (startsWith, definePoints, result) ->
        dynamicTest("test ($startsWith, $definePoints)") {
            assertThat(SimpleRangeToBoolean(definePoints, startsWith).definingPoints).isEqualTo(result)
        }
    }
}