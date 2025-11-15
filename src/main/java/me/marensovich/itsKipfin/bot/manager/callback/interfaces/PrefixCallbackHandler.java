package me.marensovich.itsKipfin.bot.manager.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработки callback-запросов Telegram по префиксу.
 * <p>
 * Используется для кнопок, у которых callbackData начинается с определённого префикса.
 * <p>
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
public interface PrefixCallbackHandler {

    /**
     * Получить префикс callbackData, с которым связан этот handler.
     *
     * @return строка префикса callbackData
     * @author marensovich
     * @since 0.0.1
     */
    String getPrefixCallbackData();

    /**
     * Обработать callback-запрос.
     *
     * @param update объект обновления Telegram
     * @author marensovich
     * @since 0.0.1
     */
    void handle(Update update);
}
