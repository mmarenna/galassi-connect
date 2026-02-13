package com.mmarenna.galassi_connect.repository;

import com.mmarenna.galassi_connect.model.entity.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {
    Optional<Credencial> findByUsername(String username);
    Optional<Credencial> findByUsuarioId(Long usuarioId);
}