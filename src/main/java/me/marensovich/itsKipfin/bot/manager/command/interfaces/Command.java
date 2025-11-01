package me.marensovich.itsKipfin.bot.manager.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The interface Command.
 */
public interface Command {
    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Is admin required boolean.
     *
     * @return the boolean
     */
    boolean isAdminRequired();

    /**
     * Execute.
     *
     * @param update the update
     */
    void execute(Update update);
}
