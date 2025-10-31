package me.marensovich.itsKipfin.utils;

import lombok.RequiredArgsConstructor;
import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyboardBuilder {

    private final ButtonManager buttonManager;

    private final List<KeyboardRow> rows = new ArrayList<>();
    private KeyboardRow currentRow = new KeyboardRow();

    // фабричный метод не нужен — мы используем DI, не static
    public KeyboardBuilder addButton(Class<? extends Button> buttonClass) {
        Button button = buttonManager.getByClass(buttonClass);
        if (button != null) {
            currentRow.add(new KeyboardButton(button.getButtonText()));
        }
        return this;
    }

    public KeyboardBuilder nextRow() {
        rows.add(currentRow);
        currentRow = new KeyboardRow();
        return this;
    }

    public ReplyKeyboardMarkup build() {
        if (!currentRow.isEmpty()) rows.add(currentRow);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(rows);
        return markup;
    }
}
