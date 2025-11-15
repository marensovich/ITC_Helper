package me.marensovich.itsKipfin.bot.manager.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

/**
 * Интерфейс команды для Telegram бота.
 * <p>
 * Определяет базовый контракт для всех команд бота:
 * имя команды, требование прав администратора и метод выполнения.
 * <p>
 *
 * @author marensovich
 * @version 0.0.1
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
     * Получить описания команды
     *
     * @return описание команды
     * @since 0.0.1
     */
    String getDescription();

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


    /**
     * Область видимости команды (по умолчанию — глобальная).
     *
     * @return the scope
     * @since 0.0.1
     */
    default BotCommandScope getScope() {
        return BotCommandScopeDefault.builder().build();
    }
}
