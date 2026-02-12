package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.service.EmpresaService;
import com.mmarenna.galassi_connect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String home() {
        return "home"; // Devuelve el nombre de la plantilla: home.html
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("empresas", empresaService.getAll());
        return "admin/dashboard";
    }

    @GetMapping("/user")
    public String userDashboard(Model model) {
        model.addAttribute("usuarios", usuarioService.getAll());
        return "user/dashboard";
    }
}