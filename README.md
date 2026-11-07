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

Antes de executar a suíte completa ou a cobertura, confirme que o PostgreSQL configurado é descartável e isolado. O arquivo `test/household_financial_control_back/controller/payment_update_test.clj` cria e atualiza pagamentos no banco configurado pela aplicação. Prefira testes unitários direcionados quando forem suficientes.

```bash
# Rodar todos os testes, somente com banco descartável e isolado
lein test

# Verificar cobertura, somente com banco descartável e isolado
lein cloverage
```

O relatório de cobertura HTML é gerado em `target/coverage/index.html`.

---

## Endpoints e exemplos de curl

### Preparação

Com o PostgreSQL configurado e a API executando via `lein run`, a URL local padrão é `http://localhost:3000`. Os comandos abaixo podem ser executados em outro terminal; `-i` inclui o status e os cabeçalhos HTTP na resposta. Valores e IDs dos exemplos são fictícios. Não é necessário iniciar a API ou conectar ao banco para consultar esta documentação.

Respostas podem conter dados financeiros pessoais: evite compartilhá-las ou salvá-las em locais acessíveis a terceiros.

| Método | Rota | Finalidade | Sucesso |
|---|---|---|---|
| GET | `/card` | Lista cartões | 200 |
| POST | `/card` | Cadastra cartão | 201 |
| GET | `/category` | Lista categorias | 200 |
| POST | `/category` | Cadastra categoria | 201 |
| GET | `/owner` | Lista proprietários | 200 |
| POST | `/owner` | Cadastra proprietário | 201 |
| GET | `/payment` | Lista todos os pagamentos | 200 |
| POST | `/payment` | Cadastra pagamento ou parcelas | 201 |
| PUT | `/payment/:id` | Atualiza um pagamento | 200 |
| GET | `/payment/monthly-reference?year=2026&month=5` | Lista pagamentos do mês de referência | 200 |

### Consultas somente de leitura

```bash
# Cartões: {"cards": [{"id": 1, "name": "..."}]}
curl -i 'http://localhost:3000/card'

# Categorias: {"categories": [{"id": 1, "name": "..."}]}
curl -i 'http://localhost:3000/category'

# Proprietários: {"owners": [{"id": 1, "name": "..."}]}
curl -i 'http://localhost:3000/owner'

# Todos os pagamentos: {"payments": [...]}
curl -i 'http://localhost:3000/payment'

# Pagamentos por ano e mês da data de referência
curl -i 'http://localhost:3000/payment/monthly-reference?year=2026&month=5'
```

`GET /payment` retorna todos os registros, sem paginação ou filtro de período. Exemplo de um item em `payments`:

```json
{
  "id": 1,
  "payment-date": "2026-05-10",
  "reference-date": "2026-05-01",
  "payment-method": "credit-card",
  "card": {"id": 1, "name": "Cartão exemplo"},
  "is-installments": true,
  "number-installments": 3,
  "quantity_installments": 1,
  "description": "Compra exemplo",
  "category": {"id": 1, "name": "Categoria exemplo"},
  "is-fixed-expense": false,
  "amount": 100.00,
  "owner": {"id": 1, "name": "Pessoa exemplo"}
}
```

`GET /payment/monthly-reference` exige `year` inteiro e `month` inteiro de 1 a 12. Filtra `reference-date` desde o primeiro dia do mês, inclusive, até o primeiro dia do mês seguinte, exclusivo. Ele retorna um item por pagamento, **sem agrupar ou somar**. A resposta contém apenas categoria, parcela atual, número de parcelas e valor:

```json
{
  "payments": [
    {"category_name": "Categoria exemplo", "quantity_installments": 1, "number_installments": 3, "amount": 100.00}
  ]
}
```

`quantity_installments` é o número da parcela atual, não o número de parcelas restantes; pode ser `null`. Para uma análise, determine o período e a data relevante (`reference-date` ou `payment-date`) e agregue os itens conforme a pergunta. O endpoint mensal não inclui data de pagamento, cartão, proprietário ou ID; use `GET /payment` se precisar desses campos. Parcelas aparecem como registros separados: não confunda o valor da parcela com o valor total da compra.

### Escrita: somente com autorização

**POST e PUT alteram dados persistidos.** Execute os exemplos abaixo apenas em uma base descartável e isolada, ou com autorização explícita para alterar a base de destino. Os IDs de cartão, categoria e proprietário de um pagamento devem existir na base usada.

#### Criar cartão, categoria ou proprietário

Cada body exige `name` como string. O sucesso retorna `mensagem` e o objeto criado sob `card`, `category` ou `owner`, respectivamente.

```bash
curl -i -X POST 'http://localhost:3000/card' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Cartão exemplo"}'

curl -i -X POST 'http://localhost:3000/category' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Categoria exemplo"}'

curl -i -X POST 'http://localhost:3000/owner' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Pessoa exemplo"}'
```

#### Criar ou atualizar pagamento

Os dois métodos usam o mesmo formato de entrada:

| Campo | Tipo | Descrição |
|---|---|---|
| `payment-date`, `reference-date` | string | Datas válidas no formato `YYYY-MM-DD` |
| `payment-method` | string | `debit-card` ou `credit-card` |
| `card-id`, `category-id`, `owner-id` | inteiro | IDs dos registros relacionados |
| `is-installments` | booleano | Se é parcelado |
| `number-installments` | inteiro | Total de parcelas; 1 se não parcelado, maior que 1 se parcelado |
| `quantity-installments` | inteiro ou `null`, opcional | Número da parcela atual |
| `description` | string ou `null`, opcional | Descrição |
| `is-fixed-expense` | booleano | Se é despesa fixa |
| `amount` | número | Valor não negativo |

```bash
# Cria um pagamento; retorna {"mensagem": "...", "payments": [...]} (201)
curl -i -X POST 'http://localhost:3000/payment' \
  -H 'Content-Type: application/json' \
  -d '{
    "payment-date": "2026-05-10",
    "reference-date": "2026-05-01",
    "payment-method": "debit-card",
    "card-id": 1,
    "is-installments": false,
    "number-installments": 1,
    "description": "Pagamento exemplo",
    "category-id": 1,
    "is-fixed-expense": false,
    "amount": 100.00,
    "owner-id": 1
  }'

# Substitui os campos de um pagamento existente; retorna {"mensagem": "...", "payment": {...}} (200)
curl -i -X PUT 'http://localhost:3000/payment/1' \
  -H 'Content-Type: application/json' \
  -d '{
    "payment-date": "2026-05-10",
    "reference-date": "2026-05-01",
    "payment-method": "debit-card",
    "card-id": 1,
    "is-installments": false,
    "number-installments": 1,
    "quantity-installments": null,
    "description": "Pagamento atualizado",
    "category-id": 1,
    "is-fixed-expense": false,
    "amount": 120.00,
    "owner-id": 1
  }'
```

No POST parcelado, `amount` é o total informado: a aplicação cria um registro por parcela, divide o valor pelo número de parcelas e avança a data de referência mensalmente. O PUT atualiza somente o registro identificado; não recria as demais parcelas. Os campos opcionais omitidos no PUT são gravados como `null`. Os campos de entrada usam `quantity-installments`, enquanto a saída usa `quantity_installments`.

Dados de entrada inválidos retornam HTTP 400 com `erro` e `detalhes`. No PUT, um ID não inteiro retorna 400 e um pagamento inexistente retorna 404. Na consulta mensal, parâmetros ausentes ou inválidos também retornam 400. Outros erros, como violações de restrições do banco, não possuem um contrato de resposta específico documentado.

---

## Licença

Copyright © 2026

Distribuído sob a [Eclipse Public License 2.0](http://www.eclipse.org/legal/epl-2.0).
