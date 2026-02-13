package com.mmarenna.galassi_connect.config;

import com.mmarenna.galassi_connect.model.entity.Credencial;
import com.mmarenna.galassi_connect.repository.CredencialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CredencialRepository credencialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminUser = "admin";
        String adminPass = "Admin2026";
        
        Optional<Credencial> adminOpt = credencialRepository.findByUsername(adminUser);
        
        Credencial admin;
        if (adminOpt.isPresent()) {
            admin = adminOpt.get();
            System.out.println("Usuario ADMIN existente. Actualizando contraseña...");
        } else {
            admin = new Credencial();
            admin.setUsername(adminUser);
            admin.setRole("ADMIN");
            System.out.println("Creando usuario ADMIN...");
        }
        
        // Siempre aseguramos que la contraseña sea la correcta
        admin.setPassword(passwordEncoder.encode(adminPass));
        credencialRepository.save(admin);
        
        System.out.println("Usuario ADMIN listo: " + adminUser + " / " + adminPass);
    }
}