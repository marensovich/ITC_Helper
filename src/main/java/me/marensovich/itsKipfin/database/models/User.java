package me.marensovich.itsKipfin.database.models;

import jakarta.persistence.*;
import lombok.Data;

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