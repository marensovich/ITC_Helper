package me.marensovich.itsKipfin.bot.manager.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The interface Callback handler.
 */
public interface CallbackHandler {
    /**
     * Gets callback data.
     *
     * @return the callback data
     */
    String getCallbackData();

    /**
     * Handle.
     *
     * @param update the update
     */
    void handle(Update update);

}
