package com.ms.sendMessage.service;

import com.ms.sendMessage.model.Mensagem;
import com.ms.sendMessage.repository.MensagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MensagemServiceTest {

    @InjectMocks
    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    private Mensagem mensagem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mensagem = new Mensagem();
        mensagem.setId(1L);
        mensagem.setTexto("Mensagem de teste");
    }

    @Test
    void testFindById_Sucesso() {
        Long id = 1L;
        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagem));

        Optional<Mensagem> result = mensagemService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(mensagem, result.get());
        verify(mensagemRepository).findById(id);
    }

    @Test
    void testFindById_NaoEncontrada() {
        Long id = 1L;
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Mensagem> result = mensagemService.findById(id);

        assertFalse(result.isPresent());
        verify(mensagemRepository).findById(id);
    }

    @Test
    void testSave() {
        when(mensagemRepository.save(mensagem)).thenReturn(mensagem);

        Mensagem result = mensagemService.save(mensagem);

        assertEquals(mensagem, result);
        verify(mensagemRepository).save(mensagem);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        mensagemService.deleteById(id);

        verify(mensagemRepository).deleteById(id);
    }
}

