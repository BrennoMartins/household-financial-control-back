(ns household-financial-control-back.model.payment
  (:require [schema.core :as s]))

(def local-date
  (s/pred #(instance? java.time.LocalDate %) 'local-date))

(def payment-method-enum
  (s/enum :debit-card :credit-card))

(def category-object-schema
  {:id s/Int
   :name s/Str})

(def owner-object-schema
  {:id s/Int
   :name s/Str})

(def card-object-schema
  {:id s/Int
   :name s/Str})

(def payment-schema
   {(s/optional-key :id) s/Int
    :payment-date local-date
    :reference-date local-date
    :payment-method payment-method-enum
    :card-id s/Int
    (s/optional-key :card) (s/maybe card-object-schema)
    :is-installments s/Bool
    :number-installments s/Int
    (s/optional-key :quantity-installments) (s/maybe s/Int)
    (s/optional-key :description) (s/maybe s/Str)
    :category-id s/Int
    (s/optional-key :category) (s/maybe category-object-schema)
    :is-fixed-expense s/Bool
    :amount s/Num
    :owner-id s/Int
    (s/optional-key :owner) (s/maybe owner-object-schema)
    :quantity-installments s/Int})

(def payment-list-schema
  [payment-schema])

(def monthly-payment-schema
  {:reference-date local-date
   :is-installments s/Bool
   :number-installments s/Int
   :category-id s/Int
   :is-fixed-expense s/Bool
   :amount s/Num})

(def monthly-reference-payment-schema
  {:category-name s/Str
   :quantity-installments (s/maybe s/Int)
   :number-installments s/Int
   :amount s/Num})

