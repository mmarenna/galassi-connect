package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class UsuarioDTO {
    private Long id;
    private Long reference_id;
    private String name;
    private String email;
    private String direccion;
    private String localidad;
    private String telefono;
    private String provincia;
    private String cuit;
    private List<Long> empresaIds; // IDs de BD de las empresas para vincular
}