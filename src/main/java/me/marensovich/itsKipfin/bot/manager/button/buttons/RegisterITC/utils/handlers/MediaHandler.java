package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.ApplicationHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.UserMediaApplicationDTO;
import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.services.ApplicationService;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


/**
 * Обработчик процесса подачи заявки для направления "Медиа и контент".
 *
 * @author marensovich
 * @since 0.0.1
 * @version 0.0.1
 */
@Component
public class MediaHandler implements ApplicationHandler<UserMediaApplicationDTO> {
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
     * key = chatId пользователя, value = {@link UserMediaApplicationDTO}
     *
     * <p>Данные удаляются из map после создания/сброса заявки.</p>
     * @since 0.0.1
     */
    public static final Map<Long, UserMediaApplicationDTO> userApplicationDataMap = new HashMap<>();

    /**
     * Экземплярные поля
     * @since 0.0.1
     */
    private Long chatId;
    private Update update;
    private KeyboardFactory keyboardFactory;
    private UserMediaApplicationDTO data;

    /**
     * Конструктор, используемый Spring для внедрения {@link ApplicationService}.
     * <p>Помещает service в статическое поле, доступное всем handler-объектам.</p>
     *
     * @param applicationService сервис работы с заявками
     * @author marensovich
     * @since 0.0.1
     */
    @Autowired
    public MediaHandler(ApplicationService applicationService) {
        MediaHandler.applicationService = applicationService;
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
    public MediaHandler(Update update, KeyboardFactory keyboardFactory) {
        this.update = update;
        this.keyboardFactory = keyboardFactory;
        this.chatId = resolveChatId(update);
        this.data = userApplicationDataMap.computeIfAbsent(chatId, k -> new UserMediaApplicationDTO());
    }

    /**
     * Шаги процесса многошаговой формы.
     * @author marensovich
     * @since 0.0.1
     */
    public enum Step {
        /**
         * Ввод ФИО
         * @since 0.0.1
         */
        FULL_NAME,

        /**
         * Ввод номера телефона
         * @since 0.0.1
         */
        PHONE_NUMBER,

        /**
         * Ввод номера группы
         * @since 0.0.1
         */
        GROUP_NUMBER,

        /**
         * Описание опыта
         * @since 0.0.1
         */
        EXPERIENCE,

        /**
         * Наличие фотоаппарата
         * @since 0.0.1
         */
        PHOTO,

        /**
         * Подтверждение данных
         * @since 0.0.1
         */
        CONFIRMATION
    }

    /**
     * Главный метод: начинает/продолжает многошаговый сбор данных.
     *
     * <p>Логика:
     * <ul>
     *     <li>Если у пользователя уже есть активная заявка — отправляет предупреждение и выходит;</li>
     *     <li>Если пользователь отправил контакт и текущий шаг {@link Step#PHONE_NUMBER} мы получаем номер телефона в качестве строки и передаем напрямую методу</li>
     *     <li>Если пришёл текст (update.hasMessage()) — прокидывает текст в процессор {@link #processUserInput(String)};</li>
     *     <li>Иначе — запускает первый шаг {@link #askFullName()}.</li>
     * </ul>
     *
     * @throws RuntimeException если Telegram API вернуло ошибку при отправке сообщения
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handle() {
        Long userId = resolveUserId(update);

        if (applicationService.isActiveApplicationExists(userId)) {
            sendMessage("❗ У вас уже есть активная заявка на вступление в ИТС. Пожалуйста, дождитесь её рассмотрения.", chatId);
            userApplicationDataMap.remove(chatId);
            return;
        }

        if (update.hasMessage()){
            if (update.getMessage().hasText()){
                processUserInput(update.getMessage().getText().trim());
                return;
            }
            if (update.getMessage().hasContact() && data.getCurrentStep().equals(Step.PHONE_NUMBER)){
                processUserInput(update.getMessage().getContact().getPhoneNumber());
            }
        } else {
            askFullName();
        }

    }

    /**
     * Обработать результат YES администратора — одобрить заявку.
     *
     * <p>Действия:
     * <ol>
     *     <li>Загрузить заявку из БД;</li>
     *     <li>Уведомить заявителя;</li>
     *     <li>Редактировать сообщение в админ-чате (пометить как одобренную);</li>
     *     <li>Обновить статус заявки в БД.</li>
     * </ol>
     *
     * @param applicationId ID заявки (строка, парсится в Long)
     * @param update Update с callbackQuery от администратора
     * @throws RuntimeException при ошибках отправки сообщений в Telegram
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handleResultYes(String applicationId, Update update) {
        Application application = applicationService.getApplicationById(Long.valueOf(applicationId));
        UserMediaApplicationDTO userData = application.getDataObject(UserMediaApplicationDTO.class);

        // уведомление пользователю
        sendUserNotification(application.getUserId(),
                "✅ Ваша заявка на вступление в ИТС одобрена! " +
                        "Свяжитесь с руководителем @" + update.getCallbackQuery().getFrom().getUserName() + " для получения дальнейшей информации.");

        // обновление сообщения в админ-чате
        updateAdminMessage(update, userData, true);
        applicationService.updateApplicationStatus(application.getId(), Application.Status.APPROVED);
    }

    /**
     * Обработать результат NO администратора — отклонить заявку.
     *
     * <p>Аналогична {@link #handleResultYes(String, Update)} но ставит статус REJECTED и отправляет другой текст.</p>
     *
     * @param applicationId ID заявки
     * @param update Update с callbackQuery от администратора
     * @throws RuntimeException при ошибках отправки сообщений в Telegram
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handleResultNo(String applicationId, Update update) {
        Application application = applicationService.getApplicationById(Long.valueOf(applicationId));
        UserMediaApplicationDTO userData = application.getDataObject(UserMediaApplicationDTO.class);

        // уведомление пользователю
        sendUserNotification(application.getUserId(),
                "❌ Ваша заявка на вступление в ИТС отклонена! " +
                        "Свяжитесь с руководителем @" + update.getCallbackQuery().getFrom().getUserName() +
                        " для получения ответов на интересующие вопросы.");

        // обновление сообщения в админ-чате
        updateAdminMessage(update, userData, false);
        applicationService.updateApplicationStatus(application.getId(), Application.Status.REJECTED);
    }

    /**
     * Обработать текст, пришедший от пользователя, согласно текущему шагу {@code data.currentStep}.
     *
     * @param input текст от пользователя (предполагается, что != null)
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void processUserInput(String input) {
        // сохраняем метаданные о пользователе
        data.setTgId(String.valueOf(update.getMessage().getFrom().getId()));
        data.setMention("@" + update.getMessage().getFrom().getUserName());

        switch (data.getCurrentStep()) {
            case FULL_NAME -> handleFullName(input);
            case PHONE_NUMBER -> handlePhoneNumber(input);
            case GROUP_NUMBER -> handleGroupNumber(input);
            case EXPERIENCE -> handleExperience(input);
            case PHOTO -> handleHasPhoto(input);
            case CONFIRMATION -> handleConfirmation(input);
        }
    }

    /**
     * Запросить у пользователя ФИО и переключить шаг на {@link Step#FULL_NAME}.
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void askFullName() {
        sendMessage("Введите ваше ФИО (например: Иванов Иван Иванович):", chatId);
        data.setCurrentStep(Step.FULL_NAME);
    }

    /**
     * Обработать введённое ФИО:
     * <ul>
     *     <li>валидирует через {@link RegisterITCButton#FIO_REGEX};</li>
     *     <li>при корректном вводе — сохраняет и запрашивает телефон;</li>
     *     <li>при некорректном — просит повторить ввод.</li>
     * </ul>
     *
     * @param input введённое пользователем значение
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handleFullName(String input) {
        if (!input.matches(RegisterITCButton.FIO_REGEX)) {
            sendMessage("❌ Неверный формат ФИО. Только русские буквы, первая — заглавная.\nПример: Иванов Иван Иванович", chatId);
            askFullName();
            return;
        }
        data.setFullName(input);
        askPhoneNumber();
    }

    /**
     * Запросить номер телефона и переключить шаг на {@link Step#PHONE_NUMBER}.
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void askPhoneNumber() {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.HTML);
        message.setText("Отправьте ваш номер телефона:");
        message.setReplyMarkup(
                keyboardFactory.create()
                        .addContactButton("Отправить номер телефона")
                        .buildReplyKeyboard()
        );
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
        data.setCurrentStep(Step.PHONE_NUMBER);
    }

    /**
     * Обработать введённый телефон, далее переход к группе.
     *
     * @param number отправленный номер телефона
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handlePhoneNumber(String number) {
        data.setPhoneNumber(number);
        askGroupNumber();
    }

    /**
     * Запросить номер группы и переключить шаг на {@link Step#GROUP_NUMBER}.
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void askGroupNumber() {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.HTML);
        message.setText("Введите номер группы (например: 2ИСИП-1224 или 3ОИБАС-1024):");
        message.setReplyMarkup(Bot.getInstance().removeKeyboard());
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        data.setCurrentStep(Step.GROUP_NUMBER);
    }

    /**
     * Обработать введённый номер группы: Uppercase + проверка через {@link RegisterITCButton#GROUP_REGEX}.
     *
     * @param input введённый номер группы
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handleGroupNumber(String input) {
        if (!input.toUpperCase().matches(RegisterITCButton.GROUP_REGEX)) {
            sendMessage("❌ Неверный формат номера группы. Пример: 2ИСИП-1224 или 3ОИБАС-1024", chatId);
            askGroupNumber();
            return;
        }
        data.setGroupNumber(input.toUpperCase());
        askExperience();
    }

    /**
     * Запросить текст об опыте и переключить шаг на {@link Step#EXPERIENCE}.
     * @author marensovich
     * @since 0.0.1
     */
    private void askExperience() {
        sendMessage("Опишите ваш опыт (пару предложений):", chatId);
        data.setCurrentStep(Step.EXPERIENCE);
    }

    /**
     * Обработать введённый опыт (проверка на минимальную длину семантически).
     *
     * @param input введённый текст опыта
     * @author marensovich
     * @since 0.0.1
     */
    private void handleExperience(String input) {
        // Простая эвристика — минимум ~10 слов. При необходимости замените на более гибкую логику.
        if (input.trim().split("\\s+").length < 10) {
            sendMessage("❌ Слишком коротко. Опишите чуть подробнее (несколько предложений):", chatId);
            askExperience();
            return;
        }
        data.setExperience(input);
        askHasPhoto();
    }

    /**
     * Уточнить на наличие фотоаппарата и переключить шаг на {@link Step#PHOTO}.
     * @author marensovich
     * @since 0.0.1
     */
    private void askHasPhoto() {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.HTML);
        message.setText("Есть ли у вас фотоаппарат? (Да/Нет):");
        message.setReplyMarkup(
                keyboardFactory.create()
                        .addButton("Да")
                        .addButton("Нет")
                        .buildReplyKeyboard()
        );
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
        data.setCurrentStep(Step.PHOTO);
    }

    /**
     * Обработка информации о наличии фотоаппарата.
     *
     * @param input введённая ссылка
     * @author marensovich
     * @since 0.0.1
     */
    private void handleHasPhoto(String input) {
        Boolean hasPhoto = parseYesNo(input);
        if (hasPhoto == null) {
            sendMessage("Пожалуйста, ответьте 'Да' или 'Нет'", chatId);
            askHasPhoto();
            return;
        }
        data.setHasPhoto(hasPhoto);
        askConfirmation();
    }


    /**
     * Парсер для перевода текстового значения в {@link Boolean}
     * @param input Поступивший текст ("Да", "Нет")
     * @since 0.0.1
     * @author marensovich
     * @return {@code true} если "да", {@code false} если "нет"
     */
    private Boolean parseYesNo(String input) {
        if (input == null) return null;
        String normalized = input.trim().toLowerCase();
        if (normalized.equals("да") || normalized.equals("yes")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Запрос подтверждения у пользователя — показывает все введённые поля и предлагает "Да"/"Нет".
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void askConfirmation() {
        String confirmationText = String.format(
                """
                Проверьте введённые данные:
                
                <b>ФИО:</b> %s
                <b>Телефон:</b> %s
                <b>Группа:</b> %s
                <b>Опыт:</b> %s
                <b>Наличие фотоаппарата:</b> %s
        
                Подтверждаете данные? (Да/Нет)""",
                escape(data.getFullName()),
                escape(data.getPhoneNumber()),
                escape(data.getGroupNumber()),
                escape(data.getExperience()),
                escape(data.getHasPhoto() ? "Есть" : "Нет")
        );

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.HTML);
        message.setText(confirmationText);
        message.setReplyMarkup(
                keyboardFactory.create()
                        .addButton("Да")
                        .addButton("Нет")
                        .buildReplyKeyboard()
        );

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при отправке сообщения подтверждения", e);
        }
        data.setCurrentStep(Step.CONFIRMATION);
    }

    /**
     * Обработать ответ пользователя на подтверждение ("Да"/"Нет").
     *
     * @param input ввод пользователя
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handleConfirmation(String input) {
        String answer = input.toLowerCase();
        if (answer.equals("да") || answer.equals("yes")) {
            processApplicationConfirmation();
        } else if (answer.equals("нет") || answer.equals("no")) {
            sendMessage("🔄 Хорошо, начнем заново.", chatId);
            data.reset();
            askFullName();
        } else {
            sendMessage("Пожалуйста, ответьте 'Да' или 'Нет' (без кавычек).", chatId);
        }
    }

    /**
     * Заключительный шаг: сохранение заявки в БД и уведомление админов.
     *
     * <p>Порядок:
     * <ol>
     *     <li>Создать запись {@link Application} через {@link ApplicationService#createApplication}.</li>
     *     <li>Отправить уведомление в админ-топик с inline-кнопками Принять/Отклонить (callback содержит ID заявки).</li>
     *     <li>Сохранить messageId админ-сообщения в заявке (через applicationService.updateApplicationMessageId).</li>
     *     <li>Очистить временные данные и снять активную кнопку у пользователя.</li>
     * </ol>
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void processApplicationConfirmation() {
        sendMessage("✅ Спасибо! Ваша заявка сохранена.", chatId);

        Application application = applicationService.createApplication(
                Application.Departament.Media,
                data,
                Long.valueOf(data.getTgId()),
                null
        );

        Message adminMessage = sendAdminNotification(application);
        applicationService.updateApplicationMessageId(application.getId(), Long.valueOf(adminMessage.getMessageId()));

        userApplicationDataMap.remove(chatId);
        Bot.getInstance().getButtonManager().unsetActiveCommand(chatId);
    }

    /**
     * Отправить уведомление администраторам (в чат/топик) и вернуть отправленное {@link Message}.
     *
     * <p>Использует переменные окружения:
     * <ul>
     *     <li>TELEGRAM_NOTIFICATION_ID — id чата/топика для уведомлений;</li>
     *     <li>TG_TOPIC — id темы (при использовании форумов/топиков).</li>
     * </ul>
     *
     * @param application созданная заявка
     * @return отправленное сообщение (Telegram Message)
     * @throws RuntimeException если отправка сообщения не удалась
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public Message sendAdminNotification(Application application) {
        String adminNotificationText = String.format(
                """
                <b>Новая заявка от %s (%s):</b>
                
                <b>ФИО:</b> %s
                <b>Телефон:</b> %s
                <b>Группа:</b> %s
                <b>Опыт:</b> %s
                <b>Наличие фотоаппарата:</b> %s""",
                data.getMention(), data.getTgId(),
                escape(data.getFullName()),
                escape(data.getPhoneNumber()),
                escape(data.getGroupNumber()),
                escape(data.getExperience()),
                escape(data.getHasPhoto() ? "Есть" : "Нет")
        );

        SendMessage notify = new SendMessage();
        notify.setParseMode(ParseMode.HTML);
        notify.setChatId(System.getenv("TELEGRAM_NOTIFICATION_ID"));
        notify.setMessageThreadId(Integer.parseInt(System.getenv("TG_TOPIC")));
        notify.setText(adminNotificationText);
        notify.setReplyMarkup(keyboardFactory.create()
                .addInlineButton("Принять заявку",
                        RegisterITCButton.ITC_ADMIN_REG_DEFARAMENT_PREFIX + RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT +
                                ":YES:" + application.getId())
                .nextInlineRow()
                .addInlineButton("Отклонить заявку",
                        RegisterITCButton.ITC_ADMIN_REG_DEFARAMENT_PREFIX + RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT +
                                ":NO:" + application.getId())
                .buildInlineKeyboard()
        );

        try {
            return Bot.getInstance().execute(notify);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при отправке уведомления администраторам", e);
        }
    }


    /**
     * Обновить админское сообщение (edit), пометив заявку как одобренную/отклонённую.
     *
     * @param update Update с callbackQuery от администратора
     * @param userData данные пользователя (десериализованные из application.data)
     * @param approved true — одобрена, false — отклонена
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void updateAdminMessage(Update update, UserMediaApplicationDTO userData, boolean approved) {
        if (!update.hasCallbackQuery()) return;

        String statusText = approved ?
                "✅ Заявка одобрена администратором @" + update.getCallbackQuery().getFrom().getUserName() + "." :
                "❌ Заявка отклонена администратором @" + update.getCallbackQuery().getFrom().getUserName() + ".";

        String messageText = String.format(
                """
                <b>Новая заявка от %s (%s):</b>
                
                <b>ФИО:</b> %s
                <b>Телефон:</b> %s
                <b>Группа:</b> %s
                <b>Опыт:</b> %s
                <b>Наличие фотоаппарата:</b> %s
                
                %s""",
                userData.getMention(), userData.getTgId(),
                escape(userData.getFullName()),
                escape(userData.getPhoneNumber()),
                escape(userData.getGroupNumber()),
                escape(userData.getExperience()),
                escape(userData.getHasPhoto() ? "Есть" : "Нет"),
                statusText
        );

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setText(messageText);

        try {
            Bot.getInstance().execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при редактировании админского сообщения", e);
        }
    }


    @Override
    public void sendUserNotification(Long userId, String text) {
        ApplicationHandler.super.sendUserNotification(userId, text);
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
