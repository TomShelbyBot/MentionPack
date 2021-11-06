package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.mentionpack.util.requireGroup
import org.telegram.telegrambots.meta.api.objects.Update

class DeleteGroupBotCommand :
    SimpleBotCommand(
        SimpleCommandMeta
            .onLabel("mgdel")
            .description("Delete mention group")
    ) {
    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.isEmpty()) {
            bot.replyBackText(update, "Please, enter in a group name")
            return
        }

        bot.chatStorage.requireGroup(update.message.chatId, args.first())
        bot.chatStorage
            .getChatMeta(update.message.chatId)
            .getContainer("mentionGroups")
            .ifPresent { container -> container.remove(args.first()) }

        bot.replyBackText(update, "OK")
    }
}
