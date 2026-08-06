package com.restaurante.dto;

public record PagamentoRequest(
        Double valor,
        String formaPagamento
) {
}
