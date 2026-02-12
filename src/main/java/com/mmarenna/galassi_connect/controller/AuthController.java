package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        // Pasamos la lista de usuarios para el dropdown
        model.addAttribute("usuarios", usuarioService.getAll());
        return "login";
    }

    @PostMapping("/auth/login")
    public String processLogin(@RequestParam String userId, @RequestParam String password) {
        // Simulación básica
        if ("admin".equals(userId)) {
            return "redirect:/admin";
        } else {
            // Asumimos que es un ID de usuario válido
            return "redirect:/user?id=" + userId;
        }
    }
}