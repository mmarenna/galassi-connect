package com.mmarenna.galassi_connect.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Vinculacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long usuarioReferenceId; // IdCliente
    private Long empresaReferenceId; // IDPROVGALASI
    private Long vendedorReferenceId; // numvend
}