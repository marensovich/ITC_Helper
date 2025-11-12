package me.marensovich.itsKipfin.database.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import me.marensovich.itsKipfin.data.Department;
import me.marensovich.itsKipfin.data.Role;


/**
 * Таблица позиций пользователей.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Data
@Embeddable
public class Position {

    /**
     * Отдел пользователя.
     *
     * @since 0.0.1
     */
    @Enumerated(EnumType.STRING)
    private Department department;

    /**
     * Роль пользователя в отделе.
     *
     * @since 0.0.1
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Instantiates a new Position.
     *
     * @param department the department
     * @param role       the role
     */
    public Position(Department department, Role role) {
        this.department = department;
        this.role = role;
    }

    /**
     * Instantiates a new Position.
     */
    public Position() {

    }
}
