package com.ms.sendMessage.service;

import com.ms.sendMessage.model.Cliente;
import com.ms.sendMessage.model.Conta;
import com.ms.sendMessage.model.Mensagem;
import com.ms.sendMessage.model.dto.MensagemDTO;
import com.ms.sendMessage.model.dto.RespostaDTO;
import com.ms.sendMessage.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ms.sendMessage.config.Constantes.VALOR_MENSAGEM;
import static com.ms.sendMessage.config.Constantes.ZERAR_VALOR;

@Service
public class ContaService {
    private final ContaRepository contaRepository;
    private final ClienteService clienteService;
    private final MensagemService mensagemService;

    @Autowired
    public ContaService(ContaRepository contaRepository, ClienteService clienteService, MensagemService mensagemService) {
        this.contaRepository = contaRepository;
        this.clienteService = clienteService;
        this.mensagemService = mensagemService;
    }

    public Optional<Conta> findById(Long id) {
        return contaRepository.findById(id);
    }

    public Conta save(Conta conta) {
        return contaRepository.save(conta);
    }

    public RespostaDTO inserirCredito(Long idCliente,Double credito){
        Optional<Cliente> cliente = clienteService.findById(idCliente);
        if (cliente.isEmpty()) {
            return (new RespostaDTO(false, "Cliente não localizado!"));
        }
        if (!cliente.get().getConta().isPrePago()) {
            return (new RespostaDTO(false, "Não é possível inserir créditos por que a conta deste cliente é pós-paga!"));
        }
        try {
            Conta conta = cliente.get().getConta();
            conta.setCredito(conta.getCredito() + credito);
            this.save(conta);
            return (new RespostaDTO(true, "Novo valor total de créditos é de R$: " + conta.getCredito()));
        }catch (Exception e){
            return (new RespostaDTO(false, "Erro ao alterar o limite!"));
        }
    }

    public void debitarCredito(Conta conta,Double valor){
        conta.setCredito(conta.getCredito() - valor);
        this.save(conta);
    }

    public RespostaDTO alterarLimite(Long idCliente,Double limite) {
        Optional<Cliente> cliente = clienteService.findById(idCliente);
        if (cliente.isEmpty()) {
            return (new RespostaDTO(false, "Cliente não localizado!"));
        }
        if (cliente.get().getConta().isPrePago()) {
            return (new RespostaDTO(false, "Não é possível alterar o limite por que a conta deste cliente é pré paga!"));
        }
        try {
            Conta conta = cliente.get().getConta();
            conta.setLimite((limite));
            this.save(conta);
            return (new RespostaDTO(true, "Novo limite do cliente é de R$: " + limite));
        }catch (Exception e){
            return (new RespostaDTO(false, "Erro ao alterar o limite!"));
        }
    }

    public RespostaDTO alterarPlano(Long idCliente){
        Optional<Cliente> cliente = clienteService.findById(idCliente);
        if (cliente.isEmpty()) {
            return (new RespostaDTO(false, "Cliente não localizado!"));
        }
        try {
            Conta conta = cliente.get().getConta();
            if(conta.isPrePago()){
                conta.setPrePago(false);
                conta.setCredito(ZERAR_VALOR);
                this.save(conta);
                return (new RespostaDTO(true, "Conta alterada de Pré-Paga para Pós-Paga"));
            }else{
                conta.setPrePago(true);
                conta.setLimite(ZERAR_VALOR);
                this.save(conta);
                return (new RespostaDTO(true, "Conta alterada de Pós-Paga para Pré-Paga"));
            }
        }catch (Exception e){
            return (new RespostaDTO(false, "Erro ao alterar o tipo de conta!"));
        }
    }

    public RespostaDTO consultarSaldo(Long idCliente){
        Optional<Cliente> cliente = clienteService.findById(idCliente);
        if (cliente.isEmpty()) {
            return (new RespostaDTO(false, "Cliente não localizado!"));
        }
        try {
            return (new RespostaDTO(true, "Saldo é de R$ "+this.getSaldo(cliente)));
        }catch (Exception e){
            return (new RespostaDTO(false, "Erro ao consultar o saldo!"));
        }
    }

    public RespostaDTO enviarMensagem(MensagemDTO mensagemDTO){
        Optional<Cliente> cliente = clienteService.findById(mensagemDTO.idCliente());
        if (cliente.isEmpty()) {
            return (new RespostaDTO(false, "Cliente não localizado!"));
        }

        try {
            if(cliente.get().getConta().isPrePago() && cliente.get().getConta().getCredito() < VALOR_MENSAGEM){
                return (new RespostaDTO(false, "Créditos insuficientes para envio de mensagens!"));
            }
            if(!cliente.get().getConta().isPrePago() && this.getSaldo(cliente) < VALOR_MENSAGEM){
                return (new RespostaDTO(false, "Limite atingido, não é possível realizar o envio da mensagem!"));
            }
            processarEnvio(cliente,mensagemDTO);
            return (new RespostaDTO(true, "Mensagem enviada com sucesso!"));
        }catch (Exception e){
            return (new RespostaDTO(false, "Erro ao consultar o saldo!"));
        }
    }

    private void processarEnvio(Optional<Cliente> cliente,MensagemDTO mensagemDTO){
        salvarMensagem(cliente,mensagemDTO);
        if(cliente.get().getConta().isPrePago()){
            debitarCredito(cliente.get().getConta(),VALOR_MENSAGEM);
        }
        //TODO implementar a chamada do serviço de envio de mensagem (Ex: Twilio)
    }

    private void salvarMensagem(Optional<Cliente> cliente,MensagemDTO mensagemDTO){
        Mensagem mensagem = new Mensagem();
        mensagem.setConta(cliente.get().getConta());
        mensagem.setNumeroTelefone(mensagemDTO.telefone());
        mensagem.setWhatsApp(mensagemDTO.isWhatsApp());
        mensagem.setTexto(mensagemDTO.texto());
        mensagemService.save(mensagem);
    }
    private Double getSaldo(Optional<Cliente> cliente) {
        try {
            List<Mensagem> mensagemList = cliente.get().getConta().getMensagens();
            return (cliente.get().getConta().getLimite() - (mensagemList.size() * VALOR_MENSAGEM));
        }catch (Exception e){
            return ZERAR_VALOR;
        }
    }

    public Optional<Conta> buscarContasPorClienteId(Long clienteId) {
        return contaRepository.findByClienteId(clienteId);
    }
}
