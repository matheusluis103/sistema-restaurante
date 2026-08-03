package com.restaurante.controller;

import com.restaurante.dto.PedidoItemRequest;
import com.restaurante.dto.PedidoItemResponse;
import com.restaurante.dto.PedidoRequest;
import com.restaurante.dto.PedidoResponse;
import com.restaurante.service.PedidoService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse abrirPedido(@RequestBody PedidoRequest pedidoRequest) {
        return pedidoService.abrirPedido(pedidoRequest);
    }

    @GetMapping
    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping("/{pedidoId}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoItemResponse adicionarItem(@PathVariable Long pedidoId,
            @RequestBody PedidoItemRequest request) {
        return pedidoService.adicionarItem(pedidoId, request);
    }

    @GetMapping("/{pedidoId}/itens")
    public List<PedidoItemResponse> listarItens(@PathVariable Long pedidoId) {
        return pedidoService.listarItens(pedidoId);
    }
}
