package me.marensovich.itsKipfin.bot.manager.button;

import lombok.extern.slf4j.Slf4j;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ButtonManager {
    private final Map<Class<? extends Button>, Button> buttonsByClass = new HashMap<>();
    private final Map<String, Button> buttonsByText = new HashMap<>();

    private final Map<Long, Button> activeButtons = new HashMap<>();


    @Autowired
    public ButtonManager(List<Button> buttonList) {
        for (Button button : buttonList) {
            buttonsByClass.put(button.getClass(), button);
            buttonsByText.put(button.getButtonText(), button);
        }
    }

    public Button getByClass(Class<? extends Button> clazz) {
        return buttonsByClass.get(clazz);
    }

    public Button findByText(String text) {
        return buttonsByText.get(text);
    }

    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        String text = update.getMessage().getText();
        Button button = findByText(text);
        if (button != null) button.handle(update);
    }


    public void setActiveCommand(Long userId, Button button) {
        log.debug("Активная кнопка " + button.getButtonText() + " закреплена за пользователем " + userId);
        activeButtons.put(userId, button);
    }

    public void unsetActiveCommand(Long userId) {
        log.debug("Активная кнопка пользователя " + userId + " была очищена");
        activeButtons.remove(userId);
    }

    public boolean hasActiveCommand(Long userId) {
        return activeButtons.containsKey(userId);
    }

    public Button getActiveCommand(Long userId) {
        return activeButtons.get(userId);
    }


}

