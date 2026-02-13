package com.mmarenna.galassi_connect.controller;

import com.mmarenna.galassi_connect.model.dto.EmpresaDTO;
import com.mmarenna.galassi_connect.model.dto.FileDTO;
import com.mmarenna.galassi_connect.model.entity.Empresa;
import com.mmarenna.galassi_connect.model.entity.File;
import com.mmarenna.galassi_connect.model.entity.Usuario;
import com.mmarenna.galassi_connect.model.entity.Vinculacion;
import com.mmarenna.galassi_connect.repository.VinculacionRepository;
import com.mmarenna.galassi_connect.service.EmpresaService;
import com.mmarenna.galassi_connect.service.FileService;
import com.mmarenna.galassi_connect.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FileService fileService;

    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private VinculacionRepository vinculacionRepository;

    // Vista de Empresas: El usuario ve sus empresas asignadas
    @GetMapping("/{userId}/empresas")
    public ResponseEntity<List<EmpresaDTO>> getUserEmpresas(@PathVariable Long userId) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(userId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            List<Vinculacion> vinculaciones = vinculacionRepository.findByUsuarioReferenceId(usuario.getReference_id());
            
            List<EmpresaDTO> empresas = new ArrayList<>();
            for (Vinculacion v : vinculaciones) {
                Optional<Empresa> emp = empresaService.getByReferenceId(v.getEmpresaReferenceId());
                emp.ifPresent(value -> empresas.add(mapToEmpresaDTO(value)));
            }
            return ResponseEntity.ok(empresas);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Acceso a Archivos: Listar y filtrar archivos de sus empresas
    @GetMapping("/{userId}/files")
    public ResponseEntity<List<FileDTO>> getUserFiles(
            @PathVariable Long userId,
            @RequestParam(required = false) String type) {
        
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

            List<File> files = fileService.getByEmpresaIdsAndType(empresaIds, type);
            List<FileDTO> dtos = files.stream()
                    .map(this::mapToFileDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Descargas
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Optional<File> fileOpt = fileService.findById(fileId);
        
        if (fileOpt.isPresent()) {
            File file = fileOpt.get();
            ByteArrayResource resource = new ByteArrayResource(file.getData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // --- Mappers Manuales ---

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

    private FileDTO mapToFileDTO(File file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setDescription(file.getDescription());
        dto.setType(file.getType());
        dto.setEmpresaId(file.getEmpresaId());
        return dto;
    }
}