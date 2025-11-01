package me.marensovich.itsKipfin.database.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Application.
 */
@Data
@Entity
@Table(name = "applications")
public class Application {

    /**
     * The enum Departament.
     */
    public enum Departament {
        /**
         * Development departament.
         */
        Development,
        /**
         * Media departament.
         */
        Media,
        /**
         * Communication departament.
         */
        Communication,
        /**
         * Designer departament.
         */
        Designer
    }

    /**
     * The enum Status.
     */
    public enum Status {
        /**
         * Pending status.
         */
        PENDING,
        /**
         * Approved status.
         */
        APPROVED,
        /**
         * Rejected status.
         */
        REJECTED
    }

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Departament departament;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String data;


    /**
     * Sets data object.
     *
     * @param <T> the type parameter
     * @param dto the dto
     */
    public <T> void setDataObject(T dto) {
        try {
            this.data = new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации DTO", e);
        }
    }

    /**
     * Gets data object.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the data object
     */
    public <T> T getDataObject(Class<T> type) {
        try {
            return new ObjectMapper().readValue(this.data, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка десериализации DTO", e);
        }
    }

}
