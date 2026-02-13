package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;

@Data
public class VendedorDTO {
    private Long id;
    private Long reference_id;
    private String name;
    private Long empresaId;
}