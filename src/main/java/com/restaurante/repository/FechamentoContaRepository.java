package com.restaurante.repository;

import com.restaurante.domain.entity.FechamentoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FechamentoContaRepository extends JpaRepository<FechamentoConta, Long> {
}
