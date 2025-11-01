package me.marensovich.itsKipfin.database.models;

import jakarta.persistence.*;
import lombok.Data;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.utils.JsonConverter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications")
public class Applications {

    public enum Departament {
        Development,
        Media,
        Communication,
        Designer
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Departament departament;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Convert(converter = JsonConverter.class)
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private RegisterITCButton.ProjectTeamHandler.UserApplicationData data;

}
