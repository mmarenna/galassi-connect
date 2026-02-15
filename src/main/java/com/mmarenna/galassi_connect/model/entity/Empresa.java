package com.mmarenna.galassi_connect.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reference_id; // IDPROVGALASI
    private String name;
    private String direccion;
    private String localidad;
    private String telefono;
    private String cuit;
    private String cp;
    private String provincia;
    private String email;
    
    @Column(length = 300)
    private String cuentas_bancarias;

    private String image_name; // Nuevo campo para el logo
}