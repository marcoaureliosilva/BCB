package com.ms.sendMessage.repository;

import com.ms.sendMessage.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

}
