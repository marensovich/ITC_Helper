package me.marensovich.itsKipfin.utils.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.database.models.User;
import me.marensovich.itsKipfin.services.UserService;
import me.marensovich.itsKipfin.settings.SettingsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

/**
 * Global exception handler for Telegram bot.
 *
 * <p>Intercepts all exceptions in bot command and update processing,
 * builds structured {@link ApiError} objects and sends detailed
 * diagnostics to the developer's Telegram.</p>
 *
 * @author marensovich
 * @version 0.0.1
 */
@Component
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    /**
     * Instantiates a new Global exception handler.
     *
     * @param objectMapper the object mapper
     * @param userService  the user service
     */
    public GlobalExceptionHandler(ObjectMapper objectMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    /**
     * Обрабатывает исключения, возникающие при обработке команд или апдейтов.
     *
     * @param e      само исключение
     * @param update the update
     */
    public void handle(
            Exception e,
            Update update
    ) {
        String username = null;
        String userId = null;
        String chatId = null;

        if (update.hasMessage() && update.getMessage().getFrom() != null) {
            username = update.getMessage().getFrom().getUserName();
            userId = update.getMessage().getFrom().getId().toString();
            chatId = update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getFrom() != null) {
            username = update.getCallbackQuery().getFrom().getUserName();
            userId = update.getCallbackQuery().getFrom().getId().toString();
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else if (update.hasInlineQuery() && update.getInlineQuery().getFrom() != null) {
            username = update.getInlineQuery().getFrom().getUserName();
            userId = update.getInlineQuery().getFrom().getId().toString();
            chatId = "inline"; // нет чата, запрос пришёл из inline
        } else {
            username = "unknown";
            userId = "unknown";
            chatId = "unknown";
        }

        User user = userService.getUserById(Long.parseLong(userId));

        var activeCommandObj = Bot.getInstance().getCommandManager().getActiveCommand(Long.valueOf(userId));
        var activeCommand = activeCommandObj != null ? activeCommandObj.getName() : "none";

        var activeButtonObj = Bot.getInstance().getButtonManager().getActiveCommand(Long.parseLong(userId));
        var activeButton = activeButtonObj != null ? activeButtonObj.getButtonText() : "none";


        ApiError apiError = new ApiError(
                username,
                userId,
                chatId,
                user.getPosition().getRole().name(),
                user.getPosition().getDepartment().name(),
                activeCommand,
                activeButton,
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now()
        );

        try {
            String json = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(apiError);

            String msg = """
                    ⚠️ *Ошибка в Telegram-боте* ⚠️
                    
                    ```
                    %s
                    ```
                    """.formatted(json);

            String channelId = SettingsManager.getSettings().getGeneralSettings().getAdminChannelId();
            String threadId = SettingsManager.getSettings().getGeneralSettings().getAdminBotErrorMessageThreadId();

            SendMessage message = new SendMessage();
            message.setChatId("6737078498");
            message.setText(msg);
            message.setParseMode(ParseMode.MARKDOWN);
//            if (threadId != null && !threadId.isBlank()) {
//                message.setMessageThreadId(Integer.parseInt(threadId));
//            }

            Bot.getInstance().executeAsync(message);

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Handles all unhandled exceptions.
     *
     * @since v.0.1
     */
    @ExceptionHandler(Exception.class)
    public void handleGeneral(
            Exception e,
            Update update
    ) {
        handle(e, update);
    }
}
