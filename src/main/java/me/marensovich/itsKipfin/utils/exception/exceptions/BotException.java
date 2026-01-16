package me.marensovich.itsKipfin.utils.exception.exceptions;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Исключение бота.
 *
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
    public BotException(String message, @Nullable Update update) {
        super(message);
        if (update == null) {
            update = new Update();
        }
        this.update = update;
    }

}
