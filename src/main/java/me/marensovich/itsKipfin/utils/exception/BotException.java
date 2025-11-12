package me.marensovich.itsKipfin.utils.exception;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Исключение бота.
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Getter
public class BotException extends RuntimeException {

    @Getter
    private final Update update;

    /**
     * Инициализирует исключение бота.
     *
     * @param message the message
     * @param update  the update
     * @author marensovich
     * @since 0.0.1
     */
    public BotException(String message, Update update) {
        super(message);
        this.update = update;
    }

}
