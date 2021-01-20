package me.theseems.tomshelby.mentionpack.handlers

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.mentionpack.util.getGroup
import me.theseems.tomshelby.mentionpack.util.sendNotification
import me.theseems.tomshelby.update.UpdateHandler
import org.telegram.telegrambots.meta.api.objects.Update

class MessageMentionHandler : UpdateHandler {
    override fun handleUpdate(bot: ThomasBot, update: Update): Boolean {
        if (!update.hasMessage()) return true

        val text = update.message.text ?: return true
        if (!text.startsWith("@")) return true

        val split = text.split(" ")
        val mentionCandidate = split[0].drop(1)
        if (mentionCandidate.isEmpty()) return true

        val group = bot.chatStorage.getGroup(update.message.chatId, mentionCandidate)
        sendNotification(
            bot, update,
            group ?: return true,
            split.drop(1).joinToString(" ")
        )
        return false
    }

    override fun getPriority(): Int {
        return 1001
    }
}
