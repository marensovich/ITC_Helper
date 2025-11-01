package me.marensovich.itsKipfin.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.callback.CallbackManager;
import me.marensovich.itsKipfin.bot.manager.command.CommandManager;
import me.marensovich.itsKipfin.bot.manager.update.UpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * The type Bot.
 */
@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Autowired
    @Getter
    private CommandManager commandManager;
    @Autowired
    @Getter
    private CallbackManager callbackManager;
    @Autowired
    @Getter
    private ButtonManager buttonManager;
    @Getter
    private static Bot instance;

    private final String botToken;
    private final String botUsername;

    @Autowired
    @Getter
    private UpdateManager updateManager;

    /**
     * Instantiates a new Bot.
     *
     * @param botToken    the bot token
     * @param botUsername the bot username
     */
    public Bot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        instance = this;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            updateManager.updateHandler(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendTextMessage(Long chatId, String text) {
        try {
            Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
            execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    /**
     * Send no access message.
     *
     * @param update the update
     */
    public void sendNoAccessMessage(Update update) {
        sendTextMessage(update.getMessage().getChatId(), "⛔ У вас нет прав для выполнения этой команды!");
    }

    /**
     * Send user private chat.
     *
     * @param update the update
     */
    public void sendUserPrivateChat(Update update) {
        sendTextMessage(update.getMessage().getChatId(), "💬 Пожалуйста, используйте личные сообщения, чтобы использовать эту команду.");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Remove keyboard reply keyboard remove.
     *
     * @return the reply keyboard remove
     */
    public ReplyKeyboardRemove removeKeyboard() {
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        keyboardRemove.setSelective(false);
        return keyboardRemove;
    }

    /**
     * Send error message.
     *
     * @param chatId the chat id
     * @param text   the text
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Show bot action.
     *
     * @param chatId     the chat id
     * @param actionType the action type
     */
    public void showBotAction(Long chatId, ActionType actionType) {
        SendChatAction chatAction = new SendChatAction();
        chatAction.setChatId(String.valueOf(chatId));
        chatAction.setAction(actionType);

        try {
            Bot.getInstance().execute(chatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
