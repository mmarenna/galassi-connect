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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<EmpresaDTO>> getAllEmpresas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Empresa> empresasPage = empresaService.getAllPaginated(pageable);
        Page<EmpresaDTO> dtoPage = empresasPage.map(this::mapToEmpresaDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping("/empresas")
    public ResponseEntity<EmpresaDTO> createEmpresa(@RequestBody EmpresaDTO dto) {
        if (dto.getReference_id() == null || dto.getReference_id().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Empresa empresa = new Empresa();
        updateEmpresaFromDTO(empresa, dto);
        
        Empresa saved = empresaService.save(empresa);
        return ResponseEntity.ok(mapToEmpresaDTO(saved));
    }

    @PutMapping("/empresas/{id}")
    public ResponseEntity<EmpresaDTO> updateEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO dto) {
        Empresa empresa = empresaService.getById(id);
        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        
        updateEmpresaFromDTO(empresa, dto);
        
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
    public ResponseEntity<Page<UsuarioDTO>> getAllUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage = usuarioService.getAllPaginated(pageable);
        Page<UsuarioDTO> dtoPage = usuariosPage.map(this::mapToUsuarioDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> createUsuario(@RequestBody UsuarioDTO dto) {
        if (dto.getReference_id() == null || dto.getReference_id().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = empresaService.getById(dto.getEmpresaId());
            if (empresa == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        Usuario usuario = new Usuario();
        updateUsuarioFromDTO(usuario, dto);

        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.ok(mapToUsuarioDTO(saved));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return usuarioService.findById(id).map(usuario -> {
            updateUsuarioFromDTO(usuario, dto);
            
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

    // --- Helpers & Mappers ---

    private void updateEmpresaFromDTO(Empresa entity, EmpresaDTO dto) {
        entity.setName(dto.getName());
        entity.setReference_id(dto.getReference_id());
        entity.setDireccion(dto.getDireccion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setTelefono(dto.getTelefono());
        entity.setCuit(dto.getCuit());
        entity.setCp(dto.getCp());
        entity.setProvincia(dto.getProvincia());
        entity.setEmail(dto.getEmail());
    }

    private void updateUsuarioFromDTO(Usuario entity, UsuarioDTO dto) {
        entity.setName(dto.getName());
        entity.setReference_id(dto.getReference_id());
        entity.setEmpresaId(dto.getEmpresaId());
        entity.setEmail(dto.getEmail());
        entity.setDireccion(dto.getDireccion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setTelefono(dto.getTelefono());
        entity.setProvincia(dto.getProvincia());
        entity.setCuit(dto.getCuit());
    }

    private FileDTO mapToFileDTO(File file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setDescription(file.getDescription());
        dto.setType(file.getType());
        dto.setEmpresaId(file.getEmpresaId());
        return dto;
    }

    private EmpresaDTO mapToEmpresaDTO(Empresa entity) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setReference_id(entity.getReference_id());
        dto.setDireccion(entity.getDireccion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setTelefono(entity.getTelefono());
        dto.setCuit(entity.getCuit());
        dto.setCp(entity.getCp());
        dto.setProvincia(entity.getProvincia());
        dto.setEmail(entity.getEmail());
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

    private UsuarioDTO mapToUsuarioDTO(Usuario entity) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setReference_id(entity.getReference_id());
        dto.setEmpresaId(entity.getEmpresaId());
        dto.setEmail(entity.getEmail());
        dto.setDireccion(entity.getDireccion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setTelefono(entity.getTelefono());
        dto.setProvincia(entity.getProvincia());
        dto.setCuit(entity.getCuit());
        return dto;
    }
}