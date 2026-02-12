package com.mmarenna.galassi_connect.service;

import com.mmarenna.galassi_connect.model.entity.Vendedor;
import com.mmarenna.galassi_connect.repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VendedorService {
    @Autowired
    private VendedorRepository vendedorRepository;

    public List<Vendedor> getAll() {
        return vendedorRepository.findAll();
    }

    public List<Vendedor> getByEmpresaId(Long empresaId) {
        return vendedorRepository.findByEmpresaId(empresaId);
    }

    public Vendedor save(Vendedor vendedor) {
        return vendedorRepository.save(vendedor);
    }
}