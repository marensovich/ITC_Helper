package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RegisterITCHandler implements PrefixCallbackHandler {
    @Override
    public String getPrefixCallbackData() {
        return RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PREFIX;
    }

    @Override
    public void handle(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        String[] parts = callbackData.split(":");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        RegisterITCButton command = (RegisterITCButton) Bot.getInstance()
                .getButtonManager()
                .getActiveCommand(chatId);

        if (command != null) {
            if (callbackData.startsWith(RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PREFIX)) {
                switch (parts[1]) {
                    case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM -> command.handleProjectTeamDepartament(update);
                    case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_DESIGNER -> command.handleDesignerDepartament(update);
                    case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PR -> command.handlePrDepartament(update);
                    case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT -> command.handleMediaDepartament(update);
                    case null, default -> Bot.getInstance().sendErrorMessage(update.getMessage().getChatId(), "Invalid callback data");
                }
            }
        }
    }
}
