package me.marensovich.itsKipfin.bot.manager.command.commands;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.HelpButton;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.utils.UniversalKeyboardBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartCommand implements Command {

    private final UniversalKeyboardBuilder universalKeyboardBuilder;

    public StartCommand(UniversalKeyboardBuilder universalKeyboardBuilder) {
        this.universalKeyboardBuilder = universalKeyboardBuilder;
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

        message.setReplyMarkup(universalKeyboardBuilder
                .addButton(HelpButton.class)
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
