package me.marensovich.itsKipfin.bot;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.callback.CallbackManager;
import me.marensovich.itsKipfin.bot.manager.command.CommandManager;
import me.marensovich.itsKipfin.bot.manager.update.UpdateManager;
import me.marensovich.itsKipfin.services.UserService;
import me.marensovich.itsKipfin.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс Telegram-бота.
 * Обрабатывает обновления, команды, кнопки и колбэки.
 */
@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Getter
    private static Bot instance;

    @Autowired
    @Getter
    private CommandManager commandManager;
    @Autowired
    @Getter
    private CallbackManager callbackManager;
    @Autowired
    @Getter
    private ButtonManager buttonManager;
    @Autowired
    @Getter
    private UpdateManager updateManager;
    @Autowired
    @Getter
    private SettingsManager settingsManager;
    @Autowired
    @Getter
    private UserService userService;

    /**
     * Токен Telegram-бота, задаётся в {@code application.properties}.
     *
     * @since 0.0.1
     */
    private final String botToken;

    /**
     * Имя пользователя (username) Telegram-бота.
     *
     * @since 0.0.1
     */
    private final String botUsername;

    /**
     * Инициализирует класс бота
     *
     * @param botToken    токен бота
     * @param botUsername username бота
     * @author marensovich
     * @since 0.0.1
     */
    public Bot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername
    ) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        instance = this;
    }


    /**
     * Метод выполняющийся после инициализации класса бота
     *
     * @author marensovich
     * @since 0.0.1
     */
    @PostConstruct
    public void postInit() {
        instance = this;
        log.info("🤖 Bot instance initialized: {}", botUsername);
        try {
            commandManager.registerCommands();
            log.info("✅ Команды Telegram зарегистрированы успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка при регистрации команд: {}", e.getMessage());
        }
    }

    /**
     * Метод принимающий обновления и передающий обработчику
     *
     * @param update обьект {@link Update}
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            updateManager.updateHandler(update);
        } catch (Exception e) {
            log.error("Ошибка обработки update: {}", e.getMessage(), e);
        }
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
     * Метод, выполняющийся при регистрации бота
     *
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void onRegister() {
        if (updateManager == null || updateManager.hashedUsers == null) {
            log.error("❌ UpdateManager или его hashedUsers не инициализированы!");
            return;
        }

        userService.getAllUsers().forEach(user ->
                updateManager.hashedUsers.put(String.valueOf(user.getUserId()), user)
        );

        settingsManager.saveSettings();
        this.applyBasicSettings();
        log.info("📥 Бот зарегистрирован, пользователи и настройки загружены");
    }


    /**
     * Метод для загрузки глобальных настроек бота
     *
     * @author marensovich
     * @since 0.0.1
     */
    private void applyBasicSettings() {
        String name = SettingsManager.getSettings().getGeneralSettings().getBotName();
        String description = SettingsManager.getSettings().getGeneralSettings().getBotDescription();
        String shortDescription = SettingsManager.getSettings().getGeneralSettings().getBotShortDescription();
//        try {
//            if (!name.isBlank()){
//                Bot.getInstance().execute(
//                        SetMyName.builder().name(name).build()
//                );
//                log.info("📥 Имя бота установлено");
//            }
//            if (!description.isBlank()){
//                Bot.getInstance().execute(
//                        SetMyDescription.builder().description(description).build()
//                );
//                log.info("📥 Описание бота установлено");
//            }
//            if (!shortDescription.isBlank()){
//                Bot.getInstance().execute(
//                        SetMyShortDescription.builder().shortDescription(shortDescription).build()
//                );
//                log.info("📥 Краткое описание бота установлено");
//            }
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
    }

    // ========= Утилиты ========= //

    /**
     * Метод отправки текста
     *
     * @param chatId ID чата
     * @param text   текст
     * @author marensovich
     * @since 0.0.1
     */
    public void sendText(Long chatId, String text) {
        try {
            showBotAction(chatId, ActionType.TYPING);
            execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }

    /**
     * Метод отправки уведомления об отсутствии прав
     *
     * @param update обьект {@link Update}
     * @author marensovich
     * @since 0.0.1
     */
    public void sendNoAccessMessage(Update update) {
        sendText(update.getMessage().getChatId(), "⛔ У вас нет прав для выполнения этой команды!");
    }

    /**
     * Метод отправки уведомления о необходимости использовать лс для выполнения команды
     *
     * @param update обьект {@link Update}
     * @author marensovich
     * @since 0.0.1
     */
    public void sendUserPrivateChat(Update update) {
        sendText(update.getMessage().getChatId(),
                "💬 Используйте личные сообщения, чтобы выполнить эту команду.");
    }


    /**
     * Метод отправки уведомления об ошибке во время работы бота
     *
     * @param chatId ID чата
     * @param text   текст
     * @author marensovich
     * @since 0.0.1
     */
    public void sendErrorMessage(Long chatId, String text) {
        try {
            showBotAction(chatId, ActionType.TYPING);
            execute(new SendMessage(chatId.toString(), "⚠ " + text));
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения об ошибке: {}", e.getMessage());
        }
    }


    /**
     * Метод установки статуса бота "печатает..."
     *
     * @param chatId     ID чата
     * @param actionType тип активности {@link ActionType}
     * @author marensovich
     * @since 0.0.1
     */
    public void showBotAction(Long chatId, ActionType actionType) {
        try {
            SendChatAction chatAction = new SendChatAction();
            chatAction.setChatId(chatId);
            chatAction.setAction(actionType);
            execute(chatAction);
        } catch (TelegramApiException e) {
            log.error("Ошибка при показе действия: {}", e.getMessage());
        }
    }

    /**
     * Метод убирающий {@link ReplyKeyboardMarkup} клавиатуру
     *
     * @return обьект {@link ReplyKeyboardRemove}
     */
    public ReplyKeyboardRemove removeKeyboard() {
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        remove.setSelective(false);
        return remove;
    }
}
