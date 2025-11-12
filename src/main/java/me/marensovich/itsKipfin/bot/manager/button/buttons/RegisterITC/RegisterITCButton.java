package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import me.marensovich.itsKipfin.utils.exception.exceptions.BotException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Кнопка "Вступление в ИТС".
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class RegisterITCButton implements Button {

    /**
     * Callback data для начальной inline-кнопки "Вступить в ИТС".
     *
     * @since 0.0.1
     */
    public static final String ITC_REGISTRATION_CALLBACK = "itc_reg_button_callback";

    /**
     * Префикс callback data для выбора направления: "itc_reg:{department}".
     *
     * @since 0.0.1
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_PREFIX = "itc_reg:";

    /**
     * Префикс callback data для административных действий с заявками:
     * "itc_admin_reg:{department}:{YES|NO}:{applicationId}".
     *
     * @since 0.0.1
     */
    public static final String ITC_ADMIN_REG_DEFARAMENT_PREFIX = "itc_admin_reg:";

    /**
     * Идентификаторы направлений (строки используются в callbackData)
     *
     * @since 0.0.1
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
     * Регулярные выражения для валидации полей
     *
     * @since 0.0.1
     */
    public static final String FIO_REGEX = "^[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+)?$";
    /**
     * The constant GITHUB_REGEX.
     */
    public static final String GITHUB_REGEX = "^(https?://)?(www\\.)?(github)\\.com/[A-Za-z0-9_-]+/?$";
    /**
     * The constant GROUP_REGEX.
     */
    public static final String GROUP_REGEX = "^[1-4](ОИБАС|ИСИП|ИИС)-\\d{3,4}$";


    private final KeyboardFactory keyboardFactory;

    /**
     * Конструктор кнопки.
     *
     * @param keyboardFactory фабрика клавиатур (внедряется Spring)
     * @author marensovich
     * @since 0.0.1
     */
    public RegisterITCButton(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    /**
     * Текст кнопки, отображаемый на reply-клавиатуре.
     *
     * @return локализованный текст кнопки@author marensovich
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public String getButtonText() {
        return "Вступление в ИТС";
    }

    /**
     * Обработчик нажатия кнопки пользователем.
     *
     * @param update Update, пришедший от Telegram (метод ожидает {@code update.hasMessage() == true})
     * @throws RuntimeException если отправка сообщения через Telegram API завершилась с ошибкой
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void handle(Update update) {
        if (!update.getMessage().getChatId().equals(update.getMessage().getFrom().getId())) {
            Bot.getInstance().sendUserPrivateChat(update);
            return;
        }

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
            Bot.getInstance().showBotAction(update.getMessage().getFrom().getId(), ActionType.TYPING);
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getMessage().getFrom().getId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Bot.getInstance().getButtonManager().unsetActiveCommand(update.getMessage().getFrom().getId());
        }
    }

    /**
     * Обработчик события нажатия inline-кнопки "Вступить в ИТС".
     * <p>
     * Отправляет пользователю краткую информацию о направлениях и inline-кнопки
     * с callbackData вида {@code itc_reg:{department}}.
     * </p>
     *
     * @param update Update содержащий {@code callbackQuery}
     * @throws RuntimeException если отправка сообщений в Telegram провалилась
     * @author marensovich
     * @since 0.0.1
     */
    public void handleRegButton(Update update) {
        Bot.getInstance().getButtonManager().setActiveCommand(update.getMessage().getFrom().getId(), this);

        // Информационное сообщение (общая справка)
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

        // Сообщение с выбором направления
        SendMessage directionMessage = new SendMessage();
        directionMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        directionMessage.setText(
                """
                        <b>Вы практически в ИТС!</b> Остался один шаг — выберите направление:
                        
                        Для завершения регистрации:
                        1. Выберите направление ниже.
                        2. Заполните форму после выбора.
                        3. Дождитесь подтверждения от руководителя.
                        """
        );
        directionMessage.setParseMode(ParseMode.HTML);
        directionMessage.setReplyMarkup(keyboardFactory.create()
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
            Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
            Bot.getInstance().execute(infoMessage);
            Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
            Bot.getInstance().execute(directionMessage);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getMessage().getFrom().getId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Bot.getInstance().getButtonManager().unsetActiveCommand(update.getMessage().getFrom().getId());
        }
    }
}
