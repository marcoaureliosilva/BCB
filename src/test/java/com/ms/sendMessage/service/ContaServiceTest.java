package com.ms.sendMessage.service;

import com.ms.sendMessage.model.Cliente;
import com.ms.sendMessage.model.Conta;
import com.ms.sendMessage.model.Mensagem;
import com.ms.sendMessage.model.dto.MensagemDTO;
import com.ms.sendMessage.model.dto.RespostaDTO;
import com.ms.sendMessage.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContaServiceTest {

    @InjectMocks
    private ContaService contaService;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private MensagemService mensagemService;

    private Cliente cliente;
    private Conta conta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        conta = new Conta();
        conta.setCredito(100.0);
        conta.setLimite(200.0);
        conta.setPrePago(true);
        conta.setMensagens(new ArrayList<>());
        cliente = new Cliente();
        cliente.setConta(conta);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        Optional<Conta> result = contaService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(contaRepository).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        Long id = 1L;
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Conta> result = contaService.findById(id);

        assertFalse(result.isPresent());
        verify(contaRepository).findById(id);
    }

    @Test
    void testSave() {
        when(contaRepository.save(conta)).thenReturn(conta);

        Conta result = contaService.save(conta);

        assertEquals(conta, result);
        verify(contaRepository).save(conta);
    }

    @Test
    void testInserirCreditoClienteNaoLocalizado() {
        Long idCliente = 1L;
        Double credito = 50.0;
        when(clienteService.findById(idCliente)).thenReturn(Optional.empty());

        RespostaDTO result = contaService.inserirCredito(idCliente, credito);

        assertFalse(result.ok());
        assertEquals("Cliente não localizado!", result.mensagem());
    }

    @Test
    void testInserirCreditoContaPosPaga() {
        Long idCliente = 1L;
        Double credito = 50.0;
        conta.setPrePago(false);
        cliente.setConta(conta);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));

        RespostaDTO result = contaService.inserirCredito(idCliente, credito);

        assertFalse(result.ok());
        assertEquals("Não é possível inserir créditos por que a conta deste cliente é pós-paga!", result.mensagem());
    }

    @Test
    void testInserirCreditoSucesso() {
        Long idCliente = 1L;
        Double credito = 50.0;
        conta.setPrePago(true);
        cliente.setConta(conta);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(contaRepository.save(conta)).thenReturn(conta);

        RespostaDTO result = contaService.inserirCredito(idCliente, credito);

        assertTrue(result.ok());
        assertEquals("Novo valor total de créditos é de R$: 150.0", result.mensagem());
        assertEquals(150.0, conta.getCredito());
    }

    @Test
    void testAlterarLimiteClienteNaoLocalizado() {
        Long idCliente = 1L;
        Double novoLimite = 300.0;
        when(clienteService.findById(idCliente)).thenReturn(Optional.empty());

        RespostaDTO result = contaService.alterarLimite(idCliente, novoLimite);

        assertFalse(result.ok());
        assertEquals("Cliente não localizado!", result.mensagem());
    }

    @Test
    void testAlterarLimiteContaPrePaga() {
        Long idCliente = 1L;
        Double novoLimite = 300.0;
        conta.setPrePago(true);
        cliente.setConta(conta);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));

        RespostaDTO result = contaService.alterarLimite(idCliente, novoLimite);

        assertFalse(result.ok());
        assertEquals("Não é possível alterar o limite por que a conta deste cliente é pré paga!", result.mensagem());
    }

    @Test
    void testAlterarLimiteSucesso() {
        Long idCliente = 1L;
        Double novoLimite = 300.0;
        conta.setPrePago(false);
        cliente.setConta(conta);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(contaRepository.save(conta)).thenReturn(conta);

        RespostaDTO result = contaService.alterarLimite(idCliente, novoLimite);

        assertTrue(result.ok());
        assertEquals("Novo limite do cliente é de R$: 300.0", result.mensagem());
        assertEquals(300.0, conta.getLimite());
    }

    @Test
    void testAlterarPlanoClienteNaoLocalizado() {
        Long idCliente = 1L;
        when(clienteService.findById(idCliente)).thenReturn(Optional.empty());

        RespostaDTO result = contaService.alterarPlano(idCliente);

        assertFalse(result.ok());
        assertEquals("Cliente não localizado!", result.mensagem());
    }

    @Test
    void testAlterarPlanoSucessoPreParaPos() {
        Long idCliente = 1L;
        cliente.setConta(conta);
        conta.setPrePago(true);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(contaRepository.save(conta)).thenReturn(conta);

        RespostaDTO result = contaService.alterarPlano(idCliente);

        assertTrue(result.ok());
        assertEquals("Conta alterada de Pré-Paga para Pós-Paga", result.mensagem());
        assertFalse(conta.isPrePago());
        assertEquals(0.0, conta.getCredito());
    }

    @Test
    void testAlterarPlanoSucessoPosParaPre() {
        Long idCliente = 1L;
        cliente.setConta(conta);
        conta.setPrePago(false);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(contaRepository.save(conta)).thenReturn(conta);

        RespostaDTO result = contaService.alterarPlano(idCliente);

        assertTrue(result.ok());
        assertEquals("Conta alterada de Pós-Paga para Pré-Paga", result.mensagem());
        assertTrue(conta.isPrePago());
        assertEquals(0.0, conta.getLimite());
    }

    @Test
    void testConsultarSaldoClienteNaoLocalizado() {
        Long idCliente = 1L;
        when(clienteService.findById(idCliente)).thenReturn(Optional.empty());

        RespostaDTO result = contaService.consultarSaldo(idCliente);

        assertFalse(result.ok());
        assertEquals("Cliente não localizado!", result.mensagem());
    }

    @Test
    void testConsultarSaldoComSaldo() {
        Long idCliente = 1L;
        conta.setPrePago(true);
        cliente.setConta(conta);
        when(clienteService.findById(idCliente)).thenReturn(Optional.of(cliente));

        RespostaDTO result = contaService.consultarSaldo(idCliente);

        assertTrue(result.ok());
        assertEquals("Saldo é de R$ 200.0", result.mensagem());
    }

    @Test
    void testEnviarMensagemClienteNaoLocalizado() {
        MensagemDTO mensagemDTO = new MensagemDTO(1L, "123456789", false, "Mensagem a ser enviada");
        when(clienteService.findById(mensagemDTO.idCliente())).thenReturn(Optional.empty());

        RespostaDTO result = contaService.enviarMensagem(mensagemDTO);

        assertFalse(result.ok());
        assertEquals("Cliente não localizado!", result.mensagem());
    }

    @Test
    void testEnviarMensagemCreditoInsuficiente() {
        MensagemDTO mensagemDTO = new MensagemDTO(1L, "123456789", false, "Mensagem a ser enviada");
        conta.setPrePago(true);
        conta.setCredito(0.0);
        cliente.setConta(conta);
        when(clienteService.findById(mensagemDTO.idCliente())).thenReturn(Optional.of(cliente));

        RespostaDTO result = contaService.enviarMensagem(mensagemDTO);

        assertFalse(result.ok());
        assertEquals("Créditos insuficientes para envio de mensagens!", result.mensagem());
    }

    @Test
    void testEnviarMensagemLimiteAtingido() {
        MensagemDTO mensagemDTO = new MensagemDTO(1L, "123456789", false, "Mensagem a ser enviada");
        conta.setPrePago(false);
        conta.setLimite(0.0);
        cliente.setConta(conta);
        when(clienteService.findById(mensagemDTO.idCliente())).thenReturn(Optional.of(cliente));

        RespostaDTO result = contaService.enviarMensagem(mensagemDTO);

        assertFalse(result.ok());
        assertEquals("Limite atingido, não é possível realizar o envio da mensagem!", result.mensagem());
    }

    @Test
    void testEnviarMensagemSucesso() {
        MensagemDTO mensagemDTO = new MensagemDTO(1L, "123456789", false, "Mensagem a ser enviada");
        conta.setPrePago(true);
        cliente.setConta(conta);

        when(clienteService.findById(mensagemDTO.idCliente())).thenReturn(Optional.of(cliente));
        when(contaRepository.save(conta)).thenReturn(conta);
        Mensagem mensagem = new Mensagem();
        when(mensagemService.save(any(Mensagem.class))).thenReturn(mensagem);

        RespostaDTO result = contaService.enviarMensagem(mensagemDTO);

        assertTrue(result.ok());
        assertEquals("Mensagem enviada com sucesso!", result.mensagem());
        assertEquals(99.75, conta.getCredito());
    }

    @Test
    void testBuscarContasPorClienteId() {
        Long clienteId = 1L;
        when(contaRepository.findByClienteId(clienteId)).thenReturn(Optional.of(conta));

        Optional<Conta> result = contaService.buscarContasPorClienteId(clienteId);

        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(contaRepository).findByClienteId(clienteId);
    }

    @Test
    void testBuscarContasPorClienteId_NaoEncontrada() {
        Long clienteId = 1L;
        when(contaRepository.findByClienteId(clienteId)).thenReturn(Optional.empty());

        Optional<Conta> result = contaService.buscarContasPorClienteId(clienteId);

        assertFalse(result.isPresent());
        verify(contaRepository).findByClienteId(clienteId);
    }
}