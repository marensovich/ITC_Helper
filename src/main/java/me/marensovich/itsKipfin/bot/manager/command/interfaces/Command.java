package me.marensovich.itsKipfin.bot.manager.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    String getName();
    boolean isAdminRequired();
    void execute(Update update);
}
