package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RegisterITCHandler implements PrefixCallbackHandler {

    private final KeyboardFactory keyboardFactory;

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
                        new RegisterITCButton.ProjectTeamHandler(update, keyboardFactory).handle();

                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_DESIGNER ->
                        new RegisterITCButton.DesignerHandler(update).handle();

                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PR ->
                        new RegisterITCButton.PRHandler(update).handle();

                case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT ->
                        new RegisterITCButton.MediaHandler(update).handle();

                default -> Bot.getInstance()
                        .sendErrorMessage(chatId, "Invalid callback data: " + callbackData);
            }
        } else if (command == null) {
            Bot.getInstance().sendErrorMessage(chatId, "No active command found for this chat.");
        }
    }
}
