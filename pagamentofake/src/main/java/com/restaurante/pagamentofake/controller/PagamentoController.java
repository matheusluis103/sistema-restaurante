package com.restaurante.pagamentofake.controller;

import com.restaurante.pagamentofake.dto.PagamentoRequest;
import com.restaurante.pagamentofake.dto.PagamentoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @PostMapping("/processar")
    public PagamentoResponse processar(@RequestBody PagamentoRequest pagamentoRequest) {
        return new PagamentoResponse(
                "APROVADO",
                UUID.randomUUID().toString()
        );
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
