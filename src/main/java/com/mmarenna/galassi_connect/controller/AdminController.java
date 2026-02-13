package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.model.dto.*;
import com.mmarenna.galassi_connect.model.entity.*;
import com.mmarenna.galassi_connect.repository.CredencialRepository;
import com.mmarenna.galassi_connect.repository.VinculacionRepository;
import com.mmarenna.galassi_connect.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    
    @Autowired
    private VinculacionRepository vinculacionRepository;

    @Autowired
    private CredencialRepository credencialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/empresas/{empresaId}/files")
    public ResponseEntity<List<FileDTO>> getFilesByEmpresa(@PathVariable Long empresaId) {
        List<File> files = fileService.getByEmpresaId(empresaId);
        List<FileDTO> dtos = files.stream().map(this::mapToFileDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        Optional<File> file = fileService.findById(fileId);
        if (file.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        fileService.delete(fileId);
        return ResponseEntity.noContent().build();
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
        if (dto.getReference_id() == null) {
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

        if (vendedorDTO.getReference_id() == null) {
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
        if (dto.getReference_id() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Usuario usuario = new Usuario();
        updateUsuarioFromDTO(usuario, dto);

        Usuario saved = usuarioService.save(usuario);
        
        if (dto.getEmpresaIds() != null) {
            for (Long empresaId : dto.getEmpresaIds()) {
                Empresa emp = empresaService.getById(empresaId);
                if (emp != null) {
                    Vinculacion v = new Vinculacion();
                    v.setUsuarioReferenceId(saved.getReference_id());
                    v.setEmpresaReferenceId(emp.getReference_id());
                    v.setVendedorReferenceId(0L);
                    vinculacionRepository.save(v);
                }
            }
        }

        return ResponseEntity.ok(mapToUsuarioDTO(saved));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return usuarioService.findById(id).map(usuario -> {
            List<Vinculacion> vinculaciones = vinculacionRepository.findByUsuarioReferenceId(usuario.getReference_id());
            vinculacionRepository.deleteAll(vinculaciones);

            updateUsuarioFromDTO(usuario, dto);
            Usuario updated = usuarioService.save(usuario);
            
            if (dto.getEmpresaIds() != null) {
                for (Long empresaId : dto.getEmpresaIds()) {
                    Empresa emp = empresaService.getById(empresaId);
                    if (emp != null) {
                        Vinculacion v = new Vinculacion();
                        v.setUsuarioReferenceId(updated.getReference_id());
                        v.setEmpresaReferenceId(emp.getReference_id());
                        v.setVendedorReferenceId(0L);
                        vinculacionRepository.save(v);
                    }
                }
            }
            
            return ResponseEntity.ok(mapToUsuarioDTO(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        Optional<Usuario> u = usuarioService.findById(id);
        if (u.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Vinculacion> vinculaciones = vinculacionRepository.findByUsuarioReferenceId(u.get().getReference_id());
        vinculacionRepository.deleteAll(vinculaciones);
        
        // También borrar credenciales si existen
        credencialRepository.findByUsuarioId(id).ifPresent(credencialRepository::delete);
        
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- NUEVO: Auditoría de Archivos de Usuario ---
    @GetMapping("/usuarios/{userId}/files")
    public ResponseEntity<List<FileDTO>> getUserFilesForAdmin(@PathVariable Long userId) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            List<Vinculacion> vinculaciones = vinculacionRepository.findByUsuarioReferenceId(usuario.getReference_id());
            
            List<Long> empresaIds = new ArrayList<>();
            for (Vinculacion v : vinculaciones) {
                Optional<Empresa> emp = empresaService.getByReferenceId(v.getEmpresaReferenceId());
                emp.ifPresent(value -> empresaIds.add(value.getId()));
            }
            
            if (empresaIds.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<File> files = fileService.getByEmpresaIdsAndType(empresaIds, null);
            List<FileDTO> dtos = files.stream()
                    .map(this::mapToFileDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // --- NUEVO: Gestión de Credenciales ---
    @GetMapping("/usuarios/{userId}/credenciales")
    public ResponseEntity<CredencialDTO> getCredencial(@PathVariable Long userId) {
        Optional<Credencial> cred = credencialRepository.findByUsuarioId(userId);
        CredencialDTO dto = new CredencialDTO();
        if (cred.isPresent()) {
            dto.setUsername(cred.get().getUsername());
            dto.setHasAccess(true);
        } else {
            dto.setHasAccess(false);
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/usuarios/{userId}/credenciales")
    public ResponseEntity<CredencialDTO> saveCredencial(@PathVariable Long userId, @RequestBody CredencialDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        Credencial credencial = credencialRepository.findByUsuarioId(userId).orElse(new Credencial());
        
        credencial.setUsuario(usuarioOpt.get());
        credencial.setUsername(dto.getUsername());
        credencial.setPassword(passwordEncoder.encode(dto.getPassword()));
        credencial.setRole("USER");

        credencialRepository.save(credencial);
        
        dto.setHasAccess(true);
        dto.setPassword(null); // No devolver el password
        return ResponseEntity.ok(dto);
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
        entity.setCuentas_bancarias(dto.getCuentas_bancarias());
    }

    private void updateUsuarioFromDTO(Usuario entity, UsuarioDTO dto) {
        entity.setName(dto.getName());
        entity.setReference_id(dto.getReference_id());
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
        dto.setCuentas_bancarias(entity.getCuentas_bancarias());
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
        dto.setEmail(entity.getEmail());
        dto.setDireccion(entity.getDireccion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setTelefono(entity.getTelefono());
        dto.setProvincia(entity.getProvincia());
        dto.setCuit(entity.getCuit());
        
        List<Vinculacion> vinculaciones = vinculacionRepository.findByUsuarioReferenceId(entity.getReference_id());
        List<Long> empresaIds = new ArrayList<>();
        for (Vinculacion v : vinculaciones) {
            Optional<Empresa> emp = empresaService.getByReferenceId(v.getEmpresaReferenceId());
            emp.ifPresent(value -> empresaIds.add(value.getId()));
        }
        dto.setEmpresaIds(empresaIds);

        return dto;
    }
}