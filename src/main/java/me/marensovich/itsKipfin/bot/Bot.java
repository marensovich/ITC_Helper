package me.marensovich.itsKipfin.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.manager.callback.CallbackManager;
import me.marensovich.itsKipfin.bot.manager.command.CommandManager;
import me.marensovich.itsKipfin.bot.manager.update.UpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Autowired @Getter private CommandManager commandManager;
    @Autowired @Getter private CallbackManager callbackManager;
    @Getter private static Bot instance;

    private final String botToken;
    private final String botUsername;

    @Autowired @Getter private UpdateManager updateManager;

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

    public void sendNoAccessMessage(Update update) {
        sendTextMessage(update.getMessage().getChatId(), "⛔ У вас нет прав для выполнения этой команды!");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public ReplyKeyboardRemove removeKeyboard(){
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        keyboardRemove.setSelective(false);
        return keyboardRemove;
    }

    public void sendErrorMessage(Long chatId, String text) {
        Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

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
