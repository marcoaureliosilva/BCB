package com.ms.sendMessage.service;

import com.ms.sendMessage.model.Mensagem;
import com.ms.sendMessage.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MensagemService {
    @Autowired
    private final MensagemRepository mensagemRepository;

    public MensagemService(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    public Optional<Mensagem> findById(Long id) {
        return mensagemRepository.findById(id);
    }

    public Mensagem save(Mensagem mensagem) {
        return mensagemRepository.save(mensagem);
    }

    public void deleteById(Long id) {
        mensagemRepository.deleteById(id);
    }
}
