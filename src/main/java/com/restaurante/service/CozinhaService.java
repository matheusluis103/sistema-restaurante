package com.restaurante.service;

import com.restaurante.domain.entity.PedidoItem;
import com.restaurante.domain.enums.StatusItemPedido;
import com.restaurante.dto.CozinhaItemResponse;
import com.restaurante.repository.PedidoItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CozinhaService {
    private final PedidoItemRepository pedidoItemRepository;

    public CozinhaService(PedidoItemRepository pedidoItemRepository) {
        this.pedidoItemRepository = pedidoItemRepository;
    }

    public List<CozinhaItemResponse> listarItensPendentes() {
        return pedidoItemRepository.findByStatusOrderByIdAsc(StatusItemPedido.PENDENTE).stream()
                .map(CozinhaItemResponse::fromEntity)
                .toList();
    }

    public List<CozinhaItemResponse> listarItensEmPreparo() {
        return pedidoItemRepository.findByStatusOrderByIdAsc(StatusItemPedido.EM_PREPARO).stream()
                .map(CozinhaItemResponse::fromEntity)
                .toList();
    }

    public CozinhaItemResponse iniciarPreparo(Long itemId) {
        PedidoItem item = buscarItemPorId(itemId);
        if (item.getStatus() != StatusItemPedido.PENDENTE) {
            throw new com.restaurante.exception.RegraNegocioException("Somente itens pendentes podem iniciar preparo.");
        }
        item.setStatus(StatusItemPedido.EM_PREPARO);
        return CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    public CozinhaItemResponse marcarComoPronto(Long itemId) {
        PedidoItem item = buscarItemPorId(itemId);
        if (item.getStatus() != StatusItemPedido.EM_PREPARO) {
            throw new com.restaurante.exception.RegraNegocioException("Somente itens em preparo podem ser marcados como pronto.");
        }
        item.setStatus(StatusItemPedido.PRONTO);
        return CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    public CozinhaItemResponse entregarItem(Long itemId) {
        PedidoItem item = buscarItemPorId(itemId);
        if (item.getStatus() != StatusItemPedido.PRONTO) {
            throw new com.restaurante.exception.RegraNegocioException("Somente itens prontos podem ser entregues.");
        }
        item.setStatus(StatusItemPedido.ENTREGUE);
        return CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    private PedidoItem buscarItemPorId(Long itemId) {
        return pedidoItemRepository.findById(itemId)
                .orElseThrow(() -> new com.restaurante.exception.RegraNegocioException("Item não encontrado."));
    }
}
