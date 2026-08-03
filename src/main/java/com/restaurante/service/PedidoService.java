package com.restaurante.service;

import com.restaurante.domain.entity.Mesa;
import com.restaurante.domain.entity.Pedido;
import com.restaurante.domain.entity.PedidoItem;
import com.restaurante.domain.entity.Produto;
import com.restaurante.domain.enums.StatusItemPedido;
import com.restaurante.domain.enums.StatusMesa;
import com.restaurante.domain.enums.StatusPedido;
import com.restaurante.dto.PedidoItemResponse;
import com.restaurante.dto.PedidoRequest;
import com.restaurante.dto.PedidoResponse;
import com.restaurante.dto.PedidoItemRequest;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoItemRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.repository.ProdutoRepository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository,
            ProdutoRepository produtoRepository, PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    public PedidoResponse abrirPedido(PedidoRequest pedidoRequest) {
        Mesa mesa = mesaRepository.findById(pedidoRequest.mesaId())
                .orElseThrow(() -> new com.restaurante.exception.RegraNegocioException("Mesa inexistente"));

        if (mesa.getStatus() != StatusMesa.LIVRE) {
            throw new com.restaurante.exception.RegraNegocioException("Mesa não está livre para abertura de pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setObservacao(pedidoRequest.observacao());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        mesa.setStatus(StatusMesa.OCUPADA);
        mesaRepository.save(mesa);

        return PedidoResponse.fromEntity(pedidoSalvo);
    }

    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoRepository.findAll(pageable)
                .map(PedidoResponse::fromEntity);
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.exception.RegraNegocioException("Pedido não encontrado"));
        return PedidoResponse.fromEntity(pedido);
    }

    public PedidoItemResponse adicionarItem(Long pedidoId, PedidoItemRequest itemRequest) {

        Pedido pedido = buscarPedidoPorId(pedidoId);
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new com.restaurante.exception.RegraNegocioException(
                    "Não é possível adicionar itens a um pedido que não está aberto.");
        }

        Produto produto = produtoRepository.findById(itemRequest.produtoId())
                .orElseThrow(() -> new com.restaurante.exception.RegraNegocioException("Produto não encontrado"));

        if (!produto.getDisponivel()) {
            throw new com.restaurante.exception.RegraNegocioException("Produto indisponível no cardápio.");
        }

        if (itemRequest.quantidade() == null || itemRequest.quantidade() <= 0) {
            throw new com.restaurante.exception.RegraNegocioException("A quantidade deve ser maior que zero.");
        }

        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setProduto(produto);
        pedidoItem.setQuantidade(itemRequest.quantidade());
        pedidoItem.setPrecoUnitario(produto.getPreco());
        pedidoItem.setObservacao(itemRequest.observacao());
        pedidoItem.setStatus(StatusItemPedido.PENDENTE);

        PedidoItem pedidoItemSalvo = pedidoItemRepository.save(pedidoItem);

        return PedidoItemResponse.fromEntity(pedidoItemSalvo);
    }

    public List<PedidoItemResponse> listarItens(Long pedidoId) {
        buscarPedidoPorId(pedidoId);
        return pedidoItemRepository.findByPedidoId(pedidoId).stream()
                .map(PedidoItemResponse::fromEntity)
                .toList();
    }

    private Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.exception.RegraNegocioException("Pedido não encontrado"));
    }
}
