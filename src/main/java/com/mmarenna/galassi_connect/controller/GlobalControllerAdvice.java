package com.mmarenna.galassi_connect.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${app.version}")
    private String appVersion;

    @ModelAttribute("appVersion")
    public String getAppVersion() {
        return appVersion;
    }
}