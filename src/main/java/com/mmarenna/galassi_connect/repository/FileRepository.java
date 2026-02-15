package com.mmarenna.galassi_connect.repository;

import com.mmarenna.galassi_connect.model.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByEmpresaId(Long empresaId);
    List<File> findByEmpresaIdIn(List<Long> empresaIds);
    long countByEmpresaId(Long empresaId); // Nuevo método
}