package com.restaurante.repository;

import com.restaurante.domain.entity.PedidoItem;
import com.restaurante.domain.enums.StatusItemPedido;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    @Query(
        """
        SELECT i
        FROM PedidoItem i
        JOIN FETCH i.produto
        JOIN FETCH i.pedido p
        JOIN FETCH p.mesa
        WHERE i.status = :status
        ORDER BY i.id
        """
    )
    List<PedidoItem> buscarItensComProdutoEPedido(@Param("status") StatusItemPedido status);

    List<PedidoItem> findByPedidoId(Long pedidoId);

    List<PedidoItem> findByStatusOrderByIdAsc(StatusItemPedido status);

    List<PedidoItem> findByPedidoIdAndStatusNot(Long pedidoId, StatusItemPedido status);

}
