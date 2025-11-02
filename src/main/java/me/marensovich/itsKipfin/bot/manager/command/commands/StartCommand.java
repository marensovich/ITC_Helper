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
 * Команда /start.
 * <p>
 * Приветствует пользователя и выводит главное меню с кнопками.
 * <p>
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 */
@Component
public class StartCommand implements Command {

    private final KeyboardFactory keyboardFactory;

    /**
     * Конструктор команды.
     *
     * @param keyboardFactory фабрика клавиатур
     * @author marensovich
     * @since 0.0.1
     */
    public StartCommand(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    /**
     * Имя команды.
     *
     * @return "/start"
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public String getName() {
        return "/start";
    }

    /**
     * Требуется ли админский доступ.
     *
     * @return false
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public boolean isAdminRequired() {
        return false;
    }

    /**
     * Выполнение команды.
     * <p>
     * Отправляет приветственное сообщение с кнопками {@link HelpButton} и {@link RegisterITCButton}.
     *
     * @param update объект обновления Telegram
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();

        // Помечаем команду как активную
        Bot.getInstance().getCommandManager().setActiveCommand(chatId, this);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Привет! Я бот-помощник подразделения ИТС. Выбери нужное действие кнопкой.");

        // Формируем клавиатуру
        message.setReplyMarkup(keyboardFactory.create()
                .addButton(HelpButton.class)
                .addButton(RegisterITCButton.class)
                .buildReplyKeyboard()
        );

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(chatId, "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new RuntimeException(e);
        } finally {
            // Снимаем активную команду после отправки
            Bot.getInstance().getCommandManager().unsetActiveCommand(chatId);
        }
    }
}
