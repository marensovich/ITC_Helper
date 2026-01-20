package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.UserDesignerApplicationDTO;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.UserMediaApplicationDTO;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.UserPRApplicationDTO;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto.UserProjectTeamApplicationDTO;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.CallbackHandler;
import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.services.ApplicationService;
import me.marensovich.itsKipfin.settings.SettingsManager;
import me.marensovich.itsKipfin.utils.exception.exceptions.BotException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CancelRegisterHandler implements CallbackHandler {

    private final ApplicationService applicationService;

    public CancelRegisterHandler(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public String getCallbackData() {
        return RegisterITCButton.ITC_CALLBACK_CANCEL_REGISTRATION;
    }

    @Override
    public void handle(Update update) {
        Application application = applicationService.getApplicationByUserId(update.getCallbackQuery().getFrom().getId());
        switch (application.getDepartament()){
            case Communication -> {
                UserPRApplicationDTO applicationDTO = application.getDataObject(UserPRApplicationDTO.class);
                updatePRMessage(update, applicationDTO);
            }
            case Development -> {
                UserProjectTeamApplicationDTO applicationDTO = application.getDataObject(UserProjectTeamApplicationDTO.class);
                updateProjectTeamMessage(update, applicationDTO);
            }
            case Media -> {
                UserMediaApplicationDTO applicationDTO = application.getDataObject(UserMediaApplicationDTO.class);
                updateMediaMessage(update, applicationDTO);
            }
            case Designer -> {
                UserDesignerApplicationDTO applicationDTO = application.getDataObject(UserDesignerApplicationDTO.class);
                updateDesignerMessage(update, applicationDTO);
            }
        }
        applicationService.updateApplicationStatus(application.getId(), Application.Status.REJECTED);
        SendMessage message = new SendMessage();
        message.setText("Заявка успешно удалена!");
        message.setChatId(update.getCallbackQuery().getFrom().getId());

        try {
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обновить админское сообщение (edit), пометив заявку как одобренную/отклонённую.
     *
     * @param update   Update с callbackQuery от администратора
     * @param userData данные пользователя (десериализованные из application.data)
     * @author marensovich
     * @since 0.0.1
     */
    public void updatePRMessage(Update update, UserPRApplicationDTO userData) {
        if (!update.hasCallbackQuery()) return;
        String interestsText = userData.getInterests() == null || userData.getInterests().isEmpty()
                ? "—"
                : String.join(", ", userData.getInterests());

        String statusText = "❌ Заявка отозвана.";

        String messageText = String.format(
                """
                        <b>Новая заявка от %s (%s):</b>
                        
                        <b>ФИО:</b> %s
                        <b>Телефон:</b> %s
                        <b>Группа:</b> %s
                        
                        <b>Почему в PR:</b> %s
                        <b>Опыт:</b> %s
                        <b>Интересы:</b> %s
                        <b>Вопросы к руководителям:</b> %s
                        
                        %s""",
                userData.getMention(), userData.getTgId(),
                escape(userData.getFullName()),
                escape(userData.getPhoneNumber()),
                escape(userData.getGroupNumber()),
                escape(userData.getReasonToJoin()),
                escape(userData.getExperience()),
                escape(interestsText),
                escape(userData.getQuestions()),
                statusText
        );

        Application application = applicationService.getApplicationByUserId(Long.valueOf(userData.getTgId()));
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(SettingsManager.getSettings().getGeneralSettings().getAdminChannelId());
        editMessage.setMessageId(Math.toIntExact(application.getMessageId()));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setText(messageText);

        try {
            Bot.getInstance().execute(editMessage);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getCallbackQuery().getFrom().getId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обновить админское сообщение (edit), пометив заявку как одобренную/отклонённую.
     *
     * @param update   Update с callbackQuery от администратора
     * @param userData данные пользователя (десериализованные из application.data)
     * @author marensovich
     * @since 0.0.1
     */
    public void updateProjectTeamMessage(Update update, UserProjectTeamApplicationDTO userData) {
        if (!update.hasCallbackQuery()) return;

        String statusText = "❌ Заявка отозвана.";

        String messageText = String.format(
                """
                        <b>Новая заявка от %s (%s):</b>
                        
                        <b>ФИО:</b> %s
                        <b>Телефон:</b> %s
                        <b>Группа:</b> %s
                        <b>Опыт:</b> %s
                        <b>GitHub:</b> %s
                        <b>Стек:</b> %s
                        
                        %s""",
                userData.getMention(), userData.getTgId(),
                escape(userData.getFullName()),
                escape(userData.getPhoneNumber()),
                escape(userData.getGroupNumber()),
                escape(userData.getExperience()),
                escape(userData.getGitHub()),
                escape(userData.getStack()),
                statusText
        );

        Application application = applicationService.getApplicationByUserId(Long.valueOf(userData.getTgId()));
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(SettingsManager.getSettings().getGeneralSettings().getAdminChannelId());
        editMessage.setMessageId(Math.toIntExact(application.getMessageId()));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setText(messageText);

        try {
            Bot.getInstance().execute(editMessage);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getCallbackQuery().getFrom().getId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обновить админское сообщение (edit), пометив заявку как одобренную/отклонённую.
     *
     * @param update   Update с callbackQuery от администратора
     * @param userData данные пользователя (десериализованные из application.data)
     * @author marensovich
     * @since 0.0.1
     */
    public void updateMediaMessage(Update update, UserMediaApplicationDTO userData) {
        if (!update.hasCallbackQuery()) return;

        String statusText = "❌ Заявка отозвана.";

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

        Application application = applicationService.getApplicationByUserId(Long.valueOf(userData.getTgId()));
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(SettingsManager.getSettings().getGeneralSettings().getAdminChannelId());
        editMessage.setMessageId(Math.toIntExact(application.getMessageId()));
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setText(messageText);

        try {
            Bot.getInstance().execute(editMessage);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getCallbackQuery().getFrom().getId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обновить админское сообщение (edit), пометив заявку как одобренную/отклонённую.
     *
     * @param update   Update с callbackQuery от администратора
     * @param userData данные пользователя (десериализованные из application.data)
     * @author marensovich
     * @since 0.0.1
     */
    public void updateDesignerMessage(Update update, UserDesignerApplicationDTO userData) {
        if (!update.hasCallbackQuery()) return;

        String statusText = "❌ Заявка отозвана.";

        String messageText = String.format(
                """
                        <b>Новая заявка от %s (%s):</b>
                        
                        <b>ФИО:</b> %s
                        <b>Телефон:</b> %s
                        <b>Группа:</b> %s
                        <b>Основные программы:</b> %s
                        <b>Примеры работ:</b> %s
                        <b>Фотографии:</b> %s
                        
                        %s""",
                userData.getMention(), userData.getTgId(),
                escape(userData.getFullName()),
                escape(userData.getPhoneNumber()),
                escape(userData.getGroupNumber()),
                escape(userData.getMainApps()),
                escape(userData.getExamples()),
                userData.getPhotoFileIds().isEmpty()
                        ? "Нет"
                        : userData.getPhotoFileIds().size() + " шт.",
                statusText
        );

        Application application =
                applicationService.getApplicationByUserId(Long.valueOf(userData.getTgId()));

        String chatId = SettingsManager.getSettings()
                .getGeneralSettings()
                .getAdminChannelId();

        int messageId = Math.toIntExact(application.getMessageId());

        try {
            // 🔹 Сначала пытаемся сохранить фото (caption)
            EditMessageCaption editCaption = new EditMessageCaption();
            editCaption.setChatId(chatId);
            editCaption.setMessageId(messageId);
            editCaption.setCaption(messageText);
            editCaption.setParseMode(ParseMode.HTML);

            Bot.getInstance().execute(editCaption);

        } catch (TelegramApiException captionException) {
            // 🔹 Если это было не фото — пробуем обычный текст
            try {
                EditMessageText editText = new EditMessageText();
                editText.setChatId(chatId);
                editText.setMessageId(messageId);
                editText.setText(messageText);
                editText.setParseMode(ParseMode.HTML);

                Bot.getInstance().execute(editText);

            } catch (TelegramApiException e) {
                Bot.getInstance().sendErrorMessage(
                        update.getCallbackQuery().getFrom().getId(),
                        "⚠️ Ошибка при работе бота, обратитесь к администратору"
                );
                throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String escape(String text) {
        return text == null ? "" : text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}
