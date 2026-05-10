(ns household-financial-control-back.wire.in.create-new-payment
  (:require [schema.core :as s]))

(def iso-date-string
  (s/pred #(try
             (java.time.LocalDate/parse %)
             true
             (catch Exception _
               false))
          'iso-date-string))

(s/defschema create-new-payment-schema
  {:payment-date iso-date-string
   :reference-date iso-date-string
   :payment-method (s/enum "debit-card" "credit-card")
   :card-id s/Int
   :is-installments s/Bool
   :number-installments s/Int
   (s/optional-key :description) (s/maybe s/Str)
   :category-id s/Int
   :is-fixed-expense s/Bool
   :amount s/Num
   :owner-id s/Int})

