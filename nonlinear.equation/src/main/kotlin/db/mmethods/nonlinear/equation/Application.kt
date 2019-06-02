package db.mmethods.nonlinear.equation

import arrow.core.Option
import arrow.core.extensions.option.applicative.just
import arrow.data.Validated
import db.mmethods.nonlinear.equation.function.DifferentiableTwiceFunction
import db.mmethods.nonlinear.equation.function.EmptyRangeToBoolean
import db.mmethods.nonlinear.equation.function.Function
import db.mmethods.nonlinear.equation.function.SimpleRangeToBoolean
import db.mmethods.nonlinear.equation.methods.BinarySearchMethod
import db.mmethods.nonlinear.equation.methods.CombinedNewtonAndSecantMethod
import db.mmethods.nonlinear.equation.methods.Errors
import db.mmethods.nonlinear.equation.methods.FixedPointMethod
import db.mmethods.nonlinear.equation.methods.NewtonMethod
import db.mmethods.nonlinear.equation.methods.SecantMethod
import java.lang.Double.parseDouble
import java.lang.Math.pow
import java.util.*
import kotlin.math.sign

val DICTIONARY = listOf(
    NewtonMethod to "Метод Ньютону - індекс 1",
    FixedPointMethod to "Метод простої ітерації - індекс 2",
    SecantMethod to "Метод хорд - індекс 3",
    BinarySearchMethod to "Метод дихотомії - індекс 4",
    CombinedNewtonAndSecantMethod to "Комбінований метод хорд та дотичних - індекс 5"
)

fun Errors.localize() = when (this) {
    Errors.NOT_APPLICABLE_DIFFERENTIAL_INVALID -> "умова застосування методу не виконана: перша похідна не зберігає знак"
    Errors.NOT_APPLICABLE_SECOND_DIFFERENTIAL_INVALID -> "умова застосування методу не виконана: друга похідна не зберігає знак"
    Errors.NO_ROOT -> "на кінцях заданого проміжку функція приймає значень одного знаку: або корінь не є відокремлений, або проміжок їх не містить"
    Errors.DIVERGES -> "обраний ітераційний метод розбігається на заданому проміжку: варто зменшити проміжок"
}

val HELP = """
Оберіть ітераційний метод (за індексом), проміжок, на якому обраний метод шукатиме корені, та очікувану абсолютну похибку.
${DICTIONARY.joinToString(separator = ".\n", postfix = ".") { it.second }}
"""

fun main(args: Array<String>) {
    val fn = DifferentiableTwiceFunction(
        Function({ x -> pow(x, 3.0) - 9 * x + 3 }),
        Function({ x -> 3.0 * pow(x, 2.0) - 9.0 }),
        Function({ x -> 6.0 * x }, SimpleRangeToBoolean(0.0), EmptyRangeToBoolean())
    )
    val sc = Scanner(System.`in`)
    while (true) {
        println(HELP)
        val idx = sc.input(
            "Індекс ітераційного методу",
            isValid = { if (it - 1 in DICTIONARY.indices) Option.empty() else "введено не існуючий індекс".just() },
            reader = Integer::parseInt
        )
        val left = sc.input("Ліва межа проміжку", reader = { parseDouble(it) })
        val right = sc.input(
            "Права межа проміжку",
            isValid = { if (left <= it) Option.empty() else "права межа має бути більшою за ліву".just() },
            reader = { parseDouble(it) }
        )
        val eps = sc.input(
            "Очікувана абсолютна похибка",
            isValid = {
                if (it.sign <= 0) "похибка має бути додатнім числом".just()
                else Option.empty()
            },
            reader = { parseDouble(it) }
        )

        DICTIONARY[idx - 1].first.apply(fn, left..right, eps)
            .processResult()
    }
}

private fun Validated<Errors, IndexedValue<Double>>.processResult() = fold(
    { error -> System.err.println("Помилка: ${error.localize()}") },
    { (iterations, root) -> println("\nПісля $iterations ітерацій отримано корінь $root") }
)

fun <T> Scanner.input(text: String, isValid: (T) -> Option<String> = { Option.empty() }, reader: (String) -> T): T {
    while (true) {
        print("$text = ")
        val read = next()
        try {
            val result = reader(read)
            val validation = isValid(result)
            if (validation.isEmpty()) return result
            else println("Некоректний ввід: ${validation.orNull()}")
        } catch (e: Exception) {
            println("Некоректний ввід: очікується ${text.toLowerCase()}, але введено '$read'")
        }
    }
}