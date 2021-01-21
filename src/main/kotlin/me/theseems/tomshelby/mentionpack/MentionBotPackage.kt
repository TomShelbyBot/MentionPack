package me.theseems.tomshelby.mentionpack

import me.theseems.tomshelby.mentionpack.commands.*
import me.theseems.tomshelby.mentionpack.handlers.MessageMentionHandler
import me.theseems.tomshelby.pack.JavaBotPackage

open class MentionBotPackage : JavaBotPackage() {
    private lateinit var mentionHandler: MessageMentionHandler

    override fun onEnable() {
        bot.commandContainer.attach(MentionGroupBotCommand())
        bot.commandContainer.attach(AddGroupBotCommand())
        bot.commandContainer.attach(ListGroupBotCommand())
        bot.commandContainer.attach(DeleteGroupBotCommand())
        bot.commandContainer.attach(RemoveGroupBotCommand())

        mentionHandler = MessageMentionHandler()
        bot.updateHandlerManager.addUpdateHandler(mentionHandler)
    }

    override fun onDisable() {
        bot.updateHandlerManager.removeUpdateHandler(mentionHandler)
        bot.commandContainer.detach("mgat")
        bot.commandContainer.detach("mglist")
        bot.commandContainer.detach("mgadd")
        bot.commandContainer.detach("mgdel")
        bot.commandContainer.detach("mgrem")
    }
}
