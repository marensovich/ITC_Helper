package me.marensovich.itsKipfin.bot.manager.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The interface Prefix callback handler.
 */
public interface PrefixCallbackHandler {
    /**
     * Gets prefix callback data.
     *
     * @return the prefix callback data
     */
    String getPrefixCallbackData();

    /**
     * Handle.
     *
     * @param update the update
     */
    void handle(Update update);
}
