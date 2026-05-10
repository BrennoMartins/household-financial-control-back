(ns household-financial-control-back.controller.payment
  (:require [household-financial-control-back.diplomatic.db.payment :as diplomatic.db.payment]
            [schema.core :as s]
            [household-financial-control-back.model.payment :as model.payment]
            [household-financial-control-back.logic.payment :as logic.payment]))

(s/defn create-new-payment :- model.payment/payment-list-schema
  [db payment-data :- model.payment/payment-schema]
  (let [payments-to-create (logic.payment/generate-instalment-payment payment-data)]
    (mapv #(diplomatic.db.payment/create-new-payment db %) payments-to-create)))

(s/defn return-all-payments :- model.payment/payment-list-schema
  [db]
  (diplomatic.db.payment/return-all-payments db))

(s/defn return-monthly-payments-by-year-month :- [model.payment/monthly-payment-schema]
  [db
   year :- s/Int
   month :- s/Int]
  (let [monthly-payments (diplomatic.db.payment/return-payments-by-year-month db year month)]
    (logic.payment/return-payments-by-category monthly-payments)))
