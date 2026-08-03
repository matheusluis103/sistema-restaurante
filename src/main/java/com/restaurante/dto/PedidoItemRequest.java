package com.restaurante.dto;

public record PedidoItemRequest(
        Long produtoId,
        Integer quantidade,
        String observacao
) {
}
