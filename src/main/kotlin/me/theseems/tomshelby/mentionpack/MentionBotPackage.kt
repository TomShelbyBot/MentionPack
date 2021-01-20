package me.theseems.tomshelby.mentionpack

import me.theseems.tomshelby.mentionpack.commands.AddGroupBotCommand
import me.theseems.tomshelby.mentionpack.commands.DeleteGroupBotCommand
import me.theseems.tomshelby.mentionpack.commands.ListGroupBotCommand
import me.theseems.tomshelby.mentionpack.commands.MentionGroupBotCommand
import me.theseems.tomshelby.mentionpack.handlers.MessageMentionHandler
import me.theseems.tomshelby.pack.JavaBotPackage

open class MentionBotPackage : JavaBotPackage() {
    private lateinit var mentionHandler: MessageMentionHandler

    override fun onEnable() {
        bot.commandContainer.attach(MentionGroupBotCommand())
        bot.commandContainer.attach(AddGroupBotCommand())
        bot.commandContainer.attach(ListGroupBotCommand())
        bot.commandContainer.attach(DeleteGroupBotCommand())

        mentionHandler = MessageMentionHandler()
        bot.updateHandlerManager.addUpdateHandler(mentionHandler)
    }

    override fun onDisable() {
        bot.updateHandlerManager.removeUpdateHandler(mentionHandler)
        bot.commandContainer.detach("mgat")
        bot.commandContainer.detach("mglist")
        bot.commandContainer.detach("mgadd")
        bot.commandContainer.detach("mgdel")
    }
}
