package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;

@Data
public class EmpresaDTO {
    private Long id;
    private Long reference_id;
    private String name;
    private String direccion;
    private String localidad;
    private String telefono;
    private String cuit;
    private String cp;
    private String provincia;
    private String email;
    private String cuentas_bancarias;
}