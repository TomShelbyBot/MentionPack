package me.theseems.tomshelby.mentionpack.util

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.storage.ChatStorage
import me.theseems.tomshelby.util.CommandUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

fun ChatStorage.getGroup(chatId: Long, groupName: String): Array<out Long>? {
    return when (groupName) {
        "all" ->
            getMemberIds(chatId).toTypedArray()

        "admins" ->
            getAdminIds(chatId).toTypedArray()

        else -> {
            val containerOptional = getChatMeta(chatId.toString()).getContainer("mentionGroups")
            val groupListOptional = containerOptional.flatMap { container -> container.getLongArray(groupName) }
            if (!containerOptional.isPresent || !groupListOptional.isPresent)
                return null

            return groupListOptional.get()
        }
    }
}

fun ChatStorage.requireGroup(chatId: Long, groupName: String): Array<out Long> {
    return getGroup(chatId, groupName)
        ?: throw CommandUtils.BotCommandException("Не удалось найти группу упоминаний '$groupName'")
}

fun sendNotification(bot: ThomasBot, update: Update, group: Array<out Long>, message: String) {
    val messageBuilder = StringBuilder(
        "\uD83D\uDD14 " + message.markdownV2Escape()
    )

    group.forEach { id ->
        messageBuilder.append(makeInvisibleMention(id))
    }

    val sendMessage = SendMessage()
    sendMessage.text = messageBuilder.toString()
    sendMessage.enableMarkdownV2(true)

    bot.sendBack(update, sendMessage)
}
