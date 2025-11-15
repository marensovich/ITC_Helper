package me.marensovich.itsKipfin.bot.manager.command.commands;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.DesignerHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.MediaHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.PRHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.ProjectTeamHandler;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.utils.KeyboardFactory;
import me.marensovich.itsKipfin.utils.exception.exceptions.BotException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Stream;

/**
 * Команда для отмены текущей активной команды пользователя.
 * <p>
 * Если у пользователя активна команда или активна кнопка,
 * она будет очищена, а пользователь получит уведомление.
 * <p>
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class CancelCommand implements Command {

    @Autowired
    private KeyboardFactory keyboardFactory;

    /**
     * Получить имя команды.
     *
     * @return название команды "/cancel"
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public String getName() {
        return "/cancel";
    }

    @Override
    public String getDescription() {
        return "Отменить работу любой команды";
    }

    /**
     * Проверка, требуется ли админский доступ.
     *
     * @return false, админ не требуется
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public boolean isAdminRequired() {
        return false;
    }

    /**
     * Выполнить команду отмены.
     * <p>
     * Очищает активную команду или активную кнопку,
     * отправляет уведомление пользователю.
     *
     * @param update объект обновления Telegram
     * @author marensovich
     * @since 0.0.1
     */
    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        boolean commandCleared = false;

        if (Bot.getInstance().getCommandManager().hasActiveCommand(userId)) {
            Bot.getInstance().getCommandManager().unsetActiveCommand(userId);
            commandCleared = true;
        } else if (Bot.getInstance().getButtonManager().hasActiveCommand(userId)) {
            Bot.getInstance().getButtonManager().unsetActiveCommand(userId);
            commandCleared = true;
        }

        Stream.of(
                ProjectTeamHandler.userApplicationDataMap,
                MediaHandler.userApplicationDataMap,
                DesignerHandler.userApplicationDataMap,
                PRHandler.userApplicationDataMap
        ).forEach(map -> map.remove(userId));

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setReplyMarkup(Bot.getInstance().removeKeyboard());

        if (commandCleared) {
            msg.setText("✅ Активная команда была успешно удалена.");
        } else {
            msg.setText("❌ Нет активных команд для удаления.");
        }

        try {
            Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
            Bot.getInstance().execute(msg);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(chatId, "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BotCommandScope getScope() {
        return BotCommandScopeAllPrivateChats.builder().build();
    }
}
