package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;

@Data
public class FileDTO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long empresaId;
    // No incluimos 'data' ni 'contentType' a menos que sea necesario
}