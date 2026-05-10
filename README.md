# household-financial-control-back

Backend do sistema de controle financeiro doméstico, desenvolvido em **Clojure** com arquitetura em camadas bem definidas para separação de responsabilidades.

---

## Tecnologias

| Biblioteca | Propósito |
|---|---|
| [Compojure](https://github.com/weavejester/compojure) | Roteamento HTTP |
| [Ring](https://github.com/ring-clojure/ring) | Servidor HTTP (Jetty) e middlewares |
| [next.jdbc](https://github.com/seancorfield/next-jdbc) | Acesso ao banco de dados |
| [HoneySQL](https://github.com/seancorfield/honeysql) | Construção de queries SQL em Clojure |
| [Prismatic Schema](https://github.com/plumatic/schema) | Validação e tipagem de dados |
| [PostgreSQL](https://www.postgresql.org/) | Banco de dados relacional |
| [lein-cloverage](https://github.com/cloverage/cloverage) | Cobertura de testes |

---

## Arquitetura

O projeto segue uma arquitetura em camadas com fluxo unidirecional de dados:

```
HTTP Request
     │
     ▼
┌─────────────────┐
│   diplomatic/   │  ← Entrada HTTP (rotas, middlewares)
│   http_server   │
└────────┬────────┘
         │ valida payload com wire.in.*
         ▼
┌─────────────────┐
│    adapter/     │  ← Conversão entre wire e modelo interno
└────────┬────────┘
         │ modelo interno (model/*)
         ▼
┌─────────────────┐
│   controller/   │  ← Orquestração da regra de negócio
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  diplomatic/    │  ← Acesso ao banco de dados (SQL via HoneySQL)
│    db/          │
└────────┬────────┘
         │
         ▼
    PostgreSQL
         │
         ▼
┌─────────────────┐
│    adapter/     │  ← Conversão do modelo interno para wire.out.*
└────────┬────────┘
         │
         ▼
HTTP Response (wire.out.*)
```

---

## Estrutura de Pastas

``` 
src/
└── household_financial_control_back/
    ├── core.clj                    # Entrypoint da aplicação
    │
    ├── wire/
    │   ├── in/                     # Schemas de entrada (payload da requisição)
    │   │   ├── create_new_card.clj
    │   │   ├── create_new_category.clj
    │   │   ├── create_new_owner.clj
    │   │   └── create_new_payment.clj
    │   └── out/                    # Schemas de saída (payload da resposta)
    │       ├── return_all_cards.clj
    │       ├── return_all_categories.clj
    │       ├── return_all_owners.clj
    │       └── return_all_payments.clj
    │
    ├── model/                      # Modelos internos da aplicação
    │   ├── card.clj
    │   ├── category.clj
    │   ├── owner.clj
    │   └── payment.clj
    │
    ├── adapter/                    # Conversão entre wire ↔ modelo interno
    │   ├── card.clj
    │   ├── category.clj
    │   ├── owner.clj
    │   └── payment.clj
    │
    ├── controller/                 # Orquestração das regras de negócio
    │   ├── card.clj
    │   ├── category.clj
    │   ├── owner.clj
    │   └── payment.clj
    │
    ├── logic/                      # Lógica de negócio pura (sem efeitos colaterais)
    │
    └── diplomatic/
        ├── http_server.clj         # Rotas HTTP e middlewares
        └── db/
            ├── household_financial_db.clj  # Configuração da conexão
            ├── card.clj            # Queries SQL de cards
            ├── category.clj        # Queries SQL de categories
            ├── owner.clj           # Queries SQL de owners
            └── payment.clj         # Queries SQL de payments
```

---

## Responsabilidades por Camada

### `wire/in`
Define os **schemas de entrada** que validam o payload recebido pela API antes de qualquer processamento.

### `wire/out`
Define os **schemas de saída** que padronizam o formato da resposta HTTP enviada ao cliente.

### `model`
Representa os **modelos internos** da aplicação — estruturas de dados que trafegam entre controller, adapter e db.

### `adapter`
Responsável pelas **conversões de dados**:
- `wire.in → model` (entrada): transforma o payload externo no modelo interno
- `model → wire.out` (saída): transforma o modelo interno na resposta padronizada

### `controller`
**Orquestra** o fluxo de cada caso de uso: recebe o modelo interno, chama a camada de banco e retorna o resultado. Não contém lógica SQL nem conversão de formato.

### `logic`
Funções de **lógica de negócio pura**, sem efeitos colaterais (sem I/O, sem banco). Facilita testes unitários isolados.

### `diplomatic/http_server`
Define as **rotas HTTP** com Compojure, aplica middlewares (CORS, JSON, validação de schema) e coordena as chamadas entre adapter e controller.

### `diplomatic/db`
Contém as **queries SQL** construídas com HoneySQL e executadas via next.jdbc. Cada entidade tem seu próprio arquivo de acesso ao banco.

---

## Banco de Dados

O schema do banco está em `resources/script_db.sql`. Para inicializar:

```bash
psql -h localhost -p 5432 -U usuario -d household-financial \
  -f resources/script_db.sql
```

A configuração de conexão está em `src/.../diplomatic/db/household_financial_db.clj`.

---

## Rodando o projeto

```bash
lein run
```

A API sobe na porta **3000**.

---

## Testes

```bash
# Rodar todos os testes
lein test

# Verificar cobertura de testes
lein cloverage
```

O relatório de cobertura HTML é gerado em `target/coverage/index.html`.

---

## Endpoints disponíveis

### Cards

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/card` | Cadastra um novo cartão |
| `GET` | `/card` | Retorna todos os cartões |

### Categories

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/category` | Cadastra uma nova categoria |
| `GET` | `/category` | Retorna todas as categorias |

### Owners

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/owner` | Cadastra um novo owner |
| `GET` | `/owner` | Retorna todos os owners |

### Payments

| Metodo | Rota | Descricao |
|---|---|---|
| `POST` | `/payment` | Cadastra um novo pagamento |
| `GET` | `/payment` | Retorna todos os pagamentos |

#### POST `/card`
```json
// Request body
{ "name": "Nubank" }

// Response 201
{
  "mensagem": "Card created successfully",
  "card": { "id": 1, "name": "Nubank" }
}
```

#### GET `/card`
```json
// Response 200
{
  "cards": [
    { "id": 1, "name": "Nubank" },
    { "id": 2, "name": "Itau" }
  ]
}
```

#### POST `/category`
```json
// Request body
{ "name": "Alimentacao" }

// Response 201
{
  "mensagem": "Category created successfully",
  "category": { "id": 1, "name": "Alimentacao" }
}
```

#### GET `/category`
```json
// Response 200
{
  "categories": [
    { "id": 1, "name": "Alimentacao" },
    { "id": 2, "name": "Transporte" }
  ]
}
```

#### POST `/owner`
```json
// Request body
{ "name": "Joao" }

// Response 201
{
  "mensagem": "Owner created successfully",
  "owner": { "id": 1, "name": "Joao" }
}
```

#### GET `/owner`
```json
// Response 200
{
  "owners": [
    { "id": 1, "name": "Joao" },
    { "id": 2, "name": "Maria" }
  ]
}
```

#### POST `/payment`
```json
// Request body
{
  "payment-date": "2026-05-10",
  "reference-date": "2026-05-01",
  "payment-method": "credit-card",
  "card-id": 1,
  "is-installments": true,
  "number-installments": 3,
  "description": "Compra do mês",
  "category-id": 1,
  "is-fixed-expense": false,
  "amount": 199.9,
  "owner-id": 1
}

// Response 201
{
  "mensagem": "Payment created successfully",
  "payment": {
    "id": 1,
    "payment-date": "2026-05-10",
    "reference-date": "2026-05-01",
    "payment-method": "credit-card",
    "card-id": 1,
    "is-installments": true,
    "number-installments": 3,
    "description": "Compra do mês",
    "category-id": 1,
    "is-fixed-expense": false,
    "amount": 199.9,
    "owner-id": 1
  }
}
```

#### GET `/payment`
```json
// Response 200
{
  "payments": [
    {
      "id": 1,
      "payment-date": "2026-05-10",
      "reference-date": "2026-05-01",
      "payment-method": "credit-card",
      "card-id": 1,
      "is-installments": true,
      "number-installments": 3,
      "description": "Compra do mês",
      "category-id": 1,
      "is-fixed-expense": false,
      "amount": 199.9,
      "owner-id": 1
    }
  ]
}
```

---

## Licença

Copyright © 2026

Distribuído sob a [Eclipse Public License 2.0](http://www.eclipse.org/legal/epl-2.0).
