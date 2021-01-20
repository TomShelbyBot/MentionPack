package me.theseems.tomshelby.mentionpack.util

import me.theseems.tomshelby.ThomasBot
import me.theseems.tomshelby.storage.SimpleTomMeta
import me.theseems.tomshelby.storage.TomMeta

fun getOrCreateContainer(bot: ThomasBot, chatId: Long, key: String): TomMeta {
    val chatMeta = bot.chatStorage.getChatMeta(chatId)
    val containerOptional = chatMeta.getContainer(key)
    if (containerOptional.isPresent)
        return containerOptional.get()

    // Or else we'll create it
    val container = SimpleTomMeta()
    chatMeta.set(key, container)

    return container
}
