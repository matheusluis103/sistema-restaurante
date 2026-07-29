package com.restaurante.service;

import com.restaurante.domain.entity.Mesa;
import com.restaurante.domain.entity.Pedido;
import com.restaurante.domain.enums.StatusMesa;
import com.restaurante.domain.enums.StatusPedido;
import com.restaurante.dto.PedidoRequest;
import com.restaurante.dto.PedidoResponse;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
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
