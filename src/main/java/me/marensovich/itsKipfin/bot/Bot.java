package me.marensovich.itsKipfin.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.callback.CallbackManager;
import me.marensovich.itsKipfin.bot.manager.command.CommandManager;
import me.marensovich.itsKipfin.bot.manager.update.UpdateManager;
import me.marensovich.itsKipfin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс Telegram-бота.
 * <p>
 * Отвечает за получение обновлений, маршрутизацию команд, обработку callback-запросов
 * и отправку сообщений пользователям.
 * Использует менеджеры {@link CommandManager}, {@link CallbackManager}, {@link ButtonManager}
 * и {@link UpdateManager} для распределения логики.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Slf4j
public class Bot extends TelegramLongPollingBot {

    /** Менеджер команд Telegram-бота. */
    @Autowired
    @Getter
    private CommandManager commandManager;

    @Autowired private UserService userService;

    /** Менеджер callback-запросов (обработка inline-кнопок). */
    @Autowired
    @Getter
    private CallbackManager callbackManager;

    /** Менеджер обычных кнопок (reply и inline). */
    @Autowired
    @Getter
    private ButtonManager buttonManager;

    /** Глобальный экземпляр бота (Singleton). */
    @Getter
    private static Bot instance;

    /** Токен Telegram-бота. */
    private final String botToken;

    /** Имя Telegram-бота (username). */
    private final String botUsername;

    /** Менеджер обработки обновлений. */
    @Autowired
    @Getter
    private UpdateManager updateManager;

    /**
     * Конструктор Telegram-бота.
     *
     * @param botToken    токен Telegram-бота (из настроек)
     * @param botUsername имя Telegram-бота (username)
     * @author marensovich
     * @since 0.0.1
     */
    public Bot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        instance = this;
    }

    /**
     * Главный обработчик обновлений.
     * <p>
     * Делегирует входящие сообщения и callback-и {@link UpdateManager}.
     *
     * @param update входящее обновление от Telegram API
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            updateManager.updateHandler(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при обработке обновления Telegram", e);
        }
    }

    /**
     * Отправляет простое текстовое сообщение пользователю.
     *
     * @param chatId идентификатор чата
     * @param text   текст сообщения
     * @author marensovich
     * @since 0.0.1
     */
    private void sendTextMessage(Long chatId, String text) {
        try {
            Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
            execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
        }
    }

    /**
     * Отправляет сообщение о недостатке прав пользователю.
     *
     * @param update объект обновления Telegram
     * @author marensovich
     * @since 0.0.1
     */
    public void sendNoAccessMessage(Update update) {
        sendTextMessage(update.getMessage().getChatId(),
                "⛔ У вас нет прав для выполнения этой команды!");
    }

    /**
     * Отправляет сообщение о необходимости использования личных сообщений.
     *
     * @param update объект обновления Telegram
     * @author marensovich
     * @since 0.0.1
     */
    public void sendUserPrivateChat(Update update) {
        sendTextMessage(update.getMessage().getChatId(),
                "💬 Пожалуйста, используйте личные сообщения, чтобы использовать эту команду.");
    }

    /**
     * Возвращает username Telegram-бота.
     *
     * @return имя пользователя (username)
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Возвращает токен Telegram-бота.
     *
     * @return токен Telegram API
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onRegister() {
        userService.getAllUsers().forEach(user -> {
            updateManager.hashedUsers.put(String.valueOf(user.getUserId()), user);
        });
    }

    /**
     * Удаляет клавиатуру из чата.
     *
     * @return объект {@link ReplyKeyboardRemove} для удаления клавиатуры
     * @author marensovich
     * @since 0.0.1
     */
    public ReplyKeyboardRemove removeKeyboard() {
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        keyboardRemove.setSelective(false);
        return keyboardRemove;
    }

    /**
     * Отправляет сообщение об ошибке пользователю.
     *
     * @param chatId идентификатор чата
     * @param text   текст ошибки
     * @author marensovich
     * @since 0.0.1
     */
    public void sendErrorMessage(Long chatId, String text) {
        Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setReplyMarkup(null);
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка отправки сообщения об ошибке", e);
        }
    }

    /**
     * Отображает текущее действие бота (например, "печатает...").
     *
     * @param chatId     идентификатор чата
     * @param actionType тип действия ({@link ActionType})
     * @author marensovich
     * @since 0.0.1
     */
    public void showBotAction(Long chatId, ActionType actionType) {
        SendChatAction chatAction = new SendChatAction();
        chatAction.setChatId(String.valueOf(chatId));
        chatAction.setAction(actionType);

        try {
            Bot.getInstance().execute(chatAction);
        } catch (TelegramApiException e) {
            log.error("Ошибка отображения действия бота: {}", e.getMessage());
        }
    }

}
