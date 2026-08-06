package com.restaurante.pagamentofake.dto;

public record PagamentoRequest(
        Double valor,
        String formaPagamento
) {
}
