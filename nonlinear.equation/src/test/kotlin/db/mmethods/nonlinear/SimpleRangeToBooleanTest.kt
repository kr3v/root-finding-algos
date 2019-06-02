package db.mmethods.nonlinear

import db.mmethods.nonlinear.equation.function.SimpleRangeToBoolean
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

class SimpleRangeToBooleanTest {
    @TestFactory
    fun testInvoke(): Stream<DynamicTest> {
        val plain = SimpleRangeToBoolean(0.0, 1.0, 10.0)
        return Stream.of(
            -0.1..-1e-5 to true,
            -1e-5..1e-2 to false,
            1e-1..9e-1 to true,
            1e-1..1.1 to false,
            (1 + 1e-2)..2.0 to true,
            3.0..5.0 to true,
            (10.0 - 1e-4)..(10.0 + 1e-4) to false,
            (10.0 - 1e-2)..(Double.POSITIVE_INFINITY) to false,
            (10.0 + 1e-2)..(Double.POSITIVE_INFINITY) to true
        ).map { (range, value) ->
            dynamicTest("test $range") {
                assertThat(range in plain).isEqualTo(value)
            }
        }
    }
}