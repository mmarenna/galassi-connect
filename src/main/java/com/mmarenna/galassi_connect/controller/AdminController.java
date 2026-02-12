package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.model.dto.EmpresaDTO;
import com.mmarenna.galassi_connect.model.dto.FileDTO;
import com.mmarenna.galassi_connect.model.dto.UsuarioDTO;
import com.mmarenna.galassi_connect.model.dto.VendedorDTO;
import com.mmarenna.galassi_connect.model.entity.Empresa;
import com.mmarenna.galassi_connect.model.entity.File;
import com.mmarenna.galassi_connect.model.entity.Usuario;
import com.mmarenna.galassi_connect.model.entity.Vendedor;
import com.mmarenna.galassi_connect.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private VendedorService vendedorService;

    // ==========================================
    // 1. GESTIÓN DE ARCHIVOS
    // ==========================================

    @PostMapping("/empresas/{empresaId}/files")
    public ResponseEntity<?> uploadFile(
            @PathVariable Long empresaId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam("type") String type) {
        
        Empresa empresa = empresaService.getById(empresaId);
        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            File savedFile = fileService.store(file, empresaId, description, type);
            return ResponseEntity.ok(mapToFileDTO(savedFile));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. GESTIÓN DE EMPRESAS
    // ==========================================

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaDTO>> getAllEmpresas() {
        List<Empresa> empresas = empresaService.getAll();
        return ResponseEntity.ok(empresas.stream().map(this::mapToEmpresaDTO).collect(Collectors.toList()));
    }

    @PostMapping("/empresas")
    public ResponseEntity<EmpresaDTO> createEmpresa(@RequestBody EmpresaDTO empresaDTO) {
        if (empresaDTO.getReference_id() == null || empresaDTO.getReference_id().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Empresa empresa = new Empresa();
        empresa.setName(empresaDTO.getName());
        empresa.setReference_id(empresaDTO.getReference_id());
        
        Empresa saved = empresaService.save(empresa);
        return ResponseEntity.ok(mapToEmpresaDTO(saved));
    }

    @PutMapping("/empresas/{id}")
    public ResponseEntity<EmpresaDTO> updateEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        Empresa empresa = empresaService.getById(id);
        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        
        empresa.setName(empresaDTO.getName());
        empresa.setReference_id(empresaDTO.getReference_id());
        
        Empresa updated = empresaService.save(empresa);
        return ResponseEntity.ok(mapToEmpresaDTO(updated));
    }

    @DeleteMapping("/empresas/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long id) {
        Empresa empresa = empresaService.getById(id);
        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // 3. GESTIÓN DE VENDEDORES
    // ==========================================

    @PostMapping("/empresas/{empresaId}/vendedores")
    public ResponseEntity<VendedorDTO> createVendedorForEmpresa(
            @PathVariable Long empresaId,
            @RequestBody VendedorDTO vendedorDTO) {
        
        Empresa empresa = empresaService.getById(empresaId);
        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }

        if (vendedorDTO.getReference_id() == null || vendedorDTO.getReference_id().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Vendedor vendedor = new Vendedor();
        vendedor.setName(vendedorDTO.getName());
        vendedor.setReference_id(vendedorDTO.getReference_id());
        vendedor.setEmpresaId(empresaId);

        Vendedor saved = vendedorService.save(vendedor);
        return ResponseEntity.ok(mapToVendedorDTO(saved));
    }

    // ==========================================
    // 4. GESTIÓN DE USUARIOS (CLIENTES)
    // ==========================================

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAll();
        return ResponseEntity.ok(usuarios.stream().map(this::mapToUsuarioDTO).collect(Collectors.toList()));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getReference_id() == null || usuarioDTO.getReference_id().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (usuarioDTO.getEmpresaId() != null) {
            Empresa empresa = empresaService.getById(usuarioDTO.getEmpresaId());
            if (empresa == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        Usuario usuario = new Usuario();
        usuario.setName(usuarioDTO.getName());
        usuario.setReference_id(usuarioDTO.getReference_id());
        usuario.setEmpresaId(usuarioDTO.getEmpresaId());

        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.ok(mapToUsuarioDTO(saved));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.findById(id).map(usuario -> {
            usuario.setName(usuarioDTO.getName());
            usuario.setReference_id(usuarioDTO.getReference_id());
            usuario.setEmpresaId(usuarioDTO.getEmpresaId());
            
            Usuario updated = usuarioService.save(usuario);
            return ResponseEntity.ok(mapToUsuarioDTO(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (usuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mappers Manuales ---

    private FileDTO mapToFileDTO(File file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setDescription(file.getDescription());
        dto.setType(file.getType());
        dto.setEmpresaId(file.getEmpresaId());
        return dto;
    }

    private EmpresaDTO mapToEmpresaDTO(Empresa empresa) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setName(empresa.getName());
        dto.setReference_id(empresa.getReference_id());
        return dto;
    }

    private VendedorDTO mapToVendedorDTO(Vendedor vendedor) {
        VendedorDTO dto = new VendedorDTO();
        dto.setId(vendedor.getId());
        dto.setName(vendedor.getName());
        dto.setReference_id(vendedor.getReference_id());
        dto.setEmpresaId(vendedor.getEmpresaId());
        return dto;
    }

    private UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setName(usuario.getName());
        dto.setReference_id(usuario.getReference_id());
        dto.setEmpresaId(usuario.getEmpresaId());
        return dto;
    }
}