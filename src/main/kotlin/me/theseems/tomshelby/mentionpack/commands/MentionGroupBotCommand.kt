package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.mentionpack.util.requireGroup
import me.theseems.tomshelby.mentionpack.util.sendNotification
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update

class MentionGroupBotCommand :
    SimpleBotCommand(
        SimpleCommandMeta
            .onLabel("mgat")
            .description("Упомянуть группу из чата")
    ) {

    override fun handle(bot: ThomasBot, rawArgs: Array<out String>, update: Update) {
        val filterArray = rawArgs.filter { arg -> arg != "-Delete" }
        val deleteMessage = filterArray.size != rawArgs.size
        val args = filterArray.toTypedArray()

        if (args.isEmpty()) {
            bot.replyBackText(update, "Укажите группу, которую нужно пингануть")
            return
        }

        val chatId = update.message.chatId
        val messageId = update.message.messageId

        if (deleteMessage) {
            val deleteMessageRequest = DeleteMessage()
            deleteMessageRequest.chatId = chatId.toString()
            deleteMessageRequest.messageId = messageId

            bot.execute(deleteMessageRequest)
        }

        sendNotification(
            bot, update,
            bot.chatStorage.requireGroup(chatId, args.first()),
            args.drop(1).joinToString(" ")
        )
    }
}
