package me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.dto;

import lombok.Getter;
import lombok.Setter;


/**
 * Базовая DTO с обязательной информацией для подачи заявок в ИТС
 *
 * @version 0.0.1
 * @since 0.0.1
 * @author marensovich
 */
@Getter
@Setter
public class BaseApplicationDTO {

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

}
