package com.restaurante.repository;

import com.restaurante.domain.entity.PedidoItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    List<PedidoItem> findByPedidoId(Long pedidoId);

    List<PedidoItem> findByStatusOrderByIdAsc(com.restaurante.domain.enums.StatusItemPedido status);

}
