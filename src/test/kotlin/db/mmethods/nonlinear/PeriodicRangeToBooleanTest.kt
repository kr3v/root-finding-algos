package db.mmethods.nonlinear

import db.mmethods.nonlinear.function.PeriodicRangeToBoolean
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.text.DecimalFormat
import java.util.stream.Stream
import kotlin.math.PI

class PeriodicRangeToBooleanTest {
    private val df = DecimalFormat("#.#####")
    private val periodic = PeriodicRangeToBoolean(0.0..PI)

    @TestFactory
    fun testInvoke(): Stream<DynamicTest> = Stream.of(
        Triple(-1e-3, 1e-3, false),
        Triple(1e-3, 1e-2, true),
        Triple(PI / 3, PI / 2, true),
        Triple(0.9 * PI, 1.1 * PI, false),
        Triple(PI * 4 / 3, PI * 5 / 3, true),
        Triple(-PI * 1 / 3, PI * 4 / 3, false),
        Triple(2 * PI - 1e-2, 2 * PI + 1e-3, false)
    ).flatMap { (a, b, value) ->
        (-100..100).map { Triple(a + 2 * PI * it, b + 2 * PI * it, value) }.stream()
    }.map { (a, b, value) ->
        dynamicTest("test (${df.format(a / PI)}, ${df.format(b / PI)}) * PI") {
            assertThat(a..b in periodic).isEqualTo(value)
        }
    }
}