(ns household-financial-control-back.wire.out.return-monthly-reference-payments
  (:require [schema.core :as s]))

(s/defschema monthly-reference-payment-out-schema
  {:reference-date s/Str
   :is-installments s/Bool
   :number-installments s/Int
   :category-id s/Int
   :is-fixed-expense s/Bool
   :amount s/Num})

(s/defschema return-monthly-reference-payments-schema
  {:payments [monthly-reference-payment-out-schema]})

