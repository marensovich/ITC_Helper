package me.marensovich.itsKipfin.bot.manager.update;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.services.UserService;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Менеджер обработки обновлений от Telegram API.
 * <p>
 * Отвечает за маршрутизацию сообщений и callback-запросов, проверку существования пользователей
 * и делегирование команд {@link Bot#getCommandManager()} и кнопок {@link ButtonManager}.
 * Также инициирует обработку пошаговых заявок через {@link RegisterITCButton.ProjectTeamHandler}.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class UpdateManager {

    private final UserService userService;
    private final ButtonManager buttonManager;
    private final KeyboardFactory keyboardFactory;

    /**
     * Конструктор UpdateManager.
     *
     * @param userService     сервис для работы с пользователями
     * @param buttonManager   менеджер кнопок
     * @param keyboardFactory фабрика клавиатур
     * @author marensovich
     * @since 0.0.1
     */
    public UpdateManager(UserService userService, ButtonManager buttonManager, KeyboardFactory keyboardFactory) {
        this.userService = userService;
        this.buttonManager = buttonManager;
        this.keyboardFactory = keyboardFactory;
    }

    /**
     * Главный обработчик обновлений от Telegram.
     * <p>
     * Проверяет, является ли пользователь зарегистрированным,
     * обрабатывает команды, callback-и и пошаговые заявки.
     *
     * @param update объект обновления Telegram
     * @throws TelegramApiException если произошла ошибка при отправке сообщений
     * @author marensovich
     * @since 0.0.1
     */
    public void updateHandler(Update update) throws TelegramApiException {

        if (!update.hasMessage() && !update.hasCallbackQuery()) return;

        if (update.hasMessage()) {
            long userId = update.getMessage().getFrom().getId();

            // Создаём нового пользователя, если его нет в базе
            if (!userService.isUserExists(userId)) {
                userService.createUser(userId);
            }

            // Проверка активной команды
            if (!Bot.getInstance().getCommandManager().hasActiveCommand(userId)) {

                // Обработка команд вида "/команда"
                if (update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
                    boolean executed = Bot.getInstance().getCommandManager().executeCommand(update);
                    if (!executed) {
                        SendMessage message = new SendMessage();
                        message.setChatId(update.getMessage().getChatId().toString());
                        message.setText(
                                "Команда не распознана, проверьте правильность написания команды. \n\n" +
                                        "Команды с доп. параметрами указаны отдельной графой в информации. Подробнее в /help."
                        );
                        Bot.getInstance().execute(message);
                        return;
                    }
                }

                // Обработка пошаговых заявок на вступление
                if (RegisterITCButton.ProjectTeamHandler.userApplicationDataMap.containsKey(userId)) {
                    RegisterITCButton.ProjectTeamHandler handler =
                            new RegisterITCButton.ProjectTeamHandler(update, keyboardFactory);
                    handler.handle();
                    return;
                }

                if (RegisterITCButton.MediaHandler.userApplicationDataMap.containsKey(userId)) {
                    RegisterITCButton.MediaHandler handler =
                            new RegisterITCButton.MediaHandler(update, keyboardFactory);
                    handler.handle();
                    return;
                }
                // Обработка обычных кнопок
                buttonManager.handle(update);

            } else {
                // Выполнение активной команды пользователя
                Bot.getInstance().getCommandManager().executeCommand(update);
                return;
            }
        }

        // Обработка callback-запросов (inline кнопки)
        if (update.hasCallbackQuery()) {
            long userId = update.getCallbackQuery().getFrom().getId();

            // Создаём нового пользователя, если его нет
            if (!userService.isUserExists(userId)) {
                userService.createUser(userId);
            }

            // Делегирование обработки callback-а
            boolean handled = Bot.getInstance().getCallbackManager().handleCallback(update);
            if (!handled) {
                SendMessage errorMsg = new SendMessage();
                errorMsg.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
                errorMsg.setText("Действие не распознано, попробуйте ещё раз");
                Bot.getInstance().execute(errorMsg);
            }
        }
    }
}
