package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.BaseApplicationDTO;
import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.utils.exception.exceptions.BotException;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Интерфейс для обработчиков анкет заявок на направления
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
public interface ApplicationHandler <T extends BaseApplicationDTO> {
    /**
     * Начало обработки события
     * @since 0.0.1
     * @author marensovich
     */
    void handle();

    /**
     * Обработчик принятия заявки администратором
     *
     * @param applicationId ID заявки
     * @param update текущий {@link Update}
     * @since 0.0.1
     * @author marensovich
     */
    void handleResultYes(String applicationId, Update update);

    /**
     * Обработчик отказа заявки администратором
     *
     * @param applicationId ID заявки
     * @param update текущий {@link Update}
     * @since 0.0.1
     * @author marensovich
     */
    void handleResultNo(String applicationId, Update update);

    /**
     * Обработка ввода данных
     *
     * @param input the input
     * @since 0.0.1
     * @author marensovich
     */
    void processUserInput(String input);

    /**
     * Запрос ФИО
     * @since 0.0.1
     * @author marensovich
     */
    void askFullName();

    /**
     * Обработка ФИО
     *
     * @param input введенный текст
     * @since 0.0.1
     * @author marensovich
     */
    void handleFullName(String input);

    /**
     * Запрос номера телефона
     * @since 0.0.1
     * @author marensovich
     */
    void askPhoneNumber();

    /**
     * Обработка номера телефона
     *
     * @param number номер телефона
     * @since 0.0.1
     * @author marensovich
     */
    void handlePhoneNumber(String number);

    /**
     * Запрос номера группы
     * @since 0.0.1
     * @author marensovich
     */
    void askGroupNumber();

    /**
     * Обработка номера группы
     *
     * @param number номер группы
     * @since 0.0.1
     * @author marensovich
     */
    void handleGroupNumber(String number);


    /**
     * Запрос подтверждения
     * @since 0.0.1
     * @author marensovich
     */
    void askConfirmation();

    /**
     * Обработка подтверждения
     *
     * @param input вводимый результат подтверждения
     * @since 0.0.1
     * @author marensovich
     */
    void handleConfirmation(String input);

    /**
     * Обработка подтверждения заявки
     * @since 0.0.1
     * @author marensovich
     */
    void processApplicationConfirmation();

    /**
     * Отправить уведомление администратору
     *
     * @param application заполненная заявка
     * @return обьект {@link Message} с отправленным сообщением
     * @since 0.0.1
     * @author marensovich
     */
    Message sendAdminNotification(Application application);

    /**
     * Обновить админское сообщение (edit), пометив заявку как одобренную/отклонённую.
     *
     * @param update Update с callbackQuery от администратора
     * @param userData данные пользователя (десериализованные из application.data)
     * @param approved true — одобрена, false — отклонена
     * @author marensovich
     * @since 0.0.1
     */
    void updateAdminMessage(Update update, T userData, boolean approved);

    /**
     * Экранирует HTML-символы в тексте, чтобы избежать поломки парсинга HTML у Telegram.
     *
     * @param text исходный текст
     * @return экранированный текст (если input == null — возвращается пустая строка)
     * @author marensovich
     * @since 0.0.1
     */
    default String escape(String text) {
        return text == null ? "" : text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }


    /**
     * Утилитный метод быстрой отправки текстового сообщения в текущий chatId (используется внутри handler).
     *
     * @param text   текст сообщения (HTML-неэкранированный)
     * @param chatId the chat id
     * @author marensovich
     * @since 0.0.1
     */
    default void sendMessage(String text, Long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setParseMode(ParseMode.HTML);
        msg.setText(text);
        try {
            Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
            Bot.getInstance().execute(msg);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(chatId, "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Отправить приватное уведомление пользователю.
     *
     * @param userId id пользователя (chat id в личных сообщениях)
     * @param text текст уведомления (может содержать HTML, экранируется при необходимости)
     * @author marensovich
     * @since 0.0.1
     */
    default void sendUserNotification(Long userId, String text) {
        SendMessage notify = new SendMessage();
        notify.setParseMode(ParseMode.HTML);
        notify.setText(text);
        notify.setChatId(userId);
        try {
            Bot.getInstance().showBotAction(userId, ActionType.TYPING);
            Bot.getInstance().execute(notify);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(userId, "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Утилитный метод быстрой отправки текстового сообщения в текущий chatId (используется внутри handler).
     *
     * @param text текст сообщения (HTML-неэкранированный)
     * @author marensovich
     * @since 0.0.1
     */
    default void sendMessage(String text, String chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setParseMode(ParseMode.HTML);
        msg.setText(text);
        try {
            Bot.getInstance().showBotAction(Long.valueOf(chatId), ActionType.TYPING);
            Bot.getInstance().execute(msg);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(Long.valueOf(chatId), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Разрешить userId (идентификатор отправителя) из {@link Update}.
     *
     * @param update текущий update
     * @return userId
     * @throws IllegalArgumentException если userId нельзя получить
     * @author marensovich
     * @since 0.0.1
     */
    default Long resolveUserId(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
            return update.getCallbackQuery().getFrom().getId();
        } else if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else {
            throw new IllegalArgumentException("Cannot determine userId from update");
        }
    }

    /**
     * Разрешить chatId из {@link Update}.
     *
     * @param update текущий update
     * @return chatId (Long)
     * @throws IllegalArgumentException если chatId нельзя получить
     * @author marensovich
     * @since 0.0.1
     */
    default Long resolveChatId(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
            return update.getCallbackQuery().getFrom().getId();
        } else if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else {
            throw new IllegalArgumentException("Cannot determine chatId from update");
        }
    }
}
