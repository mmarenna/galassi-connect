package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.model.entity.Usuario;
import com.mmarenna.galassi_connect.service.EmpresaService;
import com.mmarenna.galassi_connect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ViewController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("empresas", empresaService.getAll());
        model.addAttribute("usuarios", usuarioService.getAll()); // Agregado para el dropdown de auditoría
        return "admin/dashboard";
    }

    @GetMapping("/user")
    public String userDashboard(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login?error=true";
        }

        model.addAttribute("usuario", usuarioOpt.get());
        return "user/dashboard";
    }
}