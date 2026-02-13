package com.mmarenna.galassi_connect.repository;

import com.mmarenna.galassi_connect.model.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    // Usamos @Query para evitar la confusión de Spring Data con los guiones bajos
    @Query("SELECT e FROM Empresa e WHERE e.reference_id = :refId")
    Optional<Empresa> findByReference_id(@Param("refId") Long refId);
}