package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.DesignerHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.MediaHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.PRHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.ProjectTeamHandler;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик callback-запросов с префиксом регистрации ИТС.
 * <p>
 * Используется для выбора подразделения при регистрации пользователя.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class RegisterITCHandler implements PrefixCallbackHandler {

    private final KeyboardFactory keyboardFactory;

    /**
     * Конструктор обработчика.
     *
     * @param keyboardFactory фабрика клавиатур
     * @author marensovich
     * @since 0.0.1
     */
    public RegisterITCHandler(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public String getPrefixCallbackData() {
        return RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PREFIX;
    }

    @Override
    public void handle(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        String[] parts = callbackData.split(":");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        RegisterITCButton command = (RegisterITCButton) Bot.getInstance()
                .getButtonManager()
                .getActiveCommand(chatId);

        if (command != null && parts.length > 1) {
            switch (parts[1]) {
                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM ->
                        new ProjectTeamHandler(update, keyboardFactory).handle();

                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_DESIGNER ->
                        new DesignerHandler(update, keyboardFactory).handle();

                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PR ->
                        new PRHandler(update, keyboardFactory).handle();

                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT ->
                        new MediaHandler(update, keyboardFactory).handle();

                default -> Bot.getInstance()
                        .sendErrorMessage(chatId, "Неверные данные callback: " + callbackData);
            }
        } else if (command == null) {
            Bot.getInstance().sendErrorMessage(chatId, "Нет активной команды для этого чата.");
        }
    }
}
