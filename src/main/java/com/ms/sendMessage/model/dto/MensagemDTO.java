package com.ms.sendMessage.model.dto;

public record MensagemDTO(Long idCliente,String telefone,boolean isWhatsApp,String texto) {
}
