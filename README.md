# Sistema Restaurante

Este projeto é uma implementação de um sistema de restaurante em Java usando Spring Boot, seguindo o projeto da playlist:

https://www.youtube.com/playlist?list=PLCUSYmPGwekepAli6UoI4dyxZ0_AtPK7f

## Sobre o projeto

- `Spring Boot 3.3.1`
- `Spring Web`
- `Spring Data JPA`
- `Flyway` para migrações de banco de dados
- `H2` em memória em runtime
- `Spring Validation`
- `SpringDoc OpenAPI` para documentação Swagger UI
- `Lombok` (opcional)
- Estrutura de pacotes:
  - `controller` - RestControllers
  - `service` - serviços de domínio
  - `repository` - repositórios Spring Data
  - `dto` - objetos de transferência de dados
  - `domain` - entidades, enums e regras de negócio
  - `exception` - tratamento de exceções personalizadas

## Configuração

O projeto usa H2 em memória configurado em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:restaurantedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

As migrações do banco ficam em `src/main/resources/db/migration`.

## Como rodar

1. Abra um terminal na pasta do projeto:

```powershell
cd c:\desenvolvimento\sistema-restaurante
```

2. Execute com Maven:

```powershell
mvn spring-boot:run
```

Se ocorrer erro de resolução de dependências, verifique sua conexão com a internet e o acesso ao repositório Maven central.

## Módulo de pagamento fake

O projeto inclui um módulo separado chamado `pagamentofake` que simula o serviço de pagamento com um endpoint REST local em `http://localhost:9090`.

- O serviço principal usa `Spring Cloud OpenFeign` para chamar o endpoint fake de pagamento.
- A URL do serviço fake está configurada em `src/main/resources/application.properties` como `pagamento.api.url=http://localhost:9090`.
- O fake expõe:
  - `POST /pagamentos/processar` para processar o pagamento
  - `GET /pagamentos/health` para checar se o serviço está no ar

### Como executar em conjunto

1. Abra um terminal na pasta do módulo fake:

```powershell
cd c:\desenvolvimento\sistema-restaurante\pagamentofake
mvn spring-boot:run
```

2. Abra outro terminal na raiz do projeto principal:

```powershell
cd c:\desenvolvimento\sistema-restaurante
mvn spring-boot:run
```

3. Use os requests em `requests/` para testar o fluxo completo.

## Como funciona o código de pagamento

- `src/main/java/com/restaurante/controller/PedidoController.java` possui o endpoint `POST /pedidos/{pedidoId}/pagar`.
- Esse endpoint chama `PagamentoService.pagar(pedidoId, formaPagamento)`.
- `PagamentoService` busca o fechamento do pedido e envia uma requisição para o fake payment service via `PagamentoClient`.
- `PagamentoClient` é um Feign client configurado com `@FeignClient(name = "pagamento-client", url = "${pagamento.api.url}")`.
- O fake service em `pagamentofake/src/main/java/com/restaurante/pagamentofake/controller/PagamentoController.java` sempre retorna um pagamento `APROVADO` com um `codigoTransacao` gerado.
- Quando o pagamento é aprovado, o sistema principal:
  - marca o pedido como `FECHADO`
  - libera a mesa como `LIVRE`
  - persiste um registro de `Pagamento`

## Testando o pagamento fake

1. Inicie o fake em `http://localhost:9090` e verifique `GET http://localhost:9090/pagamentos/health`.
2. Inicie a aplicação principal em `http://localhost:8080`.
3. Crie pedidos, adicione itens e avance o fluxo da cozinha normalmente.
4. Após fechar a conta do pedido, execute `POST /pedidos/{pedidoId}/pagar?formaPagamento=PIX` no serviço principal.
5. O fake retornará `"status": "APROVADO"` e o pedido será fechado.

> Observação: não é necessário chamar diretamente `POST /pagamentos/processar` quando estiver usando o fluxo principal. O endpoint `POST /pedidos/{pedidoId}/pagar` já dispara a chamada ao serviço fake.

## Endpoints principais

### Pedido

- `POST /pedidos` - abre um pedido
- `GET /pedidos` - lista pedidos paginados
- `GET /pedidos/{id}` - busca pedido por id
- `POST /pedidos/{pedidoId}/itens` - adiciona item ao pedido
- `GET /pedidos/{pedidoId}/itens` - lista itens de um pedido
- `POST /pedidos/{pedidoId}/fechamento` - fecha a conta do pedido
- `GET /pedidos/{pedidoId}/fechamento` - busca o fechamento da conta

### Cozinha

- `GET /cozinha/itens-pendentes` - lista itens pendentes
- `GET /cozinha/itens-em-preparo` - lista itens em preparo
- `PATCH /cozinha/itens/{itemId}/iniciar-preparo` - marca item como em preparo
- `PATCH /cozinha/itens/{itemId}/marcar-pronto` - marca item como pronto
- `PATCH /cozinha/itens/{itemId}/entregar` - marca item como entregue

### Produto

- `POST /api/produtos` - cadastra produto
- `GET /api/produtos` - lista produtos paginados
- `GET /api/produtos/{id}` - busca produto por id
- `PUT /api/produtos/{id}` - atualiza produto
- `DELETE /api/produtos/{id}` - exclui produto

## Testando requests

O projeto já contém arquivos de requisição em `requests/`:

- `requests/pedido.http`
- `requests/cozinha.http`
- `requests/produto.http`
- `requests/fechamento.http`
- `requests/pagamento.http`

Basta abrir esses arquivos no VS Code e executar as requisições com a extensão HTTP client.

### Sequência de uso (passo a passo)

Siga esta ordem para testar o fluxo completo com os arquivos de requests:

1. Start the application:

```powershell
cd c:\desenvolvimento\sistema-restaurante
mvn spring-boot:run
```

2. Criar produtos (use `requests/produto.http`):

- Executar o `POST /api/produtos` para cadastrar itens no cardápio.
- Use `GET /api/produtos` para confirmar.

3. Abrir pedido (use `requests/pedido.http`):

- `POST /pedidos` com `mesaId` existente para abrir um pedido.
- `GET /pedidos` e `GET /pedidos/{id}` para verificar.

4. Adicionar itens ao pedido (use `requests/pedido.http`):

- `POST /pedidos/{pedidoId}/itens` para incluir produtos no pedido.
- `GET /pedidos/{pedidoId}/itens` para listar os itens.

5. Fluxo da cozinha (use `requests/cozinha.http`):

- `GET /cozinha/itens-pendentes` para ver itens a preparar.
- `PATCH /cozinha/itens/{itemId}/iniciar-preparo` para marcar como `EM_PREPARO`.
- `PATCH /cozinha/itens/{itemId}/marcar-pronto` para marcar `PRONTO`.
- `PATCH /cozinha/itens/{itemId}/entregar` para marcar `ENTREGUE`.

6. Fechamento da conta (use `requests/fechamento.http`):

- Somente após todos os itens do pedido estarem no status `ENTREGUE`.
- `POST /pedidos/{pedidoId}/fechamento` para criar o fechamento.
- `GET /pedidos/{pedidoId}/fechamento` para recuperar os dados.

### Observações sobre erros comuns

- `400 Bad Request` ao criar pedido: verifique se a `mesa` está livre.
- `400 Bad Request` ao fechar: todos os itens devem estar `ENTREGUE`.
- Use os arquivos em `requests/` para avançar o estado dos itens quando necessário.

### Exemplos rápidos

Abrir pedido:

```http
POST http://localhost:8080/pedidos
Content-Type: application/json

{
  "mesaId": 1,
  "observacao": "Cliente pediu atendimento rapido"
}
```

Adicionar item ao pedido:

```http
POST http://localhost:8080/pedidos/1/itens
Content-Type: application/json

{
  "produtoId": 1,
  "quantidade": 2,
  "observacao": "Sem cebola"
}
```

Fechar pedido (após entrega dos itens):

```http
POST http://localhost:8080/pedidos/1/fechamento
Content-Type: application/json

{
  "taxaServico": 10.00,
  "desconto": 5.00
}
```
## Swagger / OpenAPI

Se o projeto estiver rodando com sucesso, o Swagger UI normalmente fica disponível em:

- `http://localhost:8080/swagger-ui.html`
- ou `http://localhost:8080/swagger-ui/index.html`

## Observações

- Este projeto é baseado na playlist indicada e segue a mesma arquitetura de controllers, services, repositories, DTOs e entidades.
- Como o banco de dados é H2 em memória, todos os dados são perdidos ao parar a aplicação.
- Caso queira persistir dados entre reinícios, é necessário trocar a URL do datasource para um banco externo ou arquivo H2.
