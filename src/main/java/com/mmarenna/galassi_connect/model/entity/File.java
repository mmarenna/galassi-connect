package com.mmarenna.galassi_connect.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String type; // Filtro de negocio (ej: "Factura", "Contrato")
    
    private Long empresaId;

    private String contentType;

    @Lob
    @Column(length = 10000000)
    private byte[] data;
}