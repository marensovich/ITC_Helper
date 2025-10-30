package me.marensovich.itsKipfin.bot.manager.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface PrefixCallbackHandler {
    String getPrefixCallbackData();
    void handle(Update update);
}
