package db.mmethods.linear.equations

import arrow.core.Option
import arrow.core.extensions.option.applicative.just
import arrow.data.Validated
import db.mmethods.linear.equations.matrix.Matrix
import db.mmethods.linear.equations.matrix.Vector
import db.mmethods.linear.equations.matrix.asString
import db.mmethods.linear.equations.methods.GaussMethod
import db.mmethods.linear.equations.methods.MpiMethod
import db.mmethods.linear.equations.methods.SystemOfLinearEquationsSolverMethod
import db.mmethods.linear.equations.methods.SystemOfLinearEquationsSolverMethod.Errors
import java.util.Scanner
import kotlin.math.sign

val DICTIONARY = listOf(
    GaussMethod to "Метод Гаусcа - індекс 1",
    MpiMethod to "Метод простої ітерації - індекс 2"
)

fun SystemOfLinearEquationsSolverMethod.Errors.localize() = when (this) {
    Errors.DETERMINANT_IS_ZERO -> "Визначник системи дорівнює нулю, система не розроблена відповідно до безлічі розв'язків"
    Errors.NOT_DIAGONALLY_DOMINATED -> "Матриця коефіцієнтів не має діагонального домінування, неможливо застосувати метод простої ітерації"
}

val HELP = """
Оберіть метод для розв'язку СЛАР (за індексом), та очікувану абсолютну похибку (у випадку методу простої ітерації).
${DICTIONARY.joinToString(separator = ".\n", postfix = ".") { it.second }}
"""

fun main(args: Array<String>) {
    val gaussMatrix = Matrix(
        3, 4,
        arrayOf(
            doubleArrayOf(9.0, 12.0, -3.0, 3.0),
            doubleArrayOf(12.0, 25.0, 8.0, 4.0),
            doubleArrayOf(-3.0, 8.0, 13.0, 5.0)
        )
    )
    val iterationMatrix = Matrix(
        3, 4,
        arrayOf(
            doubleArrayOf(4.0, 1.0, 0.0, -28.0 / 3.0),
            doubleArrayOf(12.0, 25.0, 8.0, 4.0),
            doubleArrayOf(-3.0, 8.0, 13.0, 5.0)
        )
    )
    val sc = Scanner(System.`in`)
    while (true) {
        println(HELP)
        val idx = sc.fetchMethodIdx()
        val method = DICTIONARY[idx - 1].first
        val isIterationMethod = method == MpiMethod
        val matrix = if (isIterationMethod) iterationMatrix else gaussMatrix
        val eps = if (isIterationMethod) sc.fetchEps() else 1e-9
        method.apply(matrix, eps).processResult(isIterationMethod, eps)
    }
}

private fun Scanner.fetchMethodIdx() = input(
    "Індекс методу",
    isValid = { if (it - 1 in DICTIONARY.indices) Option.empty() else "введено не існуючий індекс".just() },
    reader = Integer::parseInt
)

private fun Scanner.fetchEps() = input(
    "Очікувана абсолютна похибка",
    isValid = {
        if (it.sign <= 0) "похибка має бути додатнім числом".just()
        else Option.empty()
    },
    reader = { java.lang.Double.parseDouble(it) }
)

private fun Validated<Errors, IndexedValue<Vector>>.processResult(isIterationMethod: Boolean, eps: Double) = fold(
    { error -> System.err.println("Помилка: ${error.localize()}") },
    { (iterations, root) ->
        println(
            if (isIterationMethod) "\nПісля $iterations ітерацій отримано розв'язок: ${root.asString(eps)}"
            else "\nПісля застосування методу Гаусса отримано розв'язок: ${root.asString(eps)}"
        )
    }
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