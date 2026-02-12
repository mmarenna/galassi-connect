package com.mmarenna.galassi_connect.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference_id; // IdCliente
    private String name;
    private String email;
    private String direccion;
    private String localidad;
    private String telefono;
    private String provincia;
    private String cuit;
    private Long empresaId; // Relación con la empresa (Restaurado)
}