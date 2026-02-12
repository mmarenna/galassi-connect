package com.mmarenna.galassi_connect.service;

import com.mmarenna.galassi_connect.model.entity.File;
import com.mmarenna.galassi_connect.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Asegura que todas las operaciones manejen transacciones correctamente (necesario para LOBs en Postgres)
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Transactional(readOnly = true)
    public List<File> getAll() {
        return fileRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<File> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<File> getByEmpresaId(Long empresaId) {
        return fileRepository.findByEmpresaId(empresaId);
    }

    @Transactional(readOnly = true)
    public List<File> getByEmpresaIdAndType(Long empresaId, String type) {
        List<File> files = fileRepository.findByEmpresaId(empresaId);
        if (type != null && !type.isEmpty()) {
            return files.stream()
                    .filter(f -> type.equals(f.getType()))
                    .collect(Collectors.toList());
        }
        return files;
    }

    public File save(File file) {
        return fileRepository.save(file);
    }

    // Nuevo método para guardar archivos binarios
    public File store(MultipartFile multipartFile, Long empresaId, String description, String type) throws IOException {
        File file = new File();
        file.setName(multipartFile.getOriginalFilename());
        file.setContentType(multipartFile.getContentType());
        file.setData(multipartFile.getBytes());
        file.setEmpresaId(empresaId);
        file.setDescription(description);
        file.setType(type);

        return fileRepository.save(file);
    }
}