package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.DesignerHandler;

/**
 * DTO временных данных заявки пользователя, хранится в {@link DesignerHandler#userApplicationDataMap}.
 *
 * <p>Поле {@code currentStep} помечено {@link JsonIgnore} чтобы при сериализации DTO
 * в базу (если потребуется) шаг не сохранялся автоматически.</p>
 * @author marensovich
 * @since 0.0.1
 * @version 0.0.1
 */
@Getter
@Setter
public class UserDesignerApplicationDTO extends BaseApplicationDTO {

    /**
     * Основные приложения
     * @since 0.0.1
     */
    private String mainApps;

    /**
     * Примеры работ
     * @since 0.0.1
     */
    private String examples;

    /**
     * Текущий шаг
     * @since 0.0.1
     */
    @JsonIgnore
    private DesignerHandler.Step currentStep = DesignerHandler.Step.FULL_NAME;

    /**
     * Сброс всех полей в начальное состояние.
     * @since 0.0.1
     * @author marensovich
     */
    @Override
    public void reset() {
        super.reset();
        mainApps = null;
        examples = null;
        currentStep = DesignerHandler.Step.FULL_NAME;
    }
}
