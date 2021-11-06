package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.AdminPermissibleBotCommand
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.mentionpack.util.getOrCreateContainer
import org.telegram.telegrambots.meta.api.objects.Update

class AddGroupBotCommand : AdminPermissibleBotCommand,
    SimpleBotCommand(
        SimpleCommandMeta
            .onLabel("mgadd")
            .description("Create group and/or add member")) {
    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.size < 2) {
            bot.replyBackText(update, "Укажите название группы и ник человека")
            return
        }

        val chatId = update.message.chatId
        val groupName = args[0]
        val failed: MutableSet<String> = HashSet()
        val success: MutableSet<Int> = HashSet()

        update.message.entities
            .filter { it.user != null }
            .map { it.user.id }
            .forEach(success::add)

        for (rawUsername in args.drop(1)) {
            val username =
                if (rawUsername.startsWith('@')) {
                    rawUsername.drop(1)
                } else {
                    rawUsername
                }

            val lookUp = bot.chatStorage.lookupMember(chatId, username);
            if (lookUp.isPresent) {
                success.add(lookUp.get().user.id)
            } else {
                failed.add(username)
            }
        }

        if (success.isNotEmpty()) {
            val groups = getOrCreateContainer(bot, chatId, "mentionGroups")
            if (!groups.getIntegerArray(groupName).isPresent) {
                groups.set(groupName, success.toIntArray())
            } else {
                success.addAll(groups.getIntegerArray(groupName).get())
                groups.set(groupName, success)
            }
        }

        var result = "Mention group $groupName currently has: ${success.size} member(s)"
        if (failed.isNotEmpty()) {
            result += "\n" + "Members haven't been found: " + failed.joinToString(", ")
        }

        bot.replyBackText(update, result)
    }
}
