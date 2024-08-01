package com.ms.sendMessage.repository;

import com.ms.sendMessage.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByClienteId(Long clienteId);
}
