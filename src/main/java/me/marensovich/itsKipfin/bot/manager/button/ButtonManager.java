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
 * Менеджер кнопок.
 * <p>
 * Управляет всеми кнопками бота, их регистрацией, поиском по тексту,
 * а также хранит активные кнопки, закрепленные за пользователями.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
@Slf4j
public class ButtonManager {

    private final Map<Class<? extends Button>, Button> buttonsByClass = new HashMap<>();
    private final Map<String, Button> buttonsByText = new HashMap<>();
    private final Map<Long, Button> activeButtons = new HashMap<>();

    /**
     * Конструктор менеджера.
     *
     * @param buttonList список всех кнопок для регистрации
     * @author marensovich
     * @since 0.0.1
     */
    @Autowired
    public ButtonManager(List<Button> buttonList) {
        for (Button button : buttonList) {
            buttonsByClass.put(button.getClass(), button);
            buttonsByText.put(button.getButtonText(), button);
        }
    }

    /**
     * Получить кнопку по классу.
     *
     * @param clazz класс кнопки
     * @return кнопка, если зарегистрирована, иначе null
     * @author marensovich
     * @since 0.0.1
     */
    public Button getByClass(Class<? extends Button> clazz) {
        return buttonsByClass.get(clazz);
    }

    /**
     * Найти кнопку по тексту.
     *
     * @param text текст кнопки
     * @return кнопка, если найдена, иначе null
     * @author marensovich
     * @since 0.0.1
     */
    public Button findByText(String text) {
        return buttonsByText.get(text);
    }

    /**
     * Обработать нажатие кнопки.
     *
     * @param update объект Update из Telegram
     * @author marensovich
     * @since 0.0.1
     */
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        String text = update.getMessage().getText();
        Button button = findByText(text);
        if (button != null) button.handle(update);
    }

    /**
     * Установить активную кнопку для пользователя.
     *
     * @param userId id пользователя
     * @param button кнопка, которая становится активной
     * @author marensovich
     * @since 0.0.1
     */
    public void setActiveCommand(Long userId, Button button) {
        log.debug("Активная кнопка " + button.getButtonText() + " закреплена за пользователем " + userId);
        activeButtons.put(userId, button);
    }

    /**
     * Снять активную кнопку у пользователя.
     *
     * @param userId id пользователя
     * @author marensovich
     * @since 0.0.1
     */
    public void unsetActiveCommand(Long userId) {
        log.debug("Активная кнопка пользователя " + userId + " была очищена");
        activeButtons.remove(userId);
    }

    /**
     * Проверить, есть ли у пользователя активная кнопка.
     *
     * @param userId id пользователя
     * @return true, если кнопка есть, иначе false
     * @author marensovich
     * @since 0.0.1
     */
    public boolean hasActiveCommand(Long userId) {
        return activeButtons.containsKey(userId);
    }

    /**
     * Получить активную кнопку пользователя.
     *
     * @param userId id пользователя
     * @return активная кнопка, или null если её нет
     * @author marensovich
     * @since 0.0.1
     */
    public Button getActiveCommand(Long userId) {
        return activeButtons.get(userId);
    }
}
