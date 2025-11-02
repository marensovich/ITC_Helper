package me.marensovich.itsKipfin.bot.manager.button.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс кнопки бота.
 * <p>
 * Определяет методы для работы с кнопкой:
 * получения текста кнопки и обработки нажатия.
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 */
public interface Button {

    /**
     * Получить текст кнопки.
     *
     * @return текст, который отображается на кнопке
     * @author marensovich
     * @since 0.0.1
     */
    String getButtonText();

    /**
     * Обработать нажатие на кнопку.
     *
     * @param update объект Update из Telegram, содержащий информацию о событии
     * @author marensovich
     * @since 0.0.1
     */
    void handle(Update update);
}
