package me.marensovich.itsKipfin.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * The type User.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "userId", unique = true)
    private Long userId;

    @Column(name = "isAdmin")
    private boolean isAdmin;


}