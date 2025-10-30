package me.marensovich.itsKipfin.bot.manager.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    String getCallbackData();
    void handle(Update update);

}
