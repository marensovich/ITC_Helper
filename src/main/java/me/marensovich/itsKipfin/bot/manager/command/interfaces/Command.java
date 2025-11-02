package me.marensovich.itsKipfin.bot.manager.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс команды для Telegram бота.
 * <p>
 * Определяет базовый контракт для всех команд бота:
 * имя команды, требование прав администратора и метод выполнения.
 * <p>
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 */
public interface Command {

    /**
     * Получить имя команды.
     *
     * @return имя команды (строка, используемая для вызова)
     * @since 0.0.1
     */
    String getName();

    /**
     * Проверка, требуется ли права администратора для выполнения команды.
     *
     * @return true, если права администратора обязательны, иначе false
     * @since 0.0.1
     */
    boolean isAdminRequired();

    /**
     * Выполнить команду.
     *
     * @param update объект обновления от Telegram (сообщение, callback и т.д.)
     * @since 0.0.1
     */
    void execute(Update update);
}
