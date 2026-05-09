(ns household-financial-control-back.model.payment
  (:require [schema.core :as s]))

;(def local-date
;  (s/pred #(instance? java.time.LocalDate %) 'local-date))
;
;(def payment-method-enum
;  (s/enum :cash :debit-card :credit-card :pix :bank-transfer :other))
;
;(def payment-schema
;  {(s/optional-key :id) s/Int
;   :payment-date local-date
;   :reference-date local-date
;   :payment-method payment-method-enum
;   :card-id s/Int
;   :is-installments s/Bool
;   :number-installments s/Int
;   (s/optional-key :description) (s/maybe s/Str)
;   :category-id s/Int
;   :is-fixed-expense s/Bool
;   :amount s/Num
;   :owner-id s/Int})

