package db.mmethods.linear.equations.matrix

fun Array<DoubleArray>.asString(): String {
    val string = StringBuilder()
    string.append(topLeftCorner).append(' ' * (this[0].size * 8 + 3)).append(topRightCorner)
        .append(System.lineSeparator())
    for (i in this.indices) {
        string.append(vertical)
        for (j in this[i].indices) {
            string.append(String.format("%8.3f", this[i][j]))
        }
        string.append("   ")
        string.append(vertical)
        string.append(System.lineSeparator())
    }
    string.append(bottomLeftCorner).append(' ' * (this[0].size * 8 + 3)).append(bottomRightCorner)
        .append(System.lineSeparator())
    return string.toString()
}

fun Vector.asString(eps: Double = 1e-3): String {
    val digits = (-Math.log10(eps)).toInt()
    return joinToString(prefix = "[", postfix = "]") { String.format("%.${digits}f", it) }
}

private const val topLeftCorner = "\u250c"
private const val topRightCorner = "\u2510"
private const val bottomLeftCorner = "\u2514"
private const val bottomRightCorner = "\u2518"
private const val vertical = "\u2502"

private operator fun Char.times(i: Int) = (1..i).map { this@times }.joinToString(separator = "")