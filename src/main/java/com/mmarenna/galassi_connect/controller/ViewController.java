package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.model.entity.Credencial;
import com.mmarenna.galassi_connect.model.entity.Usuario;
import com.mmarenna.galassi_connect.repository.CredencialRepository;
import com.mmarenna.galassi_connect.service.EmpresaService;
import com.mmarenna.galassi_connect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CredencialRepository credencialRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("empresas", empresaService.getAll());
        model.addAttribute("usuarios", usuarioService.getAll());
        return "admin/dashboard";
    }

    @GetMapping("/user")
    public String userDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Credencial credencial = credencialRepository.findByUsername(username).orElse(null);
        
        if (credencial != null && credencial.getUsuario() != null) {
            model.addAttribute("usuario", credencial.getUsuario());
            return "user/dashboard";
        }
        
        return "redirect:/login?error=true";
    }
}