package com.restaurante.dto;

import java.math.BigDecimal;

import com.restaurante.domain.entity.PedidoItem;
import com.restaurante.domain.enums.StatusItemPedido;

public record PedidoItemResponse(
        Long id,
        Long pedidoId,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal total,
        String observacao,
        StatusItemPedido status
) {

    public static PedidoItemResponse fromEntity(PedidoItem item) {
        BigDecimal total = item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new PedidoItemResponse(
                item.getId(),
                item.getPedido().getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                total,
                item.getObservacao(),
                item.getStatus()
        );
    }
}
