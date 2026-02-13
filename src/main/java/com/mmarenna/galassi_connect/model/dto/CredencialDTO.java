package com.mmarenna.galassi_connect.model.dto;

import lombok.Data;

@Data
public class CredencialDTO {
    private String username;
    private String password;
    private boolean hasAccess; // Para saber si ya tiene credencial creada
}