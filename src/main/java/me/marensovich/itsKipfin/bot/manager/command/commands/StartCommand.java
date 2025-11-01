package me.marensovich.itsKipfin.bot.manager.command.commands;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.HelpButton;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * The type Start command.
 */
@Component
public class StartCommand implements Command {

    private final KeyboardFactory keyboardFactory;

    /**
     * Instantiates a new Start command.
     *
     * @param keyboardFactory the keyboard factory
     */
    public StartCommand(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public boolean isAdminRequired() {
        return false;
    }

    @Override
    public void execute(Update update) {
        Bot.getInstance().getCommandManager().setActiveCommand(update.getMessage().getChatId(), this);


        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText("Привет! Я бот помощник подразделения ИТС. Выбери нужное действие кнопкой.");

        message.setReplyMarkup(keyboardFactory.create()
                .addButton(HelpButton.class)
                .addButton(RegisterITCButton.class)
                .buildReplyKeyboard()
        );

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getChatId());
    }
}
