package me.marensovich.itsKipfin.bot.manager.button.buttons;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpButton implements Button {
    @Override
    public String getButtonText() {
        return "Помощь";
    }

    @Override
    public void handle(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText("Если вам нужна помощь, пожалуйста, свяжитесь с нашим отделом поддержки по адресу");

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
