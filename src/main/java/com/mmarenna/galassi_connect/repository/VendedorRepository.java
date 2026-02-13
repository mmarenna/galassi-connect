package com.mmarenna.galassi_connect.repository;

import com.mmarenna.galassi_connect.model.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
    List<Vendedor> findByEmpresaId(Long empresaId);

    @Query("SELECT v FROM Vendedor v WHERE v.reference_id = :refId")
    Optional<Vendedor> findByReference_id(@Param("refId") Long refId);
}