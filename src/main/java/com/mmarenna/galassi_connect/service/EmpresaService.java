package com.mmarenna.galassi_connect.service;

import com.mmarenna.galassi_connect.model.entity.Empresa;
import com.mmarenna.galassi_connect.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    public List<Empresa> getAll() {
        return empresaRepository.findAll();
    }

    public Page<Empresa> getAllPaginated(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    public Empresa getById(Long id) {
        return empresaRepository.findById(id).orElse(null);
    }

    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public void delete(Long id) {
        empresaRepository.deleteById(id);
    }
}