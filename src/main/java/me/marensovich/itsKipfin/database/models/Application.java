package me.marensovich.itsKipfin.database.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Сущность заявки пользователя на вступление в отдел.
 * <p>
 * Содержит информацию о пользователе, отделе, статусе заявки
 * и сериализованные данные анкеты в формате JSON.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Data
@Entity
@Slf4j
@Table(name = "applications")
public class Application {

    /**
     * Перечисление доступных отделов ИТС.
     * @author marensovich
     * @version 0.0.1
     * @since 0.0.1
     */
    public enum Departament {
        /** Отдел разработки. */
        Development,
        /** Медиа-отдел. */
        Media,
        /** Отдел коммуникаций. */
        Communication,
        /** Отдел дизайнеров. */
        Designer
    }

    /**
     * Перечисление возможных статусов заявки.
     * @author marensovich
     * @version 0.0.1
     * @since 0.0.1
     */
    public enum Status {
        /** Заявка ожидает рассмотрения. */
        PENDING,
        /** Заявка одобрена. */
        APPROVED,
        /** Заявка отклонена. */
        REJECTED
    }

    /**
     * Уникальный идентификатор заявки.
     * @since 0.0.1
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя Telegram, подавшего заявку.
     * @since 0.0.1
     */
    private Long userId;

    /**
     * Идентификатор Telegram-сообщения, связанного с заявкой (для обновлений/редактирования).
     * @since 0.0.1
     */
    private Long messageId;

    /**
     * Отдел, в который подана заявка.
     * @since 0.0.1
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Departament departament;

    /**
     * Текущий статус заявки.
     * @since 0.0.1
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /**
     * Дата и время создания заявки.
     * @since 0.0.1
     */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Сериализованные данные анкеты пользователя (в формате JSON).
     * @since 0.0.1
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String data;

    /**
     * Сериализует DTO-объект в JSON и сохраняет в поле {@code data}.
     *
     * @param <T> тип DTO
     * @param dto объект данных пользователя
     * @throws RuntimeException если произошла ошибка сериализации
     * @author marensovich
     * @since 0.0.1
     */
    public <T> void setDataObject(T dto) {
        try {
            this.data = new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации DTO", e);
        }
    }

    /**
     * Десериализует JSON из {@code data} обратно в указанный тип объекта.
     *
     * @param <T>  тип возвращаемого объекта
     * @param type класс, в который нужно преобразовать данные
     * @return десериализованный объект DTO
     * @throws RuntimeException если произошла ошибка десериализации
     * @author marensovich
     * @since 0.0.1
     */
    public <T> T getDataObject(Class<T> type) {
        try {
            return new ObjectMapper().readValue(this.data, type);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации DTO. targetClass={}, json={}", type.getName(), this.data, e);
            throw new RuntimeException("Ошибка десериализации DTO: " + e.getMessage(), e);
        }
    }
}
