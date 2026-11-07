# Implementação - API PUT Payment

## ✅ Status: Completo

A API PUT para editar payments foi implementada com sucesso em todos os layers da arquitetura.

---

## 📋 Resumo da Implementação

### 1. **Wire Input Schema** - `update_payment.clj`
- Schema de validação para dados de entrada
- Campos obrigatórios: payment-date, reference-date, payment-method, card-id, is-installments, number-installments, category-id, is-fixed-expense, amount, owner-id
- Campos opcionais: description, quantity-installments
- Validação de datas ISO (YYYY-MM-DD)
- Validação de payment-method ("debit-card" | "credit-card")

### 2. **Database Layer** - `diplomatic/db/payment.clj`
```clojure
(s/defn update-payment :- model.payment/payment-schema
  [db payment-id :- s/Int payment-data :- model.payment/payment-schema]
  ...)
```
- Executa UPDATE no banco usando HoneSQL
- Retorna os dados atualizados
- Converte snake_case para kebab-case
- Converte payment-method para keyword

### 3. **Controller Layer** - `controller/payment.clj`
```clojure
(s/defn update-payment :- model.payment/payment-schema
  [db payment-id :- s/Int payment-data :- model.payment/payment-schema]
  ...)
```
- Chama o DB layer para atualizar
- Enriquece o resultado com dados relacionados (card, category, owner)
- Retorna nil se o payment não existir

### 4. **Adapter Layer** - `adapter/payment.clj`
```clojure
(s/defn wire-update-payment->internal-payment :- model.payment/payment-schema
  [payload :- wire.in.update-payment/update-payment-schema]
  ...)
```
- Converte dados do wire para modelo interno
- Realiza transformações de datas (string → LocalDate)
- Converte payment-method (string → keyword)

### 5. **HTTP Server** - `diplomatic/http_server.clj`
```clojure
(PUT "/payment/:id" [id :as req]
  ...)
```
- Rota: `PUT /payment/:id`
- Validações:
  - ID deve ser um inteiro válido
  - Body deve conformar com o schema
- Retorna:
  - **200**: Payment atualizado com dados enriquecidos
  - **400**: ID inválido ou dados inválidos
  - **404**: Payment não encontrado

### 6. **Tests** - `payment_update_test.clj`
- Teste de atualização básica
- Valida se os campos foram atualizados corretamente

---

## 🔗 Fluxo de Requisição

```
PUT /payment/1
    ↓
HTTP Server validates ID & body
    ↓
Adapter: wire → internal
    ↓
Controller: update-payment
    ↓
DB Layer: update-payment
    ↓
Controller: enrich with relationships (card, category, owner)
    ↓
Adapter: internal → wire
    ↓
HTTP Response (200 OK with enriched payment)
```

---

## 📡 Exemplo de Uso

### Request
```bash
curl -X PUT http://localhost:3000/payment/1 \
  -H "Content-Type: application/json" \
  -d '{
    "payment-date": "2026-06-15",
    "reference-date": "2026-06-15",
    "payment-method": "credit-card",
    "card-id": 2,
    "is-installments": true,
    "number-installments": 3,
    "description": "Compra atualizada",
    "category-id": 5,
    "is-fixed-expense": false,
    "amount": 299.99,
    "owner-id": 1,
    "quantity-installments": 2
  }'
```

### Response (200 OK)
```json
{
  "mensagem": "Payment updated successfully",
  "payment": {
    "id": 1,
    "payment-date": "2026-06-15",
    "reference-date": "2026-06-15",
    "payment-method": "credit-card",
    "card": {
      "id": 2,
      "name": "Visa"
    },
    "is-installments": true,
    "number-installments": 3,
    "quantity_installments": 2,
    "description": "Compra atualizada",
    "category": {
      "id": 5,
      "name": "Eletrônicos"
    },
    "is-fixed-expense": false,
    "amount": 299.99,
    "owner": {
      "id": 1,
      "name": "João Silva"
    }
  }
}
```

---

## 📁 Arquivos Criados/Modificados

### ✨ Novos
- `/src/household_financial_control_back/wire/in/update_payment.clj`
- `/test/household_financial_control_back/controller/payment_update_test.clj`
- `/API_PUT_PAYMENT.md` (documentação detalhada)

### ✏️ Modificados
- `/src/household_financial_control_back/diplomatic/db/payment.clj`
- `/src/household_financial_control_back/controller/payment.clj`
- `/src/household_financial_control_back/adapter/payment.clj`
- `/src/household_financial_control_back/diplomatic/http_server.clj`

---

## ✅ Validações

✓ Todas as namespaces compilam com sucesso
✓ Schema validation implementado
✓ Tratamento de erros (400, 404)
✓ Enriquecimento de dados relacionados
✓ Seguindo o padrão arquitetural do projeto

---

## 🚀 Próximos Passos (Opcional)

1. Adicionar testes de integração com banco de dados
2. Adicionar validação de referências (card-id, category-id, owner-id existem?)
3. Considerar estratégia para atualizar parcelas existentes
4. Adicionar endpoint DELETE /payment/:id (complementar)
5. Documentar no README.md da API


