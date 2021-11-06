package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.util.CommandUtils
import org.telegram.telegrambots.meta.api.objects.Update


class RemoveGroupBotCommand : SimpleBotCommand(
    SimpleCommandMeta
        .onLabel("mgrem")
        .description("Remove user from the mention group")) {
    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.size < 2) {
            bot.replyBackText(update, "Please, provide in a group name and member to kick")
            return
        }

        val chatMeta = bot.chatStorage
            .getChatMeta(update.message.chatId)
            .getContainer("mentionGroups")
            .orElseThrow { throw CommandUtils.BotCommandException("Specified notification group is not found") }

        val groupList = chatMeta.getIntegerArray(args[0])
            .orElseThrow { throw CommandUtils.BotCommandException("Group list is empty") }

        val successIds: MutableSet<Int> = HashSet()
        val fails = mutableListOf<Pair<String, String>>()
        update.message.entities
            .filter { it.user != null }
            .map { it.user.id }
            .forEach(successIds::add)

        for (username in args.drop(1)) {
            val memberId = bot.chatStorage.lookup(update.message.chatId, username)
            if (!memberId.isPresent) {
                fails += username to "is not found"
            } else if (!groupList.contains(memberId.get())) {
                fails += username to "not in the mention group"
            } else {
                successIds += memberId.get()
            }
        }

        if (fails.isNotEmpty()) {
            val failsString = fails.joinToString(
                    prefix = "\n",
                    separator = "\n",
                    transform = { pair -> "${pair.first}: ${pair.second}" })
            bot.replyBackText(
                update,
                "Some specified users have not been encountered (${fails.size}): \n$failsString"
            )
        }

        if (successIds.isNotEmpty()) {
            val alternate = mutableListOf<Int>(*groupList).apply { removeAll(successIds) }
            if (alternate.isEmpty()) {
                chatMeta.remove(args.first())
            } else {
                chatMeta.set(args.first(), alternate.toTypedArray())
            }

            bot.replyBackText(update,
                "Mention group ${args.first()} now has: ${alternate.size} member(s)")
        }
    }

}