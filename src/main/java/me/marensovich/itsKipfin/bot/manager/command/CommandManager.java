package me.marensovich.itsKipfin.bot.manager.command;

import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.command.commands.CancelCommand;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.GetMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final List<Command> commandsList;
    private final UserService userService;

    // Флаг, чтобы команды регистрировались только один раз
    private boolean commandsRegistered = false;



    /**
     * Конструктор CommandManager.
     * Регистрирует все переданные команды.
     *
     * @param commandList список команд
     * @author marensovich
     * @since 0.0.1
     */
    @Autowired
    public CommandManager(List<Command> commandList, UserService userService) {
        this.commandsList = commandList;
        this.userService = userService;
    }

    /**
     * Регистрирует все команды по их scope.
     * Сначала выводит текущие команды бота, потом регистрирует новые.
     * @since 0.0.1
     * @author marensovich
     */
    public synchronized void registerCommands() {
        if (commandsRegistered) {
            log.info("ℹ Команды уже зарегистрированы, пропускаем повторный вызов");
            return;
        }

        if (commandsList == null || commandsList.isEmpty()) {
            log.warn("⚠ Список команд пуст — нечего регистрировать");
            return;
        }

        // Сохраняем команды по имени
        commandsList.forEach(cmd -> commands.put(cmd.getName().toLowerCase(), cmd));

        // Группировка по scope
        Map<BotCommandScope, List<BotCommand>> grouped = commandsList.stream()
                .collect(Collectors.groupingBy(
                        Command::getScope,
                        Collectors.mapping(
                                c -> new BotCommand(c.getName(), c.getDescription()),
                                Collectors.toList()
                        )
                ));

        // Регистрация команд по scope
        for (Map.Entry<BotCommandScope, List<BotCommand>> entry : grouped.entrySet()) {
            BotCommandScope scope = entry.getKey();
            List<BotCommand> botCommands = entry.getValue();

            try {
                // Получаем текущие команды для scope
                List<BotCommand> currentCommands = Bot.getInstance().execute(
                        GetMyCommands.builder().scope(scope).build()
                );

                if (currentCommands != null && !currentCommands.isEmpty()) {
                    log.info("📥 Текущие команды для {}:", scope.getClass().getSimpleName());
                    currentCommands.forEach(cmd -> log.info("{} — {}", cmd.getCommand(), cmd.getDescription()));
                } else {
                    log.info("📥 Нет текущих команд для {}", scope.getClass().getSimpleName());
                }

                // Сравниваем с текущими командами, обновляем только если есть изменения
                boolean needUpdate = true;
                if (currentCommands != null && currentCommands.size() == botCommands.size()) {
                    needUpdate = !new HashSet<>(currentCommands).containsAll(botCommands);
                }

                if (needUpdate) {
                    Bot.getInstance().execute(
                            SetMyCommands.builder()
                                    .commands(botCommands)
                                    .scope(scope)
                                    .build()
                    );
                    log.info("✅ Зарегистрированы команды для {}", scope.getClass().getSimpleName());
                } else {
                    log.info("ℹ Команды для {} уже актуальны", scope.getClass().getSimpleName());
                }

            } catch (TelegramApiException e) {
                log.error("❌ Ошибка при работе с командами для {}: {}", scope.getClass().getSimpleName(), e.getMessage());
            }
        }

        // Отмечаем, что регистрация уже выполнена
        commandsRegistered = true;
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
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;

        long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText().trim();
        String commandKey = text.split(" ")[0].toLowerCase();

        // Обработка активной команды
        if (hasActiveCommand(userId)) {
            Command active = activeCommands.get(userId);
            if (text.startsWith("/cancel")) {
                new CancelCommand().execute(update);
                return true;
            }

            sendActiveCommandMessage(update.getMessage().getChatId(), active.getName());
            return true;
        }

        // Обычные команды
        Command command = commands.get(commandKey);
        if (command == null) return false;

        if (command.isAdminRequired() && !userService.isUserAdmin(userId)) {
            Bot.getInstance().sendNoAccessMessage(update);
            return true;
        }

        command.execute(update);
        return true;
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
