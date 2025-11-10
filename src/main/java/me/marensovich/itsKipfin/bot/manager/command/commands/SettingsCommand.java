package me.marensovich.itsKipfin.bot.manager.command.commands;

import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.data.Role;
import me.marensovich.itsKipfin.database.models.User;
import me.marensovich.itsKipfin.services.UserService;
import me.marensovich.itsKipfin.settings.SettingsManager;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.EnumSet;

@Slf4j
@Component
public class SettingsCommand implements Command {

    @Autowired private UserService userService;
    @Autowired private KeyboardFactory keyboardFactory;
    @Autowired private SettingsManager settingsManager;

    @Override
    public String getName() {
        return "/settings";
    }

    @Override
    public String getDescription() {
        return "Display or change bot settings";
    }

    @Override
    public boolean isAdminRequired() {
        return false;
    }

    @Override
    public void execute(Update update) {
        if (update.getMessage().getChatId().equals(update.getMessage().getFrom().getId())) {
            Bot.getInstance().getCommandManager().setActiveCommand(update.getMessage().getFrom().getId(), this);

            if (userService.isUserAdmin(update.getMessage().getFrom().getId())){
                if (SettingsManager.getSettings().getGeneralSettings().getAdminChannelId() == null){
                    SendMessage message = new SendMessage();
                    message.setText("Отправьте канал который будет назначен в качестве админ канала");
                    message.setChatId(update.getMessage().getFrom().getId());
                    message.setReplyMarkup(keyboardFactory.create()
                            .addRequestChatButton(
                                    "Отправьте канал",
                                    1,
                                    false,
                                    true,
                                    false,
                                    true
                            )
                            .buildReplyKeyboard()
                    );
                    try {
                        Bot.getInstance().execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                SendMessage message = new SendMessage();
                message.setText("Настройки доступны только администраторам бота.");
                message.setChatId(update.getMessage().getFrom().getId());
                try {
                    Bot.getInstance().execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getFrom().getId());
            }


            if (update.getMessage().getChatShared() != null) {
                Long userId = update.getMessage().getFrom().getId();
                Long selectedChatId = update.getMessage().getChatShared().getChatId();

                GetChatMember getChatMember = new GetChatMember();
                getChatMember.setChatId(selectedChatId);
                getChatMember.setUserId(userId);

                ChatMember chatMember = null;
                try {
                    chatMember = Bot.getInstance().execute(getChatMember);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                String status = chatMember.getStatus();
                if (status.equals("left") || status.equals("kicked")) {
                    Bot.getInstance().sendText(userId, "⚠️ Вы не состоите в этом чате. Добавьтесь туда и повторите попытку.");
                    return;
                }
                SettingsManager.getSettings().getGeneralSettings().setAdminChannelId(String.valueOf(update.getMessage().getChatShared().getChatId()));
                settingsManager.saveSettings();
                Bot.getInstance().sendText(update.getMessage().getFrom().getId(), "Админ канал успешно установлен!");
                Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getFrom().getId());

                SendMessage msg = new SendMessage();
                msg.setChatId(selectedChatId.toString());
                msg.setText("""
                ✅ Этот чат успешно установлен в качестве админ канала бота
                
                Для настройки уведомлений бота используйте команду /settings в необходимом канале.
                """);
                try {
                    Bot.getInstance().execute(msg);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Bot.getInstance().sendErrorMessage(update.getMessage().getFrom().getId(), "Произошла ошибка при получении чата. Попробуйте еще раз.");
            }

        } else {
            if (update.getMessage().getChatId().equals(SettingsManager.getSettings().getGeneralSettings().getAdminChannelId())) {;

                User user = userService.getUserById(update.getMessage().getFrom().getId());
                if (user == null) {
                    Bot.getInstance().sendErrorMessage(update.getMessage().getChatId(), "Пользователь не найден.");
                    return;
                }

                if (!EnumSet.of(Role.CURATOR, Role.HEAD, Role.PRESIDENT).contains(user.getPosition().getRole()) || user.isAdmin()) {
                    SendMessage message = new SendMessage();
                    message.setText("Настройки доступны только для руководителей отдела и выше.");
                    message.setChatId(update.getMessage().getChatId());
                    if (update.getMessage().getMessageThreadId() != null) {
                        message.setMessageThreadId(update.getMessage().getMessageThreadId());
                    }

                    try {
                        Bot.getInstance().execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }

                switch (user.getPosition().getDepartment()) {
                    case Development -> {
                        SettingsManager.getSettings().getApplications().getProjectTeamApplication().setNewApplicationNotificationThreadId(
                                String.valueOf(update.getMessage().getMessageThreadId())
                        );
                    }
                    case Media -> {
                        SettingsManager.getSettings().getApplications().getMediaApplication().setNewApplicationNotificationThreadId(
                                String.valueOf(update.getMessage().getMessageThreadId())
                        );
                    }
                    case Designer -> {
                        SettingsManager.getSettings().getApplications().getDesignerApplication().setNewApplicationNotificationThreadId(
                                String.valueOf(update.getMessage().getMessageThreadId())
                        );
                    }
                    case Communication -> {
                        SettingsManager.getSettings().getApplications().getPrApplication().setNewApplicationNotificationThreadId(
                                String.valueOf(update.getMessage().getMessageThreadId())
                        );
                    }
                    case Head -> {
                        SendMessage message = new SendMessage();
                        message.setText("Устанавливать каналы уведомлений могут только руководители отделов.");
                        message.setChatId(update.getMessage().getChatId());
                        if (update.getMessage().getMessageThreadId() != null) {
                            message.setMessageThreadId(update.getMessage().getMessageThreadId());
                        }

                        try {
                            Bot.getInstance().execute(message);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case null, default ->  {
                    SendMessage message = new SendMessage();
                        message.setText("Не удалось определить ваш отдел. Обратитесь к администратору бота.");
                        message.setChatId(update.getMessage().getChatId());
                        if (update.getMessage().getMessageThreadId() != null) {
                            message.setMessageThreadId(update.getMessage().getMessageThreadId());
                        }

                        try {
                            Bot.getInstance().execute(message);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            } else {
                Bot.getInstance().sendText(update.getMessage().getChatId(), "Настройки доступны только в каналах с ботом.");
            }

        }
    }

    @Override
    public BotCommandScope getScope() {
        return BotCommandScopeChat.builder().chatId("3395394215").build();
    }
}
