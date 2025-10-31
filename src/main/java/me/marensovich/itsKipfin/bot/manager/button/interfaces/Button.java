package me.marensovich.itsKipfin.bot.manager.button.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Button {
    String getButtonText();
    void handle(Update update);
}
