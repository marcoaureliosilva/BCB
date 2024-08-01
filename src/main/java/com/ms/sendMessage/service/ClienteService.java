package com.ms.sendMessage.service;

import com.ms.sendMessage.model.Cliente;
import com.ms.sendMessage.model.Conta;
import com.ms.sendMessage.repository.ClienteRepository;
import com.ms.sendMessage.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private final ClienteRepository clienteRepository;

    @Autowired
    private final ContaRepository contaRepository;

    public ClienteService(ClienteRepository clienteRepository, ContaRepository contaRepository) {
        this.clienteRepository = clienteRepository;
        this.contaRepository = contaRepository;
    }

    public List<Cliente> findAll(){
        return clienteRepository.findAll();
    }

    public Page<Cliente> findAll(Pageable pageable){
        return clienteRepository.findAll(pageable);
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        if(cliente.getConta()!=null){
            cliente.getConta().setCliente(cliente);
        }
        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }
}
