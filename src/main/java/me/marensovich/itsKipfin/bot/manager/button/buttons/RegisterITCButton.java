package me.marensovich.itsKipfin.bot.manager.button.buttons;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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

    public void handleRegButton(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);


        SendMessage infoMessage = new SendMessage();
        infoMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        infoMessage.setText(
                """
                <b>Краткая информации о каждом направлении:</b>
                
                <b>1. Проектная команда</b>
                Создание и разработка сайтов, ботов, внутренних систем и других цифровых продуктов для ИТС и наших партнеров.
                Разработка технических заданий, программирование, тестирование и внедрение цифровых решений.
                Разработка программ, скриптов автоматизации и интеграций. 
                Анализ потребностей колледжа и предложение цифровых решений.
                
                <b>2. Медиа и контент</b>
                Съемка и монтаж видео для социальных сетей для VK, Telegram и других платформ.
                Фотоотчеты мероприятий.
                
                <b>3. PR и Коммуникации</b>
                Продвижение ИТС и их проектов в социальных сетях и на других платформах.
                Взаимодействие с Администрацией колледжа и внешними организациями. 
                Подготовка постов, пресс-релизов, участие в форумах и конференциях. 
                
                <b>4. Дизайнеры</b> 
                Разработка визуального стиля проектов (сайты, интерфейсы, посты и т.д.).
                Подготовка макетов, логотипов и брендбуков.
                Создание графических материалов для социальных сетей и других платформ.
                """
        );
        infoMessage.setParseMode(ParseMode.HTML);

        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText(
                """
                <b>Вы практически в ИТС!</b> Остался всего один шаг — выбрать направление, в котором вы хотите развиваться вместе с нами.
                
                Для завершение процесса подачи заявки на вступление в ИТС вам необходимо:
                1. Выберите желаемое направление в ИТС, используя кнопки ниже.
                2. Заполните форму для регистрации, которая будет отправлена вам после выбора направления.
                3. Дождитесь подтверждения вашей заявки от руководителя направления ИТС.
                
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


    public void handleProjectTeamDepartament(Update update) {
        Bot.getInstance().showBotAction(update.getCallbackQuery().getFrom().getId(), ActionType.TYPING);
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        message.setText("Вы выбрали направление 'Проектная команда'. Пожалуйста, следуйте дальнейшим инструкциям...");
        message.setReplyMarkup(Bot.getInstance().removeKeyboard());

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
        message.setReplyMarkup(Bot.getInstance().removeKeyboard());

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
        message.setReplyMarkup(Bot.getInstance().removeKeyboard());

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
        message.setReplyMarkup(Bot.getInstance().removeKeyboard());
        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
