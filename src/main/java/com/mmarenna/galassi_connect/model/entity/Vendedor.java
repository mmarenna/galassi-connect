package com.mmarenna.galassi_connect.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Vendedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reference_id; // numvend (Cambiado a Long)
    private String name;
    private Long empresaId; // Mantenemos esto para saber a qué empresa pertenece el vendedor "laboralmente"
}