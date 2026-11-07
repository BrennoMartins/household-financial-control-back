(ns household-financial-control-back.wire.out.return-all-payments
  (:require [schema.core :as s]))

(s/defschema category-object-schema
  {:id s/Int
   :name s/Str})

(s/defschema owner-object-schema
  {:id s/Int
   :name s/Str})

(s/defschema card-object-schema
  {:id s/Int
   :name s/Str})

(s/defschema payment-out-schema
   {(s/optional-key :id) s/Int
    :payment-date s/Str
    :reference-date s/Str
    :payment-method (s/enum "debit-card" "credit-card")
    (s/optional-key :card) (s/maybe card-object-schema)
    :is-installments s/Bool
    :number-installments s/Int
    (s/optional-key :quantity_installments) (s/maybe s/Int)
    (s/optional-key :description) (s/maybe s/Str)
    (s/optional-key :category) (s/maybe category-object-schema)
    :is-fixed-expense s/Bool
    :amount s/Num
    (s/optional-key :owner) (s/maybe owner-object-schema)})

(s/defschema return-all-payments-schema
   {:payments [payment-out-schema]})
