package me.marensovich.itsKipfin.bot.manager.command;

import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.command.commands.CancelCommand;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер команд Telegram бота.
 * <p>
 * Отвечает за регистрацию команд, проверку прав пользователей,
 * выполнение команд и управление активными пошаговыми командами.
 * <p>
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
@Slf4j
public class CommandManager {

    /** Список всех зарегистрированных команд по ключу имени команды. */
    private final Map<String, Command> commands = new HashMap<>();

    /** Активные пошаговые команды пользователей (userId -> Command). */
    private final Map<Long, Command> activeCommands = new HashMap<>();

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    /**
     * Конструктор CommandManager.
     * Регистрирует все переданные команды.
     *
     * @param commandList список команд
     * @author marensovich
     * @since 0.0.1
     */
    @Autowired
    public CommandManager(List<Command> commandList) {
        commandList.forEach(this::registerCommand);
    }

    /**
     * Регистрирует команду в мапе команд.
     *
     * @param command команда для регистрации
     * @author marensovich
     * @since 0.0.1
     */
    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    /**
     * Выполняет команду по входящему сообщению.
     * <p>
     * Проверяет активные команды, права администратора и выполняет соответствующую команду.
     *
     * @param update обновление от Telegram
     * @return true если команда была выполнена, false если нет
     * @author marensovich
     * @since 0.0.1
     */
    public boolean executeCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }

        long userId = update.getMessage().getFrom().getId();

        // Проверка активной пошаговой команды
        if (hasActiveCommand(userId)) {
            Command activeCommand = activeCommands.get(userId);

            if (update.getMessage().getText().startsWith("/cancel")) {
                new CancelCommand().execute(update);
                return true;
            }

            sendActiveCommandMessage(update.getMessage().getChatId(), activeCommand.getName());
            return true;
        }

        // Обработка стандартных команд
        String messageText = update.getMessage().getText().trim();
        String[] parts = messageText.split(" ");
        String commandKey = parts[0].toLowerCase();

        if (commands.containsKey(commandKey)) {
            Command command = commands.get(commandKey);

            if (command.isAdminRequired() && !userService.isUserAdmin(userId)) {
                Bot.getInstance().sendNoAccessMessage(update);
                return true;
            }

            command.execute(update);
            return true;
        }

        return false;
    }

    /**
     * Устанавливает активную команду для пользователя.
     *
     * @param userId id пользователя
     * @param command активная команда
     * @author marensovich
     * @since 0.0.1
     */
    public void setActiveCommand(Long userId, Command command) {
        log.debug("Активная команда {} закреплена за пользователем {}", command.getName(), userId);
        activeCommands.put(userId, command);
    }

    /**
     * Снимает активную команду пользователя.
     *
     * @param userId id пользователя
     * @author marensovich
     * @since 0.0.1
     */
    public void unsetActiveCommand(Long userId) {
        log.debug("Активная команда пользователя {} была очищена", userId);
        activeCommands.remove(userId);
    }

    /**
     * Проверяет, есть ли у пользователя активная команда.
     *
     * @param userId id пользователя
     * @return true если есть активная команда, false иначе
     * @author marensovich
     * @since 0.0.1
     */
    public boolean hasActiveCommand(Long userId) {
        return activeCommands.containsKey(userId);
    }

    /**
     * Получает активную команду пользователя.
     *
     * @param userId id пользователя
     * @return команда пользователя или null
     * @author marensovich
     * @since 0.0.1
     */
    public Command getActiveCommand(Long userId) {
        return activeCommands.get(userId);
    }

    /**
     * Отправляет пользователю сообщение о текущей активной команде.
     *
     * @param chatId id чата
     * @param commandName название команды
     * @author marensovich
     * @since 0.0.1
     */
    private void sendActiveCommandMessage(Long chatId, String commandName) {
        String reply = """
                Бот обрабатывает отправленную вами команду **%command%**
                
                В случае если вы хотите прекратить выполнение команды — отправьте /cancel
                """;
        sendMessage(chatId, reply.replace("%command%", commandName));
    }

    /**
     * Отправляет сообщение в чат.
     *
     * @param chatId id чата
     * @param text текст сообщения
     * @author marensovich
     * @since 0.0.1
     */
    private void sendMessage(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        msg.setParseMode("Markdown");
        try {
            Bot.getInstance().execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
