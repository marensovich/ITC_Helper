package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.ApplicationHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.UserPRApplicationDTO;
import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.services.ApplicationService;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class PRHandler implements ApplicationHandler {
    /**
     * ApplicationService внедряется Spring-ом в static поле через конструктор с {@link Autowired}.
     * Это позволяет создавать экземпляры handler'а вручную (new ProjectTeamHandler(update, keyboardFactory))
     * и иметь доступ к applicationService.
     *
     * <p>Важно: подход с static-полем выбран для простоты интеграции с текущей архитектурой.
     * В более строгой архитектуре лучше пользоваться фабрикой/биновым прототипом.</p>
     *
     */
    private static ApplicationService applicationService;

    /**
     * Временное хранилище данных заявок для пользователей:
     * key = chatId пользователя, value = {@link UserPRApplicationDTO}
     *
     * <p>Данные удаляются из map после создания/сброса заявки.</p>
     * @since 0.0.1
     */
    public static final Map<Long, UserPRApplicationDTO> userApplicationDataMap = new HashMap<>();

    /**
     * Регулярные выражения для валидации полей
     * @since 0.0.1
     */
    private static final String FIO_REGEX = "^[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+)?$";
    private static final String GROUP_REGEX = "^[1-4](ОИБАС|ИСИП|ИИС)-\\d{1,4}$";

    /**
     * Экземплярные поля
     * @since 0.0.1
     */
    private Long chatId;
    private Update update;
    private KeyboardFactory keyboardFactory;
    private UserPRApplicationDTO data;

    /**
     * Конструктор, используемый Spring для внедрения {@link ApplicationService}.
     * <p>Помещает service в статическое поле, доступное всем handler-объектам.</p>
     *
     * @param applicationService сервис работы с заявками
     * @author marensovich
     * @since 0.0.1
     */
    @Autowired
    public PRHandler(ApplicationService applicationService) {
        PRHandler.applicationService = applicationService;
    }

    /**
     * Конструктор для runtime-использования: создаём handler для конкретного {@code update}.
     *
     * @param update текущий {@link Update} (сообщение/коллбэк)
     * @param keyboardFactory фабрика клавиатур (используется при подтверждении)
     * @throws IllegalArgumentException если невозможно разрешить chatId из update
     * @author marensovich
     * @since 0.0.1
     */
    public PRHandler(Update update, KeyboardFactory keyboardFactory) {
        this.update = update;
        this.keyboardFactory = keyboardFactory;
        this.chatId = resolveChatId(update);
        this.data = userApplicationDataMap.computeIfAbsent(chatId, k -> new UserPRApplicationDTO());
    }

    /**
     * Отправляет простое текстовое подтверждение выбора направления
     * и удаляет клавиатуру (использует {@link Bot#removeKeyboard()}).
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handle() {
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.enableHtml(true);
        message.setText("В разработке. Ждите обновлений");

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleResultYes(String applicationId, Update update) {

    }

    @Override
    public void handleResultNo(String applicationId, Update update) {

    }

    @Override
    public void processUserInput(String input) {

    }

    @Override
    public void askFullName() {

    }

    @Override
    public void handleFullName(String input) {

    }

    @Override
    public void askPhoneNumber() {

    }

    @Override
    public void handlePhoneNumber(String number) {

    }

    @Override
    public void askGroupNumber() {

    }

    @Override
    public void handleGroupNumber(String number) {

    }

    @Override
    public void askConfirmation() {

    }

    @Override
    public void handleConfirmation(String input) {

    }

    @Override
    public void processApplicationConfirmation() {

    }

    @Override
    public Message sendAdminNotification(Application application) {
        return null;
    }

    @Override
    public String escape(String text) {
        return ApplicationHandler.super.escape(text);
    }

    @Override
    public void sendMessage(String text, Long chatId) {
        ApplicationHandler.super.sendMessage(text, chatId);
    }

    @Override
    public void sendUserNotification(Long userId, String text) {
        ApplicationHandler.super.sendUserNotification(userId, text);
    }

    @Override
    public void sendMessage(String text, String chatId) {
        ApplicationHandler.super.sendMessage(text, chatId);
    }

    @Override
    public Long resolveUserId(Update update) {
        return ApplicationHandler.super.resolveUserId(update);
    }

    @Override
    public Long resolveChatId(Update update) {
        return ApplicationHandler.super.resolveChatId(update);
    }

}
