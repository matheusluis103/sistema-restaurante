package com.restaurante.controller;

import com.restaurante.dto.FechamentoContaRequest;
import com.restaurante.dto.FechamentoContaResponse;
import com.restaurante.service.FechamentoContaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos/{pedidoId}/fechamento")
public class FechamentoContaController {

    private final FechamentoContaService fechamentoContaService;

    public FechamentoContaController(FechamentoContaService fechamentoContaService) {
        this.fechamentoContaService = fechamentoContaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FechamentoContaResponse fecharConta(@PathVariable Long pedidoId,
                                               @RequestBody FechamentoContaRequest request) {
        return fechamentoContaService.fecharConta(pedidoId, request);
    }

    @GetMapping
    public FechamentoContaResponse buscarPorPedido(@PathVariable Long pedidoId) {
        return fechamentoContaService.buscarPorPedido(pedidoId);
    }
}
