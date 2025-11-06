package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC;

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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Кнопка "Вступление в ИТС".
 *
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 *
 */
@Component
public class RegisterITCButton implements Button {

    /**
     * Callback data для начальной inline-кнопки "Вступить в ИТС".
     * @since 0.0.1
     */
    public static final String ITC_REGISTRATION_CALLBACK = "itc_reg_button_callback";

    /**
     * Префикс callback data для выбора направления: "itc_reg:{department}".
     * @since 0.0.1
     */
    public static final String ITC_REGISTRATION_DEPARTAMENT_PREFIX = "itc_reg:";

    /**
     * Префикс callback data для административных действий с заявками:
     * "itc_admin_reg:{department}:{YES|NO}:{applicationId}".
     * @since 0.0.1
     */
    public static final String ITC_ADMIN_REG_DEFARAMENT_PREFIX = "itc_admin_reg:";

    /**
    * Идентификаторы направлений (строки используются в callbackData)
    * @since 0.0.1
    */
    public static final String ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM = "project_team";
    public static final String ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT = "video_content";
    public static final String ITC_REGISTRATION_DEPARTAMENT_PR = "pr";
    public static final String ITC_REGISTRATION_DEPARTAMENT_DESIGNER = "designer";

    /**
     * Регулярные выражения для валидации полей
     * @since 0.0.1
     */
    public static final String FIO_REGEX = "^[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+)?$";
    public static final String GITHUB_REGEX = "^(https?://)?(www\\.)?(github|gitlab)\\.com/[A-Za-z0-9_-]+/?$";
    public static final String GROUP_REGEX = "^[1-4](ОИБАС|ИСИП|ИИС)-\\d{3,4}$";


    private final KeyboardFactory keyboardFactory;

    /**
     * Конструктор кнопки.
     *
     * @param keyboardFactory фабрика клавиатур (внедряется Spring)
     * @since 0.0.1
     * @author marensovich
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
            // выбрасываем runtime-exception: вызывающий код ожидает, что бот не "заглотит" ошибку silently
            throw new RuntimeException("Ошибка при отправке сообщения о вступлении в ИТС", e);
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
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);

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
            Bot.getInstance().execute(infoMessage);
            Bot.getInstance().execute(directionMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при отправке сообщений со списком направлений ИТС", e);
        }
    }
}
