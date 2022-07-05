package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.util.CommandUtils
import org.telegram.telegrambots.meta.api.objects.Update


class RemoveGroupBotCommand : SimpleBotCommand(
    SimpleCommandMeta
        .onLabel("mgrem")
        .description("Удалить пользователя из группы упоминаний")
) {

    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.size < 2) {
            bot.replyBackText(update, "Укажите группу и участника для исключения")
            return
        }

        val chatMeta = bot.chatStorage.getChatMeta(update.message.chatId.toString()).getContainer("mentionGroups")
            .orElseThrow { throw CommandUtils.BotCommandException("Не могу найти группу упоминаний") }
        val groupList = chatMeta.getLongArray(args[0])
            .orElseThrow { throw CommandUtils.BotCommandException("Не могу найти группу упоминаний") }

        val successIds: MutableSet<Long> = HashSet()
        val fails = mutableListOf<Pair<String, String>>()

        for (username in args.drop(1)) {
            val memberId = bot.chatStorage.lookup(update.message.chatId.toString(), username)
            if (!memberId.isPresent) {
                fails += username to "Участник не был найден"
            } else if (!groupList.contains(memberId.get())) {
                fails += username to "Пользователь не состоит в группе упоминаний"
            } else {
                successIds += memberId.get()
            }
        }

        if (fails.isNotEmpty()) {
            val failsString =
                fails.joinToString(
                    prefix = "\n",
                    separator = "\n",
                    transform = { pair -> "${pair.first}: ${pair.second}" })
            bot.replyBackText(
                update,
                "Следующие участники не были удалены (${fails.size}): \n$failsString"
            )
        }

        if (successIds.isNotEmpty()) {
            val alternate = mutableListOf<Long>(*groupList)
            alternate.removeAll(successIds)

            if (alternate.isEmpty()) {
                chatMeta.remove(args.first())
            } else {
                chatMeta.set(args.first(), alternate.toTypedArray())
            }
            bot.replyBackText(update, "Теперь в группе ${args.first()} участников: ${alternate.size}")
        }
    }

}