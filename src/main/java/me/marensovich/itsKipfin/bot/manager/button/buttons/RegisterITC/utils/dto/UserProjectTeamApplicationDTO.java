package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.ProjectTeamHandler;

/**
 * DTO временных данных заявки пользователя, хранится в {@link ProjectTeamHandler#userApplicationDataMap}.
 *
 * <p>Поле {@code currentStep} помечено {@link JsonIgnore} чтобы при сериализации DTO
 * в базу (если потребуется) шаг не сохранялся автоматически.</p>
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Getter
@Setter
public class UserProjectTeamApplicationDTO extends BaseApplicationDTO {

    /**
     * Описание опыта
     *
     * @since 0.0.1
     */
    private String experience;

    /**
     * Ссылка на GitHub или GitLab
     *
     * @since 0.0.1
     */
    private String gitHub;

    /**
     * Стек технологий
     *
     * @since 0.0.1
     */
    private String stack;

    /**
     * Текущий шаг
     *
     * @since 0.0.1
     */
    @JsonIgnore
    private ProjectTeamHandler.Step currentStep = ProjectTeamHandler.Step.FULL_NAME;

    /**
     * Сброс всех полей в начальное состояние.
     *
     * @author marensovich
     * @since 0.0.1
     */
    public void reset() {
        super.reset();
        experience = null;
        gitHub = null;
        stack = null;
        currentStep = ProjectTeamHandler.Step.FULL_NAME;
    }
}

