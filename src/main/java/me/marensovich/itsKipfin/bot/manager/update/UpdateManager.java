package me.marensovich.itsKipfin.bot.manager.update;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.DesignerHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.MediaHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.PRHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.ProjectTeamHandler;
import me.marensovich.itsKipfin.database.models.User;
import me.marensovich.itsKipfin.services.UserService;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер обработки обновлений от Telegram API.
 * <p>
 * Отвечает за маршрутизацию сообщений и callback-запросов, проверку существования пользователей
 * и делегирование команд {@link Bot#getCommandManager()} и кнопок {@link ButtonManager}.
 * Также инициирует обработку пошаговых заявок через:
 * <li>{@link ProjectTeamHandler}</li>
 * <li>{@link MediaHandler}</li>
 * <li>{@link RegisterITCButton.PRHandler}</li>
 * <li>{@link RegisterITCButton.DesignerHandler}</li>
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class UpdateManager {

    public Map<String, User> hashedUsers = new HashMap<>();

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

            // Проверяем наличие пользователя в списке
            if (!hashedUsers.containsKey(String.valueOf(userId))) {
                // Создаём нового пользователя, если его нет в базе
                // Добавляем в список
                if (!userService.isUserExists(userId)) {
                    User user = userService.createUser(userId);
                    hashedUsers.put(String.valueOf(userId), user);
                }
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
                if (ProjectTeamHandler.userApplicationDataMap.containsKey(userId)) {
                    ProjectTeamHandler handler = new ProjectTeamHandler(update, keyboardFactory);
                    handler.handle();
                    return;
                }

                if (MediaHandler.userApplicationDataMap.containsKey(userId)) {
                    MediaHandler handler = new MediaHandler(update, keyboardFactory);
                    handler.handle();
                    return;
                }

                if (DesignerHandler.userApplicationDataMap.containsKey(userId)) {
                    DesignerHandler handler = new DesignerHandler(update, keyboardFactory);
                    handler.handle();
                    return;
                }

                if (PRHandler.userApplicationDataMap.containsKey(userId)) {
                    PRHandler handler = new PRHandler(update, keyboardFactory);
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

            // Проверяем наличие пользователя в списке
            if (!hashedUsers.containsKey(String.valueOf(userId))) {
                // Создаём нового пользователя, если его нет в базе
                // Добавляем в список
                if (!userService.isUserExists(userId)) {
                    User user = userService.createUser(userId);
                    hashedUsers.put(String.valueOf(userId), user);
                }
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
