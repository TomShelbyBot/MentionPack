package me.theseems.tomshelby.mentionpack.util

import me.theseems.tomshelby.Main
import me.theseems.tomshelby.storage.ChatStorage
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.objects.ChatMember

fun makeInvisibleMention(userId: Int): String {
    return "[­](tg://user?id=$userId)"
}

fun makeMention(userId: Int, title: String? = null): String =
    if (title == null) {
        makeInvisibleMention(userId)
    } else {
        "[$title](tg://user?id=$userId)"
    }

private enum class Info {
    USERNAME, FIRST_NAME, SECOND_NAME, ID, END
}

fun ChatMember.stringInfo(withTitle: Boolean = false): String {
    val infoParts = mutableListOf<String>()

    var currentState = Info.USERNAME
    while (true) when (currentState) {
        Info.USERNAME -> {
            val hasUsername = user.userName != null
            if (hasUsername)
                infoParts.add(user.userName)

            currentState = if (hasUsername) Info.END else Info.FIRST_NAME
        }

        Info.FIRST_NAME -> {
            if (user.firstName != null)
                infoParts.add(user.firstName)
            currentState = Info.SECOND_NAME
        }

        Info.SECOND_NAME -> {
            val hasLastName = user.lastName != null
            if (hasLastName)
                infoParts.add(user.lastName)

            currentState = if (hasLastName) Info.END else Info.ID
        }

        Info.ID -> {
            infoParts.add(user.id.toString())
            currentState = Info.END
        }

        Info.END -> {
            if (withTitle && customTitle != null) infoParts.add(customTitle)
            break
        }
    }

    return infoParts.joinToString(" ")
}

fun ChatStorage.getMemberIds(chatId: Long): List<Int> = getResolvableUsernames(chatId)
    .map { username -> lookupMember(chatId, username) }
    .filter { value -> value.isPresent }
    .map { value -> value.get().user.id }

fun getAdminIds(chatId: Long): List<Int> = Main.getBot()
    .execute(GetChatAdministrators().setChatId(chatId))
    .map { entry -> entry.user.id }
