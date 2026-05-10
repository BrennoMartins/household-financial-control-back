BEGIN;

-- Tabelas auxiliares (1 lado do relacionamento)
CREATE TABLE IF NOT EXISTS cards (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS owners (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL UNIQUE
    );

-- Tabela principal (N lado do relacionamento)
CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        payment_date DATE NOT NULL,                 -- Payment Date
                                        reference_date DATE NOT NULL,               -- Reference Date
                                        payment_method VARCHAR(50) NOT NULL,        -- Payment Method
    card_id BIGINT NOT NULL,                    -- Card (FK)
    is_installments BOOLEAN NOT NULL DEFAULT FALSE, -- Is Installments
    number_installments INTEGER NOT NULL DEFAULT 1, -- Number Installments
    description TEXT,                           -- Description
    category_id BIGINT NOT NULL,                -- Category (FK)
    is_fixed_expense BOOLEAN NOT NULL DEFAULT FALSE, -- Is Fixed Expense
    amount NUMERIC(14,2) NOT NULL,              -- Amount
    owner_id BIGINT NOT NULL,                   -- Owner (FK)

    CONSTRAINT fk_payments_card
    FOREIGN KEY (card_id) REFERENCES cards(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT fk_payments_category
    FOREIGN KEY (category_id) REFERENCES categories(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT fk_payments_owner
    FOREIGN KEY (owner_id) REFERENCES owners(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT chk_payments_amount_non_negative
    CHECK (amount >= 0),

    -- Regra de parcelas:
    -- se for parcelado, precisa ser > 1
    -- se não for parcelado, precisa ser 1
    CONSTRAINT chk_payments_installments_logic
    CHECK (
(is_installments = TRUE  AND number_installments > 1) OR
(is_installments = FALSE AND number_installments = 1)
    )
    );

-- Índices para melhorar consultas por relacionamento
CREATE INDEX IF NOT EXISTS idx_payments_card_id ON payments(card_id);
CREATE INDEX IF NOT EXISTS idx_payments_category_id ON payments(category_id);
CREATE INDEX IF NOT EXISTS idx_payments_owner_id ON payments(owner_id);

COMMIT;
