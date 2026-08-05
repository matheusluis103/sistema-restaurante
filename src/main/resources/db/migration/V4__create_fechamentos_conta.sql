CREATE TABLE fechamentos_conta (
    id BIGSERIAL PRIMARY KEY,
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    taxa_servico NUMERIC(10,2) NOT NULL CHECK (taxa_servico >= 0),
    desconto NUMERIC(10,2) NOT NULL CHECK (desconto >= 0),
    total NUMERIC(10,2) NOT NULL CHECK (total >= 0),
    data_fechamento DATE NOT NULL,
    pedido_id BIGINT NOT NULL UNIQUE REFERENCES pedidos(id)
);

CREATE INDEX idx_fechamentos_conta_pedido ON fechamentos_conta(pedido_id);
