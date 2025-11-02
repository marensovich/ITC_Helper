package me.marensovich.itsKipfin.bot.manager.button.buttons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.services.ApplicationService;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Register itc button.
 */
@Component
public class RegisterITCButton implements Button {

    /**
     * The constant ITC_REGISTRATION_CALLBACK.
     */
    public static final String ITC_REGISTRATION_CALLBACK = "itc_reg_button_callback";

    /**
     * The constant ITC_REGISTRATION_DEPARTAMENT_PREFIX.
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_PREFIX = "itc_reg:";
    /**
     * The constant ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM.
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM = "project_team";
    /**
     * The constant ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT.
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT = "video_content";
    /**
     * The constant ITC_REGISTRATION_DEPARTAMENT_PR.
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_PR = "pr";
    /**
     * The constant ITC_REGISTRATION_DEPARTAMENT_DESIGNER.
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_DESIGNER = "designer";


    /**
     * The constant ITC_ADMIN_REG_DEFARAMENT_PREFIX.
     */
    public static final String ITC_ADMIN_REG_DEFARAMENT_PREFIX = "itc_admin_reg:";

    private final KeyboardFactory keyboardFactory;

    /**
     * Instantiates a new Register itc button.
     *
     * @param keyboardFactory the keyboard factory
     */
    public RegisterITCButton(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public String getButtonText() {
        return "Вступление в ИТС";
    }

    @Override
    public void handle(Update update) {
        if (!update.getMessage().getChatId().equals(update.getMessage().getFrom().getId())) {
            Bot.getInstance().sendUserPrivateChat(update);
            return;
        }

        Bot.getInstance().getButtonManager().setActiveCommand(update.getMessage().getFrom().getId(), this);
        Bot.getInstance().showBotAction(update.getMessage().getFrom().getId(), ActionType.TYPING);


        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(
                """
                        <b>Вступление в ИТС.</b>
                        
                        Для вступления в ИТС вам необходимо подать заявку на вступление.
                        Подать заявку можно используя кнопку ниже.
                        """
        );
        message.setParseMode(ParseMode.HTML);
        message.setReplyMarkup(keyboardFactory.create()
                .addInlineButton("Вступить в ИТС", ITC_REGISTRATION_CALLBACK)
                .buildInlineKeyboard()
        );

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handle reg button.
     *
     * @param update the update
     */
    public void handleRegButton(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);

        // Отправка общей информации
        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        infoMessage.setText(
                """
                        <b>Краткая информация о направлениях:</b>
                        
                        <b>1. Проектная команда</b> — создание цифровых продуктов.
                        <b>2. Медиа и контент</b> — видео, фото, социальные сети.
                        <b>3. PR и коммуникации</b> — продвижение проектов.
                        <b>4. Дизайнеры</b> — визуальный стиль, макеты, графика.
                        """
        );
        infoMessage.setParseMode(ParseMode.HTML);

        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText(
                """
                        <b>Вы практически в ИТС!</b> Остался один шаг — выберите направление:
                        
                        Для завершения регистрации:
                        1. Выберите направление ниже.
                        2. Заполните форму после выбора.
                        3. Дождитесь подтверждения от руководителя.
                        """
        );
        message.setParseMode(ParseMode.HTML);
        message.setReplyMarkup(keyboardFactory.create()
                .addInlineButton("Проектная команда", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM)
                .nextInlineRow()
                .addInlineButton("Медиа и контент", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT)
                .nextInlineRow()
                .addInlineButton("PR и Коммуникации", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_PR)
                .nextInlineRow()
                .addInlineButton("Дизайнеры", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_DESIGNER)
                .buildInlineKeyboard()
        );

        try {
            Bot.getInstance().execute(infoMessage);
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // ======= Вложенные классы для каждого направления =======

    /**
     * The type Project team handler.
     */
    @Component
    public static class ProjectTeamHandler {

        // Репозиторий, внедряется один раз через Spring
        private static ApplicationService applicationService;

        /**
         * Instantiates a new Project team handler.
         *
         * @param applicationService the application service
         */
        @Autowired
        public ProjectTeamHandler(ApplicationService applicationService) {
            ProjectTeamHandler.applicationService = applicationService;
        }

        /**
         * The constant userApplicationDataMap.
         */
// --- Данные пользователей ---
        public static final Map<Long, UserApplicationData> userApplicationDataMap = new HashMap<>();

        // --- Регулярки ---
        private static final String FIO_REGEX = "^[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+)?$";
        private static final String PHONE_REGEX = "^\\+?\\d{11}$";
        private static final String GITHUB_REGEX = "^(https?://)?(www\\.)?(github|gitlab)\\.com/[A-Za-z0-9_-]+/?$";
        private static final String GROUP_REGEX = "^[1-4](ОИБАС|ИСИП|ИИС)-\\d{1,4}$";

        // --- Поля экземпляра ---
        private Long chatId;
        private Update update;
        private KeyboardFactory keyboardFactory;
        private UserApplicationData data;

        /**
         * Instantiates a new Project team handler.
         *
         * @param update          the update
         * @param keyboardFactory the keyboard factory
         */
// --- Конструктор для runtime-создания хэндлера ---
        public ProjectTeamHandler(Update update, KeyboardFactory keyboardFactory) {
            this.update = update;
            this.keyboardFactory = keyboardFactory;

            Long resolvedChatId;
            if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
                resolvedChatId = update.getCallbackQuery().getFrom().getId();
            } else if (update.hasMessage()) {
                resolvedChatId = update.getMessage().getChatId();
            } else {
                throw new IllegalArgumentException("Cannot determine chatId from update");
            }

            this.chatId = resolvedChatId;
            this.data = userApplicationDataMap.computeIfAbsent(chatId, k -> new UserApplicationData());
        }

        /**
         * Handle.
         */
        public void handle() {
            Long id;
            if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
                id = update.getCallbackQuery().getFrom().getId();
            } else if (update.hasMessage()) {
                id = update.getMessage().getFrom().getId();
            } else {
                throw new IllegalArgumentException("Cannot determine userId from update");
            }
            if (applicationService.isActiveApplicationExists(id)) {
                sendMessage("❗ У вас уже есть активная заявка на вступление в ИТС. Пожалуйста, дождитесь её рассмотрения.");
                Bot.getInstance().getButtonManager().unsetActiveCommand(chatId);
                return;
            }
            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText().trim();

                data.tgId = String.valueOf(update.getMessage().getFrom().getId());
                data.mention = "@" + update.getMessage().getFrom().getUserName();

                switch (data.getCurrentStep()) {
                    case FULL_NAME -> handleFullName(text);
                    case PHONE_NUMBER -> handlePhoneNumber(text);
                    case GROUP_NUMBER -> handleGroupNumber(text);
                    case EXPERIENCE -> handleExperience(text);
                    case GITHUB -> handleGitHub(text);
                    case STACK -> handleStack(text);
                    case CONFIRMATION -> handleConfirmation(text);
                }
            } else {
                askFullName();
            }
        }

        // --- 1. ФИО ---
        private void askFullName() {
            sendMessage("Введите ваше ФИО (например: Иванов Иван Иванович):");
            data.setCurrentStep(Step.FULL_NAME);
        }

        private void handleFullName(String input) {
            if (!input.matches(FIO_REGEX)) {
                sendMessage("❌ Неверный формат ФИО. Только русские буквы, первая — заглавная.\nПример: Иванов Иван Иванович");
                askFullName();
                return;
            }
            data.setFullName(input);
            askPhoneNumber();
        }

        // --- 2. Телефон ---
        private void askPhoneNumber() {
            sendMessage("Введите номер телефона (например: +79001234567):");
            data.setCurrentStep(Step.PHONE_NUMBER);
        }

        private void handlePhoneNumber(String input) {
            if (!input.matches(PHONE_REGEX)) {
                sendMessage("❌ Неверный формат телефона. Используйте только цифры, можно с '+', 11 символов.");
                askPhoneNumber();
                return;
            }
            data.setPhoneNumber(input);
            askGroupNumber();
        }

        // --- 3. Группа ---
        private void askGroupNumber() {
            sendMessage("Введите номер группы (например: 2ИСИП-1224 или 3ОИБАС-1024):");
            data.setCurrentStep(Step.GROUP_NUMBER);
        }

        private void handleGroupNumber(String input) {
            if (!input.toUpperCase().matches(GROUP_REGEX)) {
                sendMessage("❌ Неверный формат номера группы. Пример: 2ИСИП-1224 или 3ОИБАС-1024");
                askGroupNumber();
                return;
            }
            data.setGroupNumber(input.toUpperCase());
            askExperience();
        }

        // --- 4. Опыт ---
        private void askExperience() {
            sendMessage("Опишите ваш опыт (пару предложений):");
            data.setCurrentStep(Step.EXPERIENCE);
        }

        private void handleExperience(String input) {
            if (input.trim().split(" ").length < 10) {
                sendMessage("❌ Слишком коротко. Опишите чуть подробнее:");
                askExperience();
                return;
            }
            data.setExperience(input);
            askGitHub();
        }

        // --- 5. GitHub ---
        private void askGitHub() {
            sendMessage("Укажите ссылку на ваш GitHub/GitLab (пример: https://github.com/username):");
            data.setCurrentStep(Step.GITHUB);
        }

        private void handleGitHub(String input) {
            if (!input.matches(GITHUB_REGEX)) {
                sendMessage("❌ Неверная ссылка на GitHub/GitLab. Попробуйте снова:");
                askGitHub();
                return;
            }
            data.setGitHub(input);
            askStack();
        }

        // --- 6. Стек ---
        private void askStack() {
            sendMessage("Введите стек технологий (например: Java, Spring, SQL):");
            data.setCurrentStep(Step.STACK);
        }

        private void handleStack(String input) {
            if (input.isEmpty() || input.length() > 200) {
                sendMessage("❌ Некорректный стек. Попробуйте снова:");
                askStack();
                return;
            }
            data.setStack(input);
            askConfirmation();
        }

        // --- 7. Подтверждение ---
        private void askConfirmation() {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setParseMode(ParseMode.HTML);
            message.setText(
                    "Проверьте введённые данные:\n\n" +
                            "<b>ФИО:</b> " + escape(data.getFullName()) + "\n" +
                            "<b>Телефон:</b> " + escape(data.getPhoneNumber()) + "\n" +
                            "<b>Группа:</b> " + escape(data.getGroupNumber()) + "\n" +
                            "<b>Опыт:</b> " + escape(data.getExperience()) + "\n" +
                            "<b>GitHub/GitLab:</b> " + escape(data.getGitHub()) + "\n" +
                            "<b>Стек:</b> " + escape(data.getStack()) + "\n\n" +
                            "Подтверждаете данные? (Да/Нет)"
            );
            message.setReplyMarkup(
                    keyboardFactory.create()
                            .addButton("Да")
                            .addButton("Нет")
                            .buildReplyKeyboard()
            );

            try {
                Bot.getInstance().execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            data.setCurrentStep(Step.CONFIRMATION);
        }

        private void handleConfirmation(String input) {
            String answer = input.toLowerCase();
            if (answer.equals("да") || answer.equals("yes")) {
                sendMessage("✅ Спасибо! Ваша заявка сохранена.");

                // Отправка уведомления
                SendMessage notify = new SendMessage();
                notify.setParseMode(ParseMode.HTML);
                notify.setChatId(System.getenv("TELEGRAM_NOTIFICATION_ID"));
                notify.setMessageThreadId(Integer.parseInt(System.getenv("TG_TOPIC")));
                notify.setText("<b>Новая заявка от " + data.mention + " (" + data.tgId + "):</b>\n\n" +
                        "<b>ФИО:</b> " + escape(data.getFullName()) + "\n" +
                        "<b>Телефон:</b> " + escape(data.getPhoneNumber()) + "\n" +
                        "<b>Группа:</b> " + escape(data.getGroupNumber()) + "\n" +
                        "<b>Опыт:</b> " + escape(data.getExperience()) + "\n" +
                        "<b>GitHub/GitLab:</b> " + escape(data.getGitHub()) + "\n" +
                        "<b>Стек:</b> " + escape(data.getStack())
                );

                notify.setReplyMarkup(keyboardFactory.create()
                        .addInlineButton("Принять заявку", ITC_ADMIN_REG_DEFARAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM + ":YES:" + data.tgId)
                        .nextInlineRow()
                        .addInlineButton("Отклонить заявку", ITC_ADMIN_REG_DEFARAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM + ":NO:" + data.tgId)
                        .buildInlineKeyboard()
                );

                try {
                    Bot.getInstance().execute(notify);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                applicationService.createApplication(Application.Departament.Development, data, Long.valueOf(data.getTgId()));

                // Очистка
                userApplicationDataMap.remove(chatId);
                Bot.getInstance().getButtonManager().unsetActiveCommand(chatId);

            } else if (answer.equals("нет") || answer.equals("no")) {
                sendMessage("🔄 Хорошо, начнем заново.");
                data.reset();
                askFullName();
            } else {
                sendMessage("Пожалуйста, ответьте 'Да' или 'Нет'.");
            }
        }

        /**
         * Handle result yes.
         *
         * @param id the id
         */
        public static void handleResultYes(String id) {
            SendMessage notify = new SendMessage();
            notify.setParseMode(ParseMode.HTML);
            notify.setText("✅ Ваша заявка на вступление в ИТС одобрена! Добро пожаловать в команду.");
            notify.setChatId(id);

            try {
                Bot.getInstance().execute(notify);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }


        /**
         * Handle result no.
         *
         * @param id the id
         */
        public static void handleResultNo(String id) {
            SendMessage notify = new SendMessage();
            notify.setParseMode(ParseMode.HTML);
            notify.setText("❌ Ваша заявка на вступление в ИТС отклонена! Спасибо за интерес к нашей команде.");
            notify.setChatId(id);

            try {
                Bot.getInstance().execute(notify);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        // --- Утилиты ---
        private void sendMessage(String text) {
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setParseMode(ParseMode.HTML);
            msg.setText(text);
            try {
                Bot.getInstance().execute(msg);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        private String escape(String text) {
            return text == null ? "" : text
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        /**
         * The enum Step.
         */
        public enum Step {
            /**
             * Full name step.
             */
            FULL_NAME,
            /**
             * Phone number step.
             */
            PHONE_NUMBER,
            /**
             * Group number step.
             */
            GROUP_NUMBER,
            /**
             * Experience step.
             */
            EXPERIENCE,
            /**
             * Github step.
             */
            GITHUB,
            /**
             * Stack step.
             */
            STACK,
            /**
             * Confirmation step.
             */
            CONFIRMATION
        }

        /**
         * The type User application data.
         */
        @Getter
        @Setter
        public static class UserApplicationData {
            private String mention;
            private String tgId;
            private String fullName;
            private String phoneNumber;
            private String groupNumber;
            private String experience;
            private String gitHub;
            private String stack;

            @JsonIgnore
            private Step currentStep = Step.FULL_NAME;

            /**
             * Reset.
             */
            public void reset() {
                mention = null;
                tgId = null;
                fullName = null;
                phoneNumber = null;
                groupNumber = null;
                experience = null;
                gitHub = null;
                stack = null;
                currentStep = Step.FULL_NAME;
            }
        }
    }


    /**
     * The type Media handler.
     */
    public static class MediaHandler {
        private final Update update;

        /**
         * Instantiates a new Media handler.
         *
         * @param update the update
         */
        public MediaHandler(Update update) {
            this.update = update;
        }

        /**
         * Handle.
         */
        public void handle() {
            sendMessage("Вы выбрали направление 'Медиа и контент'. Пожалуйста, следуйте инструкциям.");
        }

        private void sendMessage(String text) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getCallbackQuery().getFrom().getId());
            message.setText(text);
            message.setReplyMarkup(Bot.getInstance().removeKeyboard());
            try {
                Bot.getInstance().execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The type Pr handler.
     */
    public static class PRHandler {
        private final Update update;

        /**
         * Instantiates a new Pr handler.
         *
         * @param update the update
         */
        public PRHandler(Update update) {
            this.update = update;
        }

        /**
         * Handle.
         */
        public void handle() {
            sendMessage("Вы выбрали направление 'PR и коммуникации'. Пожалуйста, следуйте инструкциям.");
        }

        private void sendMessage(String text) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getCallbackQuery().getFrom().getId());
            message.setText(text);
            message.setReplyMarkup(Bot.getInstance().removeKeyboard());
            try {
                Bot.getInstance().execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The type Designer handler.
     */
    public static class DesignerHandler {
        private final Update update;

        /**
         * Instantiates a new Designer handler.
         *
         * @param update the update
         */
        public DesignerHandler(Update update) {
            this.update = update;
        }

        /**
         * Handle.
         */
        public void handle() {
            sendMessage("Вы выбрали направление 'Дизайнеры'. Пожалуйста, следуйте инструкциям.");
        }

        private void sendMessage(String text) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getCallbackQuery().getFrom().getId());
            message.setText(text);
            message.setReplyMarkup(Bot.getInstance().removeKeyboard());
            try {
                Bot.getInstance().execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
