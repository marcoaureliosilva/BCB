package com.ms.sendMessage.repository;

import com.ms.sendMessage.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  ClienteRepository extends JpaRepository<Cliente, Long> {
}
