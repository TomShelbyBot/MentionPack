package me.theseems.tomshelby.mentionpack.util

import me.theseems.tomshelby.Main
import me.theseems.tomshelby.storage.ChatStorage
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator

fun makeInvisibleMention(userId: Long): String {
    return "[­](tg://user?id=$userId)"
}

fun makeMention(userId: Long, title: String? = null): String =
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
            if (withTitle && this is ChatMemberAdministrator) {
                infoParts.add(customTitle)
            }

            break
        }
    }

    return infoParts.joinToString(" ")
}

fun ChatStorage.getMemberIds(chatId: Long): List<Long> {
    val result = mutableListOf<Long>()
    getResolvableUsernames(chatId.toString()).map { nickname ->
        lookupMember(chatId.toString(), nickname).ifPresent { member ->
            result.add(member.user.id)
        }
    }

    return result
}

fun getAdminIds(chatId: Long): List<Long> {
    val result = mutableListOf<Long>()

    val getChatMemberAdministrators = GetChatAdministrators()
    getChatMemberAdministrators.chatId = chatId.toString()

    val administrators = Main.getBot().execute(getChatMemberAdministrators)

    for (chatMember in administrators) {
        result.add(chatMember.user.id)
    }

    return result
}
