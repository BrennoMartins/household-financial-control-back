(ns household-financial-control-back.adapter.payment
  (:require [schema.core :as s]
            [household-financial-control-back.wire.in.create-new-payment :as wire.in.create-new-payment]
            [household-financial-control-back.wire.out.return-all-payments :as wire.out.return-all-payments]
            [household-financial-control-back.wire.out.return-monthly-reference-payments :as wire.out.return-monthly-reference-payments]
            [household-financial-control-back.model.payment :as model.payment])
  (:import (java.sql Date)
           (java.time LocalDate ZoneId)))

(defn- ->local-date [value]
  (cond
    (instance? LocalDate value) value
    (instance? Date value) (.toLocalDate ^Date value)
    (instance? java.util.Date value) (-> (.toInstant ^java.util.Date value)
                                         (.atZone (ZoneId/systemDefault))
                                         (.toLocalDate))
    (string? value) (LocalDate/parse value)
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
    (instance? LocalDate value) (.toString ^LocalDate value)
    (instance? Date value) (.toString ^Date value)
    (instance? java.util.Date value) (-> (.toInstant ^java.util.Date value)
                                         (.atZone (ZoneId/systemDefault))
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
   :owner-id (:owner-id payload)
   :quantity-installments (:quantity-installments payload)})

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

(s/defn internal-monthly-payment->wire-payment :- wire.out.return-monthly-reference-payments/monthly-reference-payment-out-schema
  [payment :- model.payment/monthly-reference-payment-schema]
  {:category_name (:category-name payment)
   :quantity_installments (:quantity-installments payment)
   :number_installments (:number-installments payment)
   :amount (:amount payment)})

(s/defn internal-monthly-payments->wire-return-monthly-reference-payments :- wire.out.return-monthly-reference-payments/return-monthly-reference-payments-schema
  [payments :- [model.payment/monthly-reference-payment-schema]]
  {:payments (mapv (fn [payment]
                     (internal-monthly-payment->wire-payment payment))
                   payments)})


