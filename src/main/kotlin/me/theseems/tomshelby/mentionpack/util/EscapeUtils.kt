package me.theseems.tomshelby.mentionpack.util

fun String.markdownV2Escape(): String {
    val newString = StringBuilder()
    for (char in this) {
        if (!char.isLetterOrDigit()) {
            newString.append("\\")
        }
        newString.append(char)
    }
    return newString.toString()
}
