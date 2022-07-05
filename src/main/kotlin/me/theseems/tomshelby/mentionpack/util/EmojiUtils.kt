package me.theseems.tomshelby.mentionpack.util

import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import kotlin.math.abs
import kotlin.math.min

val emojiRange = 0x1f601..0x1f64f

private fun clampMod(value: Int, range: IntRange): Int {
    return range.first + abs(value) % (range.last - range.first + 1)
}

fun String.mapToEmoji(sliceStep: Int = 8): String {
    var result = String()

    for (i in indices step sliceStep) {
        val currentSubstring = substring(i, min(length - 1, i + sliceStep))
        val emoji = String(
            Character.toChars(
                clampMod(currentSubstring.hashCode(), emojiRange)
            )
        )

        result += emoji
    }

    return result
}

fun ChatMember.emojis(): String {
    return stringInfo().mapToEmoji()
}
