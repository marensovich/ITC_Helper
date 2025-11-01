package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

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
