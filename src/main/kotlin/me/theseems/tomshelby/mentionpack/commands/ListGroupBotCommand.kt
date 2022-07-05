package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.mentionpack.util.emojis
import me.theseems.tomshelby.mentionpack.util.requireGroup
import me.theseems.tomshelby.mentionpack.util.stringInfo
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.objects.Update

class ListGroupBotCommand : SimpleBotCommand(
    SimpleCommandMeta
        .onLabel("mglist")
        .description("Создать группу (или добавить человека туда)")
) {

    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.isEmpty()) {
            bot.replyBackText(update, "Укажите группу")
            return
        }

        val group = bot.chatStorage.requireGroup(update.message.chatId, args.first())
        val builder = StringBuilder()
        group.forEach { id ->
            val getChatMember = GetChatMember()
            getChatMember.chatId = update.message.chatId.toString()
            getChatMember.userId = id

            val chatMember = bot.execute(getChatMember)
            val info = chatMember.stringInfo()
            builder.append("${chatMember.emojis()} $info").append('\n')
        }

        bot.replyBackText(update, builder.toString())
    }
}
