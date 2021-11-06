package me.theseems.tomshelby.mentionpack.util

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.storage.ChatStorage
import me.theseems.tomshelby.util.CommandUtils
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

fun ChatStorage.findGroups(chatId: Long): Array<String> {
    val containerOptional = getChatMeta(chatId)
        .getContainer("mentionGroups")

    if (!containerOptional.isPresent) {
        return emptyArray()
    }

    return containerOptional.get().keys.toTypedArray()
}

fun ChatStorage.getGroup(chatId: Long, groupName: String): Array<out Int>? {
    return when (groupName) {
        "all" ->
            getMemberIds(chatId).toTypedArray()

        "admins" ->
            getAdminIds(chatId).toTypedArray()

        else -> {
            val containerOptional = getChatMeta(chatId).getContainer("mentionGroups")
            val groupListOptional = containerOptional.flatMap { it.getIntegerArray(groupName) }
            if (!containerOptional.isPresent || !groupListOptional.isPresent) {
                return null
            }

            return groupListOptional.get()
        }
    }
}

fun ChatStorage.requireGroup(chatId: Long, groupName: String): Array<out Int> {
    return getGroup(chatId, groupName)
        ?: throw CommandUtils.BotCommandException("Mention group $groupName is not found")
}

fun sendNotification(bot: ThomasBot, update: Update, group: Array<out Int>, message: String) {
    val messageBuilder = StringBuilder(
        "\uD83D\uDD14 " + message.markdownV2Escape())

    group.map(::makeInvisibleMention)
        .forEach(messageBuilder::append)

    bot.sendBack(
        update,
        SendMessage()
            .setText(messageBuilder.toString())
            .enableMarkdownV2(true)
    )
}
