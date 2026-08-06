package com.restaurante.dto;

public record PagamentoResponse(
        String status,
        String codigoTransacao
) {
}
