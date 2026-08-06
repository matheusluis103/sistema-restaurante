package com.restaurante.service;

import com.restaurante.client.PagamentoClient;
import com.restaurante.domain.entity.FechamentoConta;
import com.restaurante.domain.entity.Pagamento;
import com.restaurante.domain.enums.FormaPagamento;
import com.restaurante.domain.enums.StatusMesa;
import com.restaurante.domain.enums.StatusPagamento;
import com.restaurante.domain.enums.StatusPedido;
import com.restaurante.dto.PagamentoRequest;
import com.restaurante.dto.PagamentoResponse;
import com.restaurante.exception.RegraNegocioException;
import com.restaurante.repository.FechamentoContaRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PagamentoRepository;
import com.restaurante.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PagamentoClient pagamentoClient;
    private final FechamentoContaRepository fechamentoContaRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoClient pagamentoClient,
                            FechamentoContaRepository fechamentoContaRepository,
                            PedidoRepository pedidoRepository,
                            MesaRepository mesaRepository,
                            PagamentoRepository pagamentoRepository) {
        this.pagamentoClient = pagamentoClient;
        this.fechamentoContaRepository = fechamentoContaRepository;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public void pagar(Long pedidoId, String formaPagamento) {
        FechamentoConta fechamento = fechamentoContaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Conta não encontrada"));

        PagamentoResponse response = pagamentoClient.processar(
                new PagamentoRequest(
                        fechamento.getTotal().doubleValue(),
                        formaPagamento
                )
        );

        if ("APROVADO".equals(response.status())) {
            var pedido = fechamento.getPedido();
            pedido.setStatus(StatusPedido.FECHADO);

            var mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);

            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setValor(fechamento.getTotal());
            pagamento.setCodigoTransacaoExterna(response.codigoTransacao());
            pagamento.setDataPagamento(java.time.LocalDateTime.now());

            pagamentoRepository.save(pagamento);
            mesaRepository.save(mesa);
            pedidoRepository.save(pedido);
        }
    }
}
