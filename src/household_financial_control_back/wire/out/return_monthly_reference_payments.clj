(ns household-financial-control-back.wire.out.return-monthly-reference-payments
  (:require [schema.core :as s]))

(s/defschema monthly-reference-payment-out-schema
  {:category_name s/Str
   :quantity_installments (s/maybe s/Int)
   :number_installments s/Int
   :amount s/Num})

(s/defschema return-monthly-reference-payments-schema
  {:payments [monthly-reference-payment-out-schema]})


;;TODO Seria legal api do get payment receber o owner
;;TODO Revisar o que fizemos na logica para retornar o objeto