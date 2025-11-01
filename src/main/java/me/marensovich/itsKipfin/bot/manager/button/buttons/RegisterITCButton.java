package me.marensovich.itsKipfin.bot.manager.button.buttons;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RegisterITCButton implements Button {


    public static final String ITC_REGISTRATION_CALLBACK = "itc_reg_button_callback";

    public static final String ITC_REGISTRATION_DEPARTAMENT_PREFIX = "itc_reg:";
    public static final String ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM = "project_team";
    public static final String ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT = "video_content";
    public static final String ITC_REGISTRATION_DEPARTAMENT_PR = "pr";
    public static final String ITC_REGISTRATION_DEPARTAMENT_DESIGNER = "designer";
    private final KeyboardFactory keyboardFactory;

    public RegisterITCButton(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }


    @Override
    public String getButtonText() {
        return "Регистрация в ИТС";
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
        message.setText("""
                Регистрация в ИТС.\s
                1. Перейдите на сайт ИТС: https://its.1c.ru/.\s
                2. Нажмите на кнопку 'Регистрация' в правом верхнем углу страницы.\s
                3. Заполните все необходимые поля в регистрационной форме, включая ваше имя, фамилию, адрес электронной почты и другие требуемые данные.\s
                4. Придумайте надежный пароль для вашей учетной записи и подтвердите его.\s
                5. Ознакомьтесь с условиями использования и политикой конфиденциальности, затем поставьте галочку, если согласны с ними.\s
                6. Нажмите на кнопку 'Зарегистрироваться' для завершения процесса регистрации.\s
                7. Проверьте вашу электронную почту для подтверждения регистрации и следуйте инструкциям в письме.""");

        message.setReplyMarkup(Bot.getInstance().removeKeyboard());
        message.setReplyMarkup(keyboardFactory.create()
                .addInlineButton("Зарегистрироваться в ИТС", ITC_REGISTRATION_CALLBACK)
                .buildInlineKeyboard()
        );

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleRegButton(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText("Пожалуйста, выберите желаемое направление");
        message.setReplyMarkup(keyboardFactory.create()
                .addInlineButton("Проектная команда", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM)
                .nextInlineRow()
                .addInlineButton("Видеоконтент", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT)
                .nextInlineRow()
                .addInlineButton("Контентмейкер", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_PR)
                .nextInlineRow()
                .addInlineButton("Дизайнер", ITC_REGISTRATION_DEPARTAMENT_PREFIX + ITC_REGISTRATION_DEPARTAMENT_DESIGNER)
                .buildInlineKeyboard()
        );

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void handleProjectTeamDepartament(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText("Вы выбрали направление 'Проектная команда'. Пожалуйста, следуйте дальнейшим инструкциям...");

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleMediaDepartament(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText("Вы выбрали направление 'Медиа и контент'. Пожалуйста, следуйте дальнейшим инструкциям...");

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void handlePrDepartament(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText("Вы выбрали направление 'PR и коммуникации'. Пожалуйста, следуйте дальнейшим инструкциям...");

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDesignerDepartament(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText("Вы выбрали направление 'Дизайнеры'. Пожалуйста, следуйте дальнейшим инструкциям...");

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
