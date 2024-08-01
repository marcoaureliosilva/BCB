package com.ms.sendMessage.service;

import com.ms.sendMessage.model.Cliente;
import com.ms.sendMessage.repository.ClienteRepository;
import com.ms.sendMessage.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaRepository contaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());
        var result = clienteService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clienteRepository).findAll();
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.findById(id);
        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(clienteRepository).findById(id);
    }

    @Test
    void testSave() {
        Cliente cliente = new Cliente();
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente result = clienteService.save(cliente);
        assertEquals(cliente, result);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void testDeleteById() {
        clienteService.deleteById(1L);
        verify(clienteRepository).deleteById(1L);
    }
}
