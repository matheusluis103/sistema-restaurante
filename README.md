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

## Endpoints principais

### Pedido

- `POST /pedidos` - abre um pedido
- `GET /pedidos` - lista pedidos paginados
- `GET /pedidos/{id}` - busca pedido por id
- `POST /pedidos/{pedidoId}/itens` - adiciona item ao pedido
- `GET /pedidos/{pedidoId}/itens` - lista itens de um pedido

### Cozinha

- `GET /cozinha/pendentes` - lista itens pendentes
- `GET /cozinha/em-preparo` - lista itens em preparo
- `PATCH /cozinha/{itemId}/iniciar` - marca item como em preparo
- `PATCH /cozinha/{itemId}/pronto` - marca item como pronto
- `PATCH /cozinha/{itemId}/entregar` - marca item como entregue

## Testando requests

O projeto já contém arquivos de requisição em `requests/`:

- `requests/pedido.http`
- `requests/cozinha.http`

Basta abrir esses arquivos no VS Code e executar as requisições com a extensão HTTP client.

### Exemplos

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

Listar itens da cozinha:

```http
GET http://localhost:8080/cozinha/pendentes
```

Marcar item como em preparo:

```http
PATCH http://localhost:8080/cozinha/1/iniciar
```

## Swagger / OpenAPI

Se o projeto estiver rodando com sucesso, o Swagger UI normalmente fica disponível em:

- `http://localhost:8080/swagger-ui.html`
- ou `http://localhost:8080/swagger-ui/index.html`

## Observações

- Este projeto é baseado na playlist indicada e segue a mesma arquitetura de controllers, services, repositories, DTOs e entidades.
- Como o banco de dados é H2 em memória, todos os dados são perdidos ao parar a aplicação.
- Caso queira persistir dados entre reinícios, é necessário trocar a URL do datasource para um banco externo ou arquivo H2.
