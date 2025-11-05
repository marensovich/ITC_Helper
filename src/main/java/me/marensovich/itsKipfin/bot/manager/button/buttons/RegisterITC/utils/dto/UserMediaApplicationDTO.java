package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.MediaHandler;

/**
 * DTO временных данных заявки пользователя, хранится в {@link MediaHandler#userApplicationDataMap}.
 *
 * <p>Поле {@code currentStep} помечено {@link JsonIgnore} чтобы при сериализации DTO
 * в базу (если потребуется) шаг не сохранялся автоматически.</p>
 * @author marensovich
 * @since 0.0.1
 * @version 0.0.1
 */
@Getter @Setter
public class UserMediaApplicationDTO {
    /**
     * Упоминание пользователя в Telegram (например @login)
     * @since 0.0.1
     */
    private String mention;

    /**
     * Telegram id пользователя (строка)
     * @since 0.0.1
     */
    private String tgId;

    /**
     * ФИО
     * @since 0.0.1
     */
    private String fullName;

    /**
     * Телефон
     * @since 0.0.1
     */
    private String phoneNumber;

    /**
     * Номер учебной группы
     * @since 0.0.1
     */
    private String groupNumber;

    /**
     * Описание опыта
     * @since 0.0.1
     */
    private String experience;

    /**
     * Наличие фотоаппарата
     * @since 0.0.1
     */
    private Boolean hasPhoto;

    /**
     * Текущий шаг
     * @since 0.0.1
     */
    @JsonIgnore
    private MediaHandler.Step currentStep = MediaHandler.Step.FULL_NAME;

    /**
     * Сброс всех полей в начальное состояние.
     * @since 0.0.1
     * @author marensovich
     */
    public void reset() {
        mention = null;
        tgId = null;
        fullName = null;
        phoneNumber = null;
        groupNumber = null;
        experience = null;
        hasPhoto = null;
        currentStep = MediaHandler.Step.FULL_NAME;
    }
}