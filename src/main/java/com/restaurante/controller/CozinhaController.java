package com.restaurante.controller;

import com.restaurante.dto.CozinhaItemResponse;
import com.restaurante.service.CozinhaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cozinha")
public class CozinhaController {

    private final CozinhaService cozinhaService;

    public CozinhaController(CozinhaService cozinhaService) {
        this.cozinhaService = cozinhaService;
    }

    @GetMapping("/itens-pendentes")
    public List<CozinhaItemResponse> listarItensPendentes() {
        return cozinhaService.listarItensPendentes();
    }

    @GetMapping("/itens-em-preparo")
    public List<CozinhaItemResponse> listarItensEmPreparo() {
        return cozinhaService.listarItensEmPreparo();
    }

    @PatchMapping("/itens/{itemId}/iniciar-preparo")
    public CozinhaItemResponse iniciarPreparo(@PathVariable Long itemId) {
        return cozinhaService.iniciarPreparo(itemId);
    }

    @PatchMapping("/itens/{itemId}/marcar-pronto")
    public CozinhaItemResponse marcarComoPronto(@PathVariable Long itemId) {
        return cozinhaService.marcarComoPronto(itemId);
    }

    @PatchMapping("/itens/{itemId}/entregar")
    @ResponseStatus(HttpStatus.OK)
    public CozinhaItemResponse entregarItem(@PathVariable Long itemId) {
        return cozinhaService.entregarItem(itemId);
    }
}
