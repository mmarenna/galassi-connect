package com.mmarenna.galassi_connect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/auth/login")
    public String processLogin(@RequestParam String username, @RequestParam String password) {
        // Simulación básica de autenticación
        if ("admin".equalsIgnoreCase(username)) {
            return "redirect:/admin";
        } else if ("user".equalsIgnoreCase(username)) {
            return "redirect:/user";
        } else {
            return "redirect:/login?error";
        }
    }
}