package com.ms.sendMessage.controller;

import com.ms.sendMessage.model.Cliente;
import com.ms.sendMessage.model.Conta;
import com.ms.sendMessage.model.dto.MensagemDTO;
import com.ms.sendMessage.model.dto.RespostaDTO;
import com.ms.sendMessage.service.ClienteService;
import com.ms.sendMessage.service.ContaService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bcb")
public class BcbController {

    @Autowired
    private ClienteService service;

    @Autowired
    private ContaService contaService;

    @PostMapping("/cadastrar-conta")
    public ResponseEntity<Conta> salvarConta(@RequestBody Conta conta) {
        contaService.save(conta);
        return new ResponseEntity<>(conta, HttpStatus.CREATED);
    }

    @PostMapping("/cadastrar-cliente")
    public ResponseEntity<Cliente> salvarCadastro(@RequestBody Cliente cliente) {
        service.save(cliente);
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);
    }

    @GetMapping("/listar-pagina")
    public Page<Cliente> listarTodos(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.findAll(pageable);
    }

    @GetMapping("/consultar-cliente/{id}")
    public ResponseEntity<Optional<Cliente>> obterPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = service.findById(id);
        if (cliente.isPresent()) {
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/listar-clientes")
    public ResponseEntity<List<Cliente>> listarTodos() {
        List<Cliente> lista = service.findAll();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @PutMapping("/atualizar-cliente/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        Optional<Cliente> existente = service.findById(id);
        if (existente.isPresent()) {
            BeanUtils.copyProperties(cliente,existente,"id");
            service.save(cliente);
            return new ResponseEntity<>(cliente, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/excluir-cliente/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Optional<Cliente> cliente = service.findById(id);
        if (cliente.isPresent()) {
            service.deleteById(cliente.get().getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/enviar-mensagem")
    public ResponseEntity<String> enviarMensagem(@RequestBody MensagemDTO mensagemDTO) {
        RespostaDTO respostaDTO = contaService.enviarMensagem(mensagemDTO);
        if(respostaDTO.ok()){
            return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.OK);
        }
        return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/inserir-credito/{idCliente}/{credito}")
    public ResponseEntity<String> inserirCredito(@PathVariable Long idCliente, @PathVariable Double credito) {
        RespostaDTO respostaDTO = contaService.inserirCredito(idCliente, credito);
        if(respostaDTO.ok()){
            return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.OK);
        }
        return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/alterar-limite/{idCliente}/{limite}")
    public ResponseEntity<String> alterarLimite(@PathVariable Long idCliente, @PathVariable Double limite) {
        RespostaDTO respostaDTO = contaService.alterarLimite(idCliente, limite);
        if(respostaDTO.ok()){
            return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.OK);
        }
        return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/alterar-plano/{idCliente}")
    public ResponseEntity<String> alterarPlano(@PathVariable Long idCliente) {
        RespostaDTO respostaDTO = contaService.alterarPlano(idCliente);
        if(respostaDTO.ok()){
            return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.OK);
        }
        return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/consultar-saldo")
    public ResponseEntity<String> consultarSaldo(@PathVariable Long idCliente) {
        RespostaDTO respostaDTO = contaService.consultarSaldo(idCliente);
        if(respostaDTO.ok()){
            return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.OK);
        }
        return new ResponseEntity<>(respostaDTO.mensagem(), HttpStatus.BAD_REQUEST);
    }
}
