package me.theseems.tomshelby.mentionpack.commands

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.command.SimpleBotCommand
import me.theseems.tomshelby.command.SimpleCommandMeta
import me.theseems.tomshelby.mentionpack.util.emojis
import me.theseems.tomshelby.mentionpack.util.findGroups
import me.theseems.tomshelby.mentionpack.util.requireGroup
import me.theseems.tomshelby.mentionpack.util.stringInfo
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.objects.Update

class ListGroupBotCommand : SimpleBotCommand(
    SimpleCommandMeta
        .onLabel("mglist")
        .description("List all groups there are")) {
    override fun handle(bot: ThomasBot, args: Array<out String>, update: Update) {
        if (args.isEmpty()) {
            val groups = bot.chatStorage.findGroups(update.message.chatId)
            if (groups.isEmpty()) {
                bot.replyBackText(update, "No groups found for your chat.")
            } else {
                bot.replyBackText(update,
                    groups.joinToString(
                        transform = {
                            "- $it (with ${
                                bot.chatStorage.requireGroup(update.message.chatId,
                                    it).size
                            } member(s))"
                        },
                        prefix = "There are mention groups for your chat:\n",
                        separator = "\n",
                        postfix = "\n\nIf you wish to list members of a certain group just type:" +
                                " /mglist <group_name>"))
            }

            return
        }

        val group = bot.chatStorage.requireGroup(update.message.chatId, args.first())
        val builder = StringBuilder()
        group.forEach { id ->
            val chatMember = bot.execute(GetChatMember().apply {
                chatId = update.message.chatId.toString()
                userId = id
            })

            builder.append("${chatMember.emojis()} ${chatMember.stringInfo()}\n")
        }

        bot.replyBackText(update, builder.toString())
    }
}
