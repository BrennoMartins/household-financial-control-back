(ns household-financial-control-back.wire.out.return-all-payments
  (:require [schema.core :as s]))
(s/defschema payment-out-schema
  {(s/optional-key :id) s/Int
   :payment-date s/Str
   :reference-date s/Str
   :payment-method (s/enum "debit-card" "credit-card")
   :card-id s/Int
   :is-installments s/Bool
   :number-installments s/Int
   (s/optional-key :description) (s/maybe s/Str)
   :category-id s/Int
   :is-fixed-expense s/Bool
   :amount s/Num
   :owner-id s/Int})
(s/defschema return-all-payments-schema
  {:payments [payment-out-schema]})
