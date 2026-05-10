(ns household-financial-control-back.controller.payment
  (:require [household-financial-control-back.diplomatic.db.payment :as diplomatic.db.payment]
            [schema.core :as s]
            [household-financial-control-back.model.payment :as model.payment]))

(s/defn create-new-payment :- model.payment/payment-schema
  [db payment-data :- model.payment/payment-schema]
  (diplomatic.db.payment/create-new-payment db payment-data))

(s/defn return-all-payments :- model.payment/payment-list-schema
  [db]
        (let )
  (diplomatic.db.payment/return-all-payments db))

