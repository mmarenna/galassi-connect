package com.mmarenna.galassi_connect.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Credencial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // "ADMIN" o "USER"

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario; // El cliente asociado (null si es admin puro)
}