package me.marensovich.itsKipfin.bot.manager.callback;

import me.marensovich.itsKipfin.bot.manager.callback.interfaces.CallbackHandler;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер для обработки callback-запросов Telegram.
 * <p>
 * Хранит обычные callback handlers и префиксные handlers, чтобы
 * вызывать соответствующую логику при нажатии inline-кнопок.
 * <p>
 * @version 0.0.1
 * @author marensovich
 * @since 0.0.1
 */
@Service
public class CallbackManager {

    private final Map<String, CallbackHandler> handlers = new HashMap<>();
    private final Map<String, PrefixCallbackHandler> prefixHandlers = new HashMap<>();

    /**
     * Конструктор менеджера callback-ов.
     *
     * @param handlers       список обычных callback handlers
     * @param prefixHandlers список префиксных callback handlers
     * @author marensovich
     * @since 0.0.1
     */
    @Autowired
    public CallbackManager(List<CallbackHandler> handlers, List<PrefixCallbackHandler> prefixHandlers) {
        handlers.forEach(this::registerHandler);
        prefixHandlers.forEach(this::registerPrefixHandler);
    }

    /**
     * Регистрация обычного callback handler.
     *
     * @param handler handler для конкретного callbackData
     * @author marensovich
     * @since 0.0.1
     */
    private void registerHandler(CallbackHandler handler) {
        handlers.put(handler.getCallbackData().toLowerCase(), handler);
    }

    /**
     * Регистрация префиксного callback handler.
     *
     * @param handler handler для callbackData, начинающихся с префикса
     * @author marensovich
     * @since 0.0.1
     */
    private void registerPrefixHandler(PrefixCallbackHandler handler) {
        prefixHandlers.put(handler.getPrefixCallbackData().toLowerCase(), handler);
    }

    /**
     * Обработка callback-запроса.
     * <p>
     * Сначала проверяет точное совпадение callbackData с обычными handlers.
     * Затем ищет префиксное совпадение с префиксными handlers.
     *
     * @param update объект обновления Telegram
     * @return true, если callback был обработан, иначе false
     * @author marensovich
     * @since 0.0.1
     */
    public boolean handleCallback(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }

        String callbackData = update.getCallbackQuery().getData();

        // Проверка обычных callback handlers
        CallbackHandler handler = handlers.get(callbackData);
        if (handler != null) {
            handler.handle(update);
            return true;
        }

        // Проверка префиксных callback handlers
        for (Map.Entry<String, PrefixCallbackHandler> entry : prefixHandlers.entrySet()) {
            if (callbackData.startsWith(entry.getKey())) {
                entry.getValue().handle(update);
                return true;
            }
        }

        // Callback не обработан
        return false;
    }
}
