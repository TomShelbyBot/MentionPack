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
            .description("Создать группу (или добавить человека туда)")
    ) {

    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.size < 2) {
            bot.replyBackText(update, "Укажите название группы и ник человека")
            return
        }

        val chatId = update.message.chatId
        val groupName = args[0]
        val failed: MutableSet<String> = HashSet()
        val success: MutableSet<Int> = HashSet()

        for (rawUsername in args.drop(1)) {
            val username =
                if (rawUsername.startsWith('@')) {
                    rawUsername.drop(1)
                } else {
                    rawUsername
                }

            val lookup = bot.chatStorage.lookupMember(chatId, username)
            if (!lookup.isPresent) {
                failed.add(username)
            } else {
                success.add(lookup.get().user.id)
            }
        }

        if (success.isNotEmpty()) {
            val groups = getOrCreateContainer(bot, chatId, "mentionGroups")
            if (!groups.getIntegerArray(groupName).isPresent) {
                groups.set(groupName, success.toIntArray())
            } else {
                val array = groups.getIntegerArray(groupName).get()
                success.addAll(array)

                groups.set(groupName, success)
            }
        }

        var result = "Теперь в группе $groupName участников: ${success.size}"
        if (failed.isNotEmpty()) {
            result += "\n" + "Следующие участники не были добавлены: " + failed.joinToString(", ")
        }

        bot.replyBackText(update, result)
    }
}
