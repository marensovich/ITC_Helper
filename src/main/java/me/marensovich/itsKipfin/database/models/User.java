package me.marensovich.itsKipfin.database.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Сущность пользователя Telegram-бота.
 * <p>
 * Хранит основные данные о пользователе: его идентификатор и статус администратора.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя (Telegram ID).
     * @since 0.0.1
     */
    @Id
    @Column(name = "userId", unique = true)
    private Long userId;

    /**
     * Позиция пользователя в ИТС
     * @since 0.0.1
     */
    @Embedded
    private Position position;

    /**
     * Флаг, указывающий, является ли пользователь администратором.
     * @since 0.0.1
     */
    @Column(name = "isAdmin")
    private boolean isAdmin;
}
