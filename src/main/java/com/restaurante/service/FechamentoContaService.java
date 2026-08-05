package com.restaurante.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.domain.entity.FechamentoConta;
import com.restaurante.domain.entity.Pedido;
import com.restaurante.domain.entity.PedidoItem;
import com.restaurante.domain.enums.StatusItemPedido;
import com.restaurante.domain.enums.StatusPedido;
import com.restaurante.dto.FechamentoContaRequest;
import com.restaurante.dto.FechamentoContaResponse;
import com.restaurante.exception.RegraNegocioException;
import com.restaurante.repository.FechamentoContaRepository;
import com.restaurante.repository.PedidoItemRepository;
import com.restaurante.repository.PedidoRepository;

@Service
public class FechamentoContaService {

    
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final FechamentoContaRepository fechamentoRepository;
    

    public FechamentoContaService(PedidoRepository pedidoRepository,
                                  PedidoItemRepository pedidoItemRepository,
                                  FechamentoContaRepository fechamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.fechamentoRepository = fechamentoRepository;
    }

    @Transactional
    public FechamentoContaResponse fecharConta(Long pedidoId, FechamentoContaRequest request) {
        Pedido pedido = buscarPedidoPorId(pedidoId);

        if (pedido.getStatus() == StatusPedido.FECHADO) {
            throw new RegraNegocioException("Pedido já está fechado.");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido cancelado não pode ser fechado.");
        }

            if (fechamentoRepository.existsByPedidoId(pedidoId)) {
                throw new RegraNegocioException("Já existe fechamento para este pedido.");
        }

        List<PedidoItem> itens = pedidoItemRepository.findByPedidoId(pedidoId);
        if (itens.isEmpty()) {
            throw new RegraNegocioException("Não é possível fechar uma conta sem itens.");
        }

        List<PedidoItem> itensNaoEntregues = pedidoItemRepository.findByPedidoIdAndStatusNot(pedidoId, StatusItemPedido.ENTREGUE);
        if (!itensNaoEntregues.isEmpty()) {
            throw new RegraNegocioException("Todos os itens precisam estar entregues para fechar.");
        }

        BigDecimal subtotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxaServico = request.taxaServico() != null ? request.taxaServico() : BigDecimal.ZERO;
        BigDecimal desconto = request.desconto() != null ? request.desconto() : BigDecimal.ZERO;

        if (taxaServico.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Taxa de Serviço não pode ser negativa.");
        }

        BigDecimal total = subtotal.add(taxaServico).subtract(desconto);
        if (desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Desconto não pode ser negativa.");
        }

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Total da conta não pode ser negativo.");
        }

        FechamentoConta fechamento = new FechamentoConta();
        fechamento.setPedido(pedido);
        fechamento.setSubtotal(subtotal);
        fechamento.setTaxaServico(taxaServico);
        fechamento.setDesconto(desconto);
        fechamento.setTotal(total);

        FechamentoConta salvo = fechamentoRepository.save(fechamento);

        pedido.setStatus(StatusPedido.FECHADO);
        pedido.setDataFechamento(LocalDateTime.now());
        pedidoRepository.save(pedido);
        return FechamentoContaResponse.fromEntity(salvo);
    }

    public FechamentoContaResponse buscarPorPedido(Long pedidoId) {
        FechamentoConta fechamento = fechamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Fechamento não encontrado."));
        return FechamentoContaResponse.fromEntity(fechamento);
    }

    private Pedido buscarPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));
    }
}
