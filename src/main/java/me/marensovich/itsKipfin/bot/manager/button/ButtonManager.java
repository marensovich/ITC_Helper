package me.marensovich.itsKipfin.bot.manager.button;

import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Button manager.
 */
@Service
@Slf4j
public class ButtonManager {
    private final Map<Class<? extends Button>, Button> buttonsByClass = new HashMap<>();
    private final Map<String, Button> buttonsByText = new HashMap<>();

    private final Map<Long, Button> activeButtons = new HashMap<>();


    /**
     * Instantiates a new Button manager.
     *
     * @param buttonList the button list
     */
    @Autowired
    public ButtonManager(List<Button> buttonList) {
        for (Button button : buttonList) {
            buttonsByClass.put(button.getClass(), button);
            buttonsByText.put(button.getButtonText(), button);
        }
    }

    /**
     * Gets by class.
     *
     * @param clazz the clazz
     * @return the by class
     */
    public Button getByClass(Class<? extends Button> clazz) {
        return buttonsByClass.get(clazz);
    }

    /**
     * Find by text button.
     *
     * @param text the text
     * @return the button
     */
    public Button findByText(String text) {
        return buttonsByText.get(text);
    }

    /**
     * Handle.
     *
     * @param update the update
     */
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        String text = update.getMessage().getText();
        Button button = findByText(text);
        if (button != null) button.handle(update);
    }


    /**
     * Sets active command.
     *
     * @param userId the user id
     * @param button the button
     */
    public void setActiveCommand(Long userId, Button button) {
        log.debug("Активная кнопка " + button.getButtonText() + " закреплена за пользователем " + userId);
        activeButtons.put(userId, button);
    }

    /**
     * Unset active command.
     *
     * @param userId the user id
     */
    public void unsetActiveCommand(Long userId) {
        log.debug("Активная кнопка пользователя " + userId + " была очищена");
        activeButtons.remove(userId);
    }

    /**
     * Has active command boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean hasActiveCommand(Long userId) {
        return activeButtons.containsKey(userId);
    }

    /**
     * Gets active command.
     *
     * @param userId the user id
     * @return the active command
     */
    public Button getActiveCommand(Long userId) {
        return activeButtons.get(userId);
    }


}

