package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String reference_id;
    private String name;
    private String email;
    private String direccion;
    private String localidad;
    private String telefono;
    private String provincia;
    private String cuit;
    private Long empresaId; // Restaurado a Long simple
}