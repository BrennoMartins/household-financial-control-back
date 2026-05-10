(ns household-financial-control-back.adapter.payment
  (:require [schema.core :as s]
            [household-financial-control-back.wire.in.create-new-payment :as wire.in.create-new-payment]
            [household-financial-control-back.wire.out.return-all-payments :as wire.out.return-all-payments]
            [household-financial-control-back.model.payment :as model.payment]))

(defn- ->local-date [value]
  (cond
    (instance? java.time.LocalDate value) value
    (instance? java.sql.Date value) (.toLocalDate ^java.sql.Date value)
    (instance? java.util.Date value) (-> (.toInstant ^java.util.Date value)
                                         (.atZone (java.time.ZoneId/systemDefault))
                                         (.toLocalDate))
    (string? value) (java.time.LocalDate/parse value)
    :else value))

(defn- ->payment-method-keyword [value]
  (cond
    (keyword? value) value
    (string? value) (keyword value)
    :else value))

(defn- ->payment-method-string [value]
  (cond
    (keyword? value) (name value)
    (string? value) value
    :else value))

(defn- ->wire-date [value]
  (cond
    (instance? java.time.LocalDate value) (.toString ^java.time.LocalDate value)
    (instance? java.sql.Date value) (.toString ^java.sql.Date value)
    (instance? java.util.Date value) (-> (.toInstant ^java.util.Date value)
                                         (.atZone (java.time.ZoneId/systemDefault))
                                         (.toLocalDate)
                                         (.toString))
    :else value))

(defn- ->wire-payment-method [value]
  (cond
    (keyword? value) (name value)
    (string? value) value
    :else value))

(s/defn wire-create-new-payment->internal-payment :- model.payment/payment-schema
  [payload :- wire.in.create-new-payment/create-new-payment-schema]
  {:payment-date (->local-date (:payment-date payload))
   :reference-date (->local-date (:reference-date payload))
   :payment-method (->payment-method-keyword (:payment-method payload))
   :card-id (:card-id payload)
   :is-installments (:is-installments payload)
   :number-installments (:number-installments payload)
   :description (:description payload)
   :category-id (:category-id payload)
   :is-fixed-expense (:is-fixed-expense payload)
   :amount (:amount payload)
   :owner-id (:owner-id payload)})

(s/defn internal-payment->wire-payment :- wire.out.return-all-payments/payment-out-schema
  [payment :- model.payment/payment-schema]
  {:id (:id payment)
   :payment-date (->wire-date (:payment-date payment))
   :reference-date (->wire-date (:reference-date payment))
   :payment-method (->payment-method-string (:payment-method payment))
   :card-id (:card-id payment)
   :is-installments (:is-installments payment)
   :number-installments (:number-installments payment)
   :description (:description payment)
   :category-id (:category-id payment)
   :is-fixed-expense (:is-fixed-expense payment)
   :amount (:amount payment)
   :owner-id (:owner-id payment)})

(s/defn internal-payments->wire-return-all-payments :- wire.out.return-all-payments/return-all-payments-schema
  [payments :- model.payment/payment-list-schema]
  {:payments (mapv (fn [payment]
                     (internal-payment->wire-payment payment))
                   payments)})


