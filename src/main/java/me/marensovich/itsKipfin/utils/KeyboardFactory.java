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

/**
 * Фабрика для создания различных типов клавиатур (Reply и Inline)
 * для Telegram-бота. Используется для удобного построения
 * интерфейсов взаимодействия с пользователем.
 *
 * <p>Поддерживает работу как с текстовыми кнопками, так и
 * с кнопками, связанными с классами, реализующими интерфейс {@link Button}.
 *
 * @author marensovich
 * @version 0.0.2
 * @since 0.0.1
 */
@Component
public class KeyboardFactory {

    @Lazy
    private final ButtonManager buttonManager;

    /**
     * Конструктор фабрики клавиатур.
     *
     * @author marensovich
     * @since 0.0.1
     * @param buttonManager менеджер кнопок, предоставляющий информацию
     *                      о зарегистрированных кнопках и их текстах
     */
    public KeyboardFactory(@Lazy ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    /**
     * Создаёт универсальный билдер клавиатур.
     * <p>Билдер позволяет создавать как обычные (Reply),
     * так и встроенные (Inline) клавиатуры.
     *
     * @return экземпляр {@link UniversalKeyboardBuilder}
     * @author marensovich
     * @since 0.0.1
     */
    public UniversalKeyboardBuilder create() {
        return new UniversalKeyboardBuilder(buttonManager);
    }

    /**
     * Вложенный класс, реализующий шаблон проектирования "Билдер"
     * для создания клавиатур Telegram (Reply и Inline).
     * <p>Не требует экземпляра внешнего класса {@link KeyboardFactory}.
     * @since 0.0.1
     * @version 0.0.2
     * @author marensovich
     */
    public static class UniversalKeyboardBuilder {

        private final ButtonManager buttonManager;

        /**
         * Конструктор билдера клавиатур.
         *
         * @author marensovich
         * @since 0.0.1
         * @param buttonManager менеджер кнопок, предоставляющий тексты кнопок
         *                      и их callback-данные
         */
        public UniversalKeyboardBuilder(@Lazy ButtonManager buttonManager) {
            this.buttonManager = buttonManager;
        }

        // --- Reply-кнопки ---
        private final List<KeyboardRow> rows = new ArrayList<>();
        private KeyboardRow currentRow = new KeyboardRow();

        /**
         * Добавляет кнопку в текущий ряд, используя зарегистрированный класс кнопки.
         *
         * @param buttonClass класс кнопки, реализующий интерфейс {@link Button}
         * @return текущий экземпляр билдера
         * @author marensovich
         * @since 0.0.1
         */
        public UniversalKeyboardBuilder addButton(Class<? extends Button> buttonClass) {
            Button button = buttonManager.getByClass(buttonClass);
            if (button != null) {
                currentRow.add(new KeyboardButton(button.getButtonText()));
            }
            return this;
        }

        /**
         * Добавляет обычную текстовую кнопку в текущий ряд.
         *
         * @param text текст кнопки
         * @return текущий экземпляр билдера
         * @author marensovich
         * @since 0.0.1
         */
        public UniversalKeyboardBuilder addButton(String text) {
            currentRow.add(new KeyboardButton(text));
            return this;
        }

        /**
         * Завершает текущий ряд кнопок и создаёт новый.
         *
         * @return текущий экземпляр билдера
         * @author marensovich
         * @since 0.0.1
         */
        public UniversalKeyboardBuilder nextRow() {
            rows.add(currentRow);
            currentRow = new KeyboardRow();
            return this;
        }

        /**
         * Завершает построение Reply-клавиатуры и возвращает готовую разметку.
         *
         * @return объект {@link ReplyKeyboardMarkup} для отправки в Telegram API
         * @author marensovich
         * @since 0.0.1
         */
        public ReplyKeyboardMarkup buildReplyKeyboard() {
            if (!currentRow.isEmpty()) rows.add(currentRow);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setResizeKeyboard(true);
            markup.setKeyboard(rows);
            return markup;
        }

        // --- Inline-кнопки ---
        private final List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();
        private List<InlineKeyboardButton> currentInlineRow = new ArrayList<>();

        /**
         * Добавляет Inline-кнопку, связанную с классом {@link Button}.
         *
         * @param buttonClass класс кнопки, реализующий интерфейс {@link Button}
         * @return текущий экземпляр билдера
         * @author marensovich
         * @since 0.0.1
         */
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

        /**
         * Добавляет Inline-кнопку с заданным текстом и callback-данными.
         *
         * @param text         текст кнопки
         * @param callbackData данные, передаваемые при нажатии кнопки
         * @return текущий экземпляр билдера
         * @author marensovich
         * @since 0.0.1
         */
        public UniversalKeyboardBuilder addInlineButton(String text, String callbackData) {
            InlineKeyboardButton inlineButton = new InlineKeyboardButton();
            inlineButton.setText(text);
            inlineButton.setCallbackData(callbackData);
            currentInlineRow.add(inlineButton);
            return this;
        }

        /**
         * Завершает текущий ряд Inline-кнопок и создаёт новый.
         *
         * @return текущий экземпляр билдера
         * @author marensovich
         * @since 0.0.1
         */
        public UniversalKeyboardBuilder nextInlineRow() {
            inlineRows.add(currentInlineRow);
            currentInlineRow = new ArrayList<>();
            return this;
        }

        /**
         * Завершает построение Inline-клавиатуры и возвращает готовую разметку.
         *
         * @return объект {@link InlineKeyboardMarkup} для отправки в Telegram API
         * @author marensovich
         * @since 0.0.1
         */
        public InlineKeyboardMarkup buildInlineKeyboard() {
            if (!currentInlineRow.isEmpty()) inlineRows.add(currentInlineRow);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(inlineRows);
            return markup;
        }
    }
}
