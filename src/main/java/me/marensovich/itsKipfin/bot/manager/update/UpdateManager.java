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
 * The type Update manager.
 */
@Component
public class UpdateManager {

    private final UserService userService;
    private final ButtonManager buttonManager;
    private final KeyboardFactory keyboardFactory;

    /**
     * Instantiates a new Update manager.
     *
     * @param userService     the user service
     * @param buttonManager   the button manager
     * @param keyboardFactory the keyboard factory
     */
    public UpdateManager(UserService userService, ButtonManager buttonManager, KeyboardFactory keyboardFactory) {
        this.userService = userService;
        this.buttonManager = buttonManager;
        this.keyboardFactory = keyboardFactory;
    }

    /**
     * Update handler.
     *
     * @param update the update
     * @throws TelegramApiException the telegram api exception
     */
    public void updateHandler(Update update) throws TelegramApiException {

        if (update.hasMessage() || update.hasCallbackQuery()) {
            if (update.hasMessage()) {
                if (userService.isUserExists(update.getMessage().getFrom().getId())) {
                    if (!Bot.getInstance().getCommandManager().hasActiveCommand(update.getMessage().getFrom().getId())) {
                        if (update.getMessage().hasText()) {
                            if (update.getMessage().getText().startsWith("/")) {
                                if (!Bot.getInstance().getCommandManager().executeCommand(update)) {
                                    String text = "Команда не распознана, проверьте правильность написания команды. \n\n" +
                                            "Команды с доп. параметрами указаны отдельной графой в информации. Подробнее в /help.";
                                    SendMessage message = new SendMessage();
                                    message.setChatId(update.getMessage().getChatId().toString());
                                    message.setText(text);
                                    try {
                                        Bot.getInstance().execute(message);
                                    } catch (TelegramApiException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return;
                                }
                            }
                            if (RegisterITCButton.ProjectTeamHandler.userApplicationDataMap.containsKey(update.getMessage().getFrom().getId())) {
                                RegisterITCButton.ProjectTeamHandler handler = new RegisterITCButton.ProjectTeamHandler(update, keyboardFactory);
                                handler.handle();
                            }
                            buttonManager.handle(update);
                        }
                    } else {
                        Bot.getInstance().getCommandManager().executeCommand(update);
                        return;
                    }
                } else {
                    userService.createUser(update.getMessage().getFrom().getId());
                }
            }
            if (update.hasCallbackQuery()) {
                if (userService.isUserExists(update.getCallbackQuery().getFrom().getId())) {
                    boolean handled = Bot.getInstance().getCallbackManager().handleCallback(update);
                    if (!handled) {
                        SendMessage errorMsg = new SendMessage();
                        errorMsg.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
                        errorMsg.setText("Действие не распознано, попробуйте ещё раз");
                        try {
                            Bot.getInstance().execute(errorMsg);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    userService.createUser(update.getCallbackQuery().getFrom().getId());
                }
            }
        }
    }

}