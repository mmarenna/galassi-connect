package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String reference_id;
    private String name;
    private Long empresaId;
}