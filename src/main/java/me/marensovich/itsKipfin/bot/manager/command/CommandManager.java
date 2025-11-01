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
 * The type Command manager.
 */
@Service
@Slf4j
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final Map<Long, Command> activeCommands = new HashMap<>();

    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private UserService userService;

    /**
     * Instantiates a new Command manager.
     *
     * @param commandList the command list
     */
    @Autowired
    public CommandManager(List<Command> commandList) {
        commandList.forEach(this::registerCommand);
    }

    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }


    /**
     * Execute command boolean.
     *
     * @param update the update
     * @return the boolean
     */
    public boolean executeCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        if (hasActiveCommand(update.getMessage().getFrom().getId())) {
            Command activeCommand = activeCommands.get(update.getMessage().getFrom().getId());
            if (update.hasMessage() && update.getMessage().hasText()) {
                if (update.getMessage().getText().startsWith("/cancel")) {
                    new CancelCommand().execute(update);
                    return true;
                }

                sendActiveCommandMessage(update.getMessage().getChatId(), activeCommand.getName());
                return true;
            }
        }

        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            String[] parts = messageText.split(" ");
            String commandKey = parts[0];

            if (commands.containsKey(commandKey)) {
                if (commands.get(commandKey).isAdminRequired()) {
                    if (!userService.isUserAdmin(update.getMessage().getFrom().getId())) {
                        Bot.getInstance().sendNoAccessMessage(update);
                        return true;
                    }
                }
                commands.get(commandKey).execute(update);
                return true;
            }
        }
        return false;
    }

    private String extractCommand(String text) {
        if (text.contains("@")) {
            text = text.substring(0, text.indexOf("@"));
        }
        return text.trim().toLowerCase();
    }

    private Command findCommand(String input) {
        return commands.get(input);
    }

    private void sendActiveCommandMessage(Long chatId, String commandName) {
        String reply = """
                Бот обрабатывает отправленную вами команду **%command%**
                
                В случае если это вы хотите прекратить выполнение команды - отправьте /cancel
                """;
        sendMessage(chatId, reply.replace("%command%", commandName));
    }

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

    /**
     * Sets active command.
     *
     * @param userId  the user id
     * @param command the command
     */
    public void setActiveCommand(Long userId, Command command) {
        log.debug("Активная команда " + command.getName() + " закреплена за пользователем " + userId);
        activeCommands.put(userId, command);
    }

    /**
     * Unset active command.
     *
     * @param userId the user id
     */
    public void unsetActiveCommand(Long userId) {
        log.debug("Активная команда пользователя " + userId + " была очищена");
        activeCommands.remove(userId);
    }

    /**
     * Has active command boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean hasActiveCommand(Long userId) {
        return activeCommands.containsKey(userId);
    }

    /**
     * Gets active command.
     *
     * @param userId the user id
     * @return the active command
     */
    public Command getActiveCommand(Long userId) {
        return activeCommands.get(userId);
    }

}
