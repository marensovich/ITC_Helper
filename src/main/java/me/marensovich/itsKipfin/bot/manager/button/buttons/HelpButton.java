package me.marensovich.itsKipfin.bot.manager.button.buttons;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Кнопка "Помощь" для бота.
 * <p>
 * При нажатии на кнопку отправляет пользователю сообщение с инструкцией по получению поддержки.
 * Активирует кнопку как текущую для пользователя на время обработки.
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 */
@Component
public class HelpButton implements Button {

    /**
     * Получить текст кнопки.
     *
     * @return текст кнопки "Помощь"
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public String getButtonText() {
        return "Помощь";
    }

    /**
     * Обработать нажатие кнопки.
     *
     * @param update объект Update из Telegram, содержащий информацию о событии
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handle(Update update) {
        Bot.getInstance().showBotAction(update.getMessage().getFrom().getId(), ActionType.TYPING);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText("Если вам нужна помощь, пожалуйста, свяжитесь с нашим отделом поддержки по адресу");

        try {
            Bot.getInstance().getButtonManager().setActiveCommand(update.getMessage().getFrom().getId(), this);
            Bot.getInstance().execute(message);
        } catch (TelegramApiException ignored) {
        } finally {
            Bot.getInstance().getButtonManager().unsetActiveCommand(update.getMessage().getFrom().getId());
        }

    }
}
