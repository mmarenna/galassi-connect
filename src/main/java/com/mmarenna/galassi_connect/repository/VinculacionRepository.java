package com.mmarenna.galassi_connect.repository;

import com.mmarenna.galassi_connect.model.entity.Vinculacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VinculacionRepository extends JpaRepository<Vinculacion, Long> {
    List<Vinculacion> findByUsuarioReferenceId(Long usuarioReferenceId);
    List<Vinculacion> findByEmpresaReferenceId(Long empresaReferenceId);
    void deleteByUsuarioReferenceId(Long usuarioReferenceId);
}