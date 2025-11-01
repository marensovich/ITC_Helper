package me.marensovich.itsKipfin.bot.manager.button.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The interface Button.
 */
public interface Button {
    /**
     * Gets button text.
     *
     * @return the button text
     */
    String getButtonText();

    /**
     * Handle.
     *
     * @param update the update
     */
    void handle(Update update);
}
