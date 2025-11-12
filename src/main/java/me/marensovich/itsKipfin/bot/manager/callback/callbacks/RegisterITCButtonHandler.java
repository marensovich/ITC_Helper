package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик нажатия кнопки регистрации ИТС.
 * <p>
 * Используется для точного совпадения callbackData с кнопкой регистрации.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class RegisterITCButtonHandler implements CallbackHandler {

    @Override
    public String getCallbackData() {
        return RegisterITCButton.ITC_REGISTRATION_CALLBACK;
    }

    @Override
    public void handle(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        RegisterITCButton command = (RegisterITCButton) Bot.getInstance()
                .getButtonManager()
                .getActiveCommand(userId);

        if (command != null) {
            command.handleRegButton(update);
        }
    }
}
