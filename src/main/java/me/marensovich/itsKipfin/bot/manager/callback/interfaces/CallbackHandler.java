package me.marensovich.itsKipfin.bot.manager.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработки callback-запросов Telegram.
 * <p>
 * Используется для точного совпадения callbackData с кнопкой.
 * <p>
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 */
public interface CallbackHandler {

    /**
     * Получить callbackData, с которым связан этот handler.
     *
     * @return строка callbackData
     * @author marensovich
     * @since 0.0.1
     */
    String getCallbackData();

    /**
     * Обработать callback-запрос.
     *
     * @param update объект обновления Telegram
     * @author marensovich
     * @since 0.0.1
     */
    void handle(Update update);
}
