# API PUT Payment - Documentação

## Endpoint
**PUT** `/payment/:id`

## Descrição
Atualiza um pagamento existente com todos os campos fornecidos no body.

## Parâmetros

### Path
- `id` (required) - ID do pagamento a ser atualizado (número inteiro)

### Body (JSON)
Todos os campos são obrigatórios:

```json
{
  "payment-date": "2026-06-01",
  "reference-date": "2026-06-01",
  "payment-method": "debit-card",
  "card-id": 1,
  "is-installments": false,
  "number-installments": 1,
  "description": "Descrição do pagamento",
  "category-id": 1,
  "is-fixed-expense": false,
  "amount": 100.50,
  "owner-id": 1,
  "quantity-installments": null
}
```

### Campos do Body

| Campo | Tipo | Descrição |
|-------|------|-----------|
| payment-date | ISO Date (YYYY-MM-DD) | Data do pagamento |
| reference-date | ISO Date (YYYY-MM-DD) | Data de referência do pagamento |
| payment-method | String | "debit-card" ou "credit-card" |
| card-id | Integer | ID do cartão |
| is-installments | Boolean | Se o pagamento é parcelado |
| number-installments | Integer | Número total de parcelas |
| description | String (opcional) | Descrição do pagamento |
| category-id | Integer | ID da categoria |
| is-fixed-expense | Boolean | Se é despesa fixa |
| amount | Número | Valor do pagamento |
| owner-id | Integer | ID do proprietário |
| quantity-installments | Integer (opcional) | Quantidade de parcelas restantes |

## Respostas

### Sucesso (200 OK)
```json
{
  "mensagem": "Payment updated successfully",
  "payment": {
    "id": 1,
    "payment-date": "2026-06-01",
    "reference-date": "2026-06-01",
    "payment-method": "debit-card",
    "card": {
      "id": 1,
      "name": "Nubank"
    },
    "is-installments": false,
    "number-installments": 1,
    "quantity_installments": null,
    "description": "Descrição do pagamento",
    "category": {
      "id": 1,
      "name": "Alimentação"
    },
    "is-fixed-expense": false,
    "amount": 100.50,
    "owner": {
      "id": 1,
      "name": "João"
    }
  }
}
```

### Erro - ID inválido (400 Bad Request)
```json
{
  "erro": "Invalid payment ID",
  "detalhes": {
    "id": "abc"
  }
}
```

### Erro - Dados inválidos (400 Bad Request)
```json
{
  "erro": "Invalid data",
  "detalhes": {
    "payment-date": "should be ISO date string"
  }
}
```

### Erro - Pagamento não encontrado (404 Not Found)
```json
{
  "erro": "Payment not found",
  "detalhes": {
    "id": 999
  }
}
```

## Exemplos com cURL

### Exemplo 1: Atualizar valor e descrição
```bash
curl -X PUT http://localhost:3000/payment/1 \
  -H "Content-Type: application/json" \
  -d '{
    "payment-date": "2026-06-01",
    "reference-date": "2026-06-01",
    "payment-method": "debit-card",
    "card-id": 1,
    "is-installments": false,
    "number-installments": 1,
    "description": "Pagamento atualizado",
    "category-id": 1,
    "is-fixed-expense": false,
    "amount": 150.00,
    "owner-id": 1,
    "quantity-installments": null
  }'
```

### Exemplo 2: Atualizar para pagamento parcelado
```bash
curl -X PUT http://localhost:3000/payment/2 \
  -H "Content-Type: application/json" \
  -d '{
    "payment-date": "2026-06-01",
    "reference-date": "2026-06-15",
    "payment-method": "credit-card",
    "card-id": 2,
    "is-installments": true,
    "number-installments": 12,
    "description": "Compra parcelada",
    "category-id": 2,
    "is-fixed-expense": false,
    "amount": 1200.00,
    "owner-id": 1,
    "quantity-installments": 12
  }'
```

## Arquivos Modificados/Criados

1. **Novo**: `src/household_financial_control_back/wire/in/update_payment.clj`
   - Define schema de validação para o body da requisição PUT

2. **Modificado**: `src/household_financial_control_back/diplomatic/db/payment.clj`
   - Adicionada função `update-payment` para executar UPDATE no banco

3. **Modificado**: `src/household_financial_control_back/controller/payment.clj`
   - Adicionada função `update-payment` que enriquece resultado com dados relacionados

4. **Modificado**: `src/household_financial_control_back/adapter/payment.clj`
   - Adicionada função `wire-update-payment->internal-payment` para conversão de dados

5. **Modificado**: `src/household_financial_control_back/diplomatic/http_server.clj`
   - Adicionada rota PUT `/payment/:id` com validações

6. **Novo**: `test/household_financial_control_back/controller/payment_update_test.clj`
   - Testes para a funcionalidade de atualização

## Notas Importantes

- O endpoint valida o ID na URL (deve ser um inteiro válido)
- O endpoint valida todos os campos do body
- Se o pagamento não for encontrado, retorna 404
- A resposta inclui os dados enriquecidos (card, category, owner) com id e name
- Datas devem estar em formato ISO (YYYY-MM-DD)
- payment-method aceita apenas "debit-card" ou "credit-card"

