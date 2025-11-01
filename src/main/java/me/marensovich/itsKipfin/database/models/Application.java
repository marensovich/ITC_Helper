package me.marensovich.itsKipfin.database.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications")
public class Application {

    public enum Departament {
        Development,
        Media,
        Communication,
        Designer
    }

    public enum Status {
        PENDING,
        APPROVED,
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


    public <T> void setDataObject(T dto) {
        try {
            this.data = new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации DTO", e);
        }
    }

    public <T> T getDataObject(Class<T> type) {
        try {
            return new ObjectMapper().readValue(this.data, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка десериализации DTO", e);
        }
    }

}
