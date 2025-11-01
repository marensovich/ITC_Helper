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
 * The type Callback manager.
 */
@Service
public class CallbackManager {

    private final Map<String, CallbackHandler> handlers = new HashMap<>();

    private final Map<String, PrefixCallbackHandler> prefixHandlers = new HashMap<>();

    /**
     * Instantiates a new Callback manager.
     *
     * @param handlers       the handlers
     * @param prefixHandlers the prefix handlers
     */
    @Autowired
    public CallbackManager(List<CallbackHandler> handlers, List<PrefixCallbackHandler> prefixHandlers) {
        handlers.forEach(this::registerHandler);
        prefixHandlers.forEach(this::registerPrefixHandler);
    }

    private void registerHandler(CallbackHandler handler) {
        handlers.put(handler.getCallbackData().toLowerCase(), handler);
    }

    private void registerPrefixHandler(PrefixCallbackHandler handler) {
        prefixHandlers.put(handler.getPrefixCallbackData().toLowerCase(), handler);
    }

    /**
     * Handle callback boolean.
     *
     * @param update the update
     * @return the boolean
     */
    public boolean handleCallback(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        String callbackData = update.getCallbackQuery().getData();

        CallbackHandler handler = handlers.get(callbackData);
        if (handler != null) {
            handler.handle(update);
            return true;
        }


        for (Map.Entry<String, PrefixCallbackHandler> entry : prefixHandlers.entrySet()) {
            if (callbackData.startsWith(entry.getKey())) {
                entry.getValue().handle(update);
                return true;
            }
        }
        return false;
    }
}