package me.marensovich.itsKipfin.utils;

import me.marensovich.itsKipfin.bot.manager.button.ButtonManager;
import me.marensovich.itsKipfin.bot.manager.button.interfaces.Button;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {
    @Lazy
    private final ButtonManager buttonManager;

    public KeyboardFactory(@Lazy ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    public UniversalKeyboardBuilder create() {
        return new UniversalKeyboardBuilder(buttonManager);
    }

    // 👇 вот здесь вложенный, но static класс, чтобы не требовал экземпляр внешнего класса
    public static class UniversalKeyboardBuilder {

        private final ButtonManager buttonManager;

        public UniversalKeyboardBuilder(@Lazy ButtonManager buttonManager) {
            this.buttonManager = buttonManager;
        }

        // --- Reply кнопки ---
        private final List<KeyboardRow> rows = new ArrayList<>();
        private KeyboardRow currentRow = new KeyboardRow();

        public UniversalKeyboardBuilder addButton(Class<? extends Button> buttonClass) {
            Button button = buttonManager.getByClass(buttonClass);
            if (button != null) {
                currentRow.add(new KeyboardButton(button.getButtonText()));
            }
            return this;
        }

        public UniversalKeyboardBuilder addButton(String text) {
            currentRow.add(new KeyboardButton(text));
            return this;
        }

        public UniversalKeyboardBuilder nextRow() {
            rows.add(currentRow);
            currentRow = new KeyboardRow();
            return this;
        }

        public ReplyKeyboardMarkup buildReplyKeyboard() {
            if (!currentRow.isEmpty()) rows.add(currentRow);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setResizeKeyboard(true);
            markup.setKeyboard(rows);
            return markup;
        }

        // --- Inline кнопки ---
        private final List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();
        private List<InlineKeyboardButton> currentInlineRow = new ArrayList<>();

        public UniversalKeyboardBuilder addInlineButton(Class<? extends Button> buttonClass) {
            Button button = buttonManager.getByClass(buttonClass);
            if (button != null) {
                InlineKeyboardButton inlineButton = new InlineKeyboardButton();
                inlineButton.setText(button.getButtonText());
                inlineButton.setCallbackData(button.getButtonText());
                currentInlineRow.add(inlineButton);
            }
            return this;
        }

        public UniversalKeyboardBuilder addInlineButton(String text, String callbackData) {
            InlineKeyboardButton inlineButton = new InlineKeyboardButton();
            inlineButton.setText(text);
            inlineButton.setCallbackData(callbackData);
            currentInlineRow.add(inlineButton);
            return this;
        }

        public UniversalKeyboardBuilder nextInlineRow() {
            inlineRows.add(currentInlineRow);
            currentInlineRow = new ArrayList<>();
            return this;
        }

        public InlineKeyboardMarkup buildInlineKeyboard() {
            if (!currentInlineRow.isEmpty()) inlineRows.add(currentInlineRow);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(inlineRows);
            return markup;
        }
    }
}
