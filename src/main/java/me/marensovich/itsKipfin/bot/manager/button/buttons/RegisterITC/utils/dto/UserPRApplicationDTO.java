package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.PRHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * DTO временных данных заявки пользователя, хранится в {@link PRHandler#userApplicationDataMap}.
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
public class UserPRApplicationDTO extends BaseApplicationDTO {

    /**
     * Причина желания вступить в PR
     *
     * @since 0.0.1
     */
    private String reasonToJoin;

    /**
     * Опыт ведения соц. сетей
     *
     * @since 0.0.1
     */
    private String experience;

    /**
     * Список интересов
     *
     * @since 0.0.1
     */
    private List<String> interests = new ArrayList<>();

    /**
     * Сколько можно выбрать (по умолчанию 2). Минимум всегда 2.
     * Верхнего предела нет.
     */
    @JsonIgnore
    private int interestsLimit = 2;

    /**
     * Вопросы к руководителям направления
     *
     * @since 0.0.1
     */
    private String questions;

    /**
     * Текущий шаг
     *
     * @since 0.0.1
     */
    @JsonIgnore
    private PRHandler.Step currentStep = PRHandler.Step.FULL_NAME;

    /**
     * Сброс всех полей в начальное состояние.
     *
     * @author marensovich
     * @since 0.0.1
     */
    public void reset() {
        super.reset();
        reasonToJoin = null;
        experience = null;
        interests.clear();
        questions = null;
        interestsLimit = 2;
        currentStep = PRHandler.Step.FULL_NAME;
    }


    /**
     * Add interest.
     *
     * @param choice the choice
     */
    public void addInterest(String choice) {
        if (!interests.contains(choice) && interests.size() < interestsLimit) {
            interests.add(choice);
        }
    }

    /**
     * Remove interest.
     *
     * @param choice the choice
     */
    public void removeInterest(String choice) {
        interests.remove(choice);
    }

    /**
     * Is interest selected boolean.
     *
     * @param choice the choice
     * @return the boolean
     */
    public boolean isInterestSelected(String choice) {
        return interests.contains(choice);
    }

    /**
     * Remaining interests int.
     *
     * @return the int
     */
    public int remainingInterests() {
        return Math.max(0, interestsLimit - interests.size());
    }

    /**
     * Увеличить лимит на delta (delta > 0) — верхнего предела нет.
     * Если delta отрицательное — уменьшаем лимит, но не ниже 2.
     *
     * @param delta the delta
     */
    public void changeInterestsLimit(int delta) {
        this.interestsLimit += delta;
        if (this.interestsLimit < 2) this.interestsLimit = 2; // минимальный лимит = 2
    }

}
