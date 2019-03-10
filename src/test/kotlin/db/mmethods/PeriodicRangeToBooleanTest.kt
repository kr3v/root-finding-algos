package db.mmethods

import db.mmethods.function.PeriodicRangeToBoolean
import db.mmethods.function.Value.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.text.DecimalFormat
import java.util.stream.Stream
import kotlin.math.PI

class PeriodicRangeToBooleanTest {
    @TestFactory
    fun testInvoke(): Stream<DynamicTest> {
        val df = DecimalFormat("#.#####")

        val periodic = PeriodicRangeToBoolean(0.0..PI, POSITIVE)
        return Stream.of(
            -1e-5 to NEGATIVE,
            1e-5 to POSITIVE,
            PI / 3 to POSITIVE,
            PI / 2 to POSITIVE,
            PI * 4 / 3 to NEGATIVE,
            PI * 5 / 3 to NEGATIVE,
            2 * PI - 1e-5 to NEGATIVE,
            2 * PI + 1e-5 to POSITIVE
        ).flatMap { (double, value) ->
            (-100..100).map { (double + 2 * PI * it) to value }.stream()
        }.map { (double, value) ->
            dynamicTest("test ${df.format(double / PI)} * PI") {
                assertThat(periodic(double)).isEqualTo(value)
            }
        }
    }
}