(ns household-financial-control-back.logic.payment
    (:require [schema.core :as s]
              [household-financial-control-back.model.payment :as model.payment])
    (:import [java.time LocalDate]))

(defn- add-months-to-date
  "Add months to a LocalDate"
  [^LocalDate date months]
  (.plusMonths date months))

(defn- divide-amount
  "Divide amount by installments with precision"
  [amount installments]
  (double (/ amount installments)))

(defn- earliest-reference-date [grouped-payments]
  (reduce (fn [earliest payment]
            (let [current-date (:reference-date payment)]
              (if (neg? (compare current-date earliest))
                current-date
                earliest)))
          (:reference-date (first grouped-payments))
          (rest grouped-payments)))

(s/defn generate-instalment-payment :- model.payment/payment-list-schema
  [payment-data :- model.payment/payment-schema]
  (let [is-installments? (:is-installments payment-data)
        number-installments (:number-installments payment-data)
        total-amount (:amount payment-data)
        reference-date (:reference-date payment-data)
        installment-amount (divide-amount total-amount (if (> number-installments 1) number-installments 1))]
    (if (and is-installments? (> number-installments 1))
      (vec (map (fn [installment-index]
                  (-> payment-data
                      (assoc :amount installment-amount)
                      (assoc :is-installments true)
                      (assoc :quantity-installments (inc installment-index))
                      (assoc :reference-date
                             (add-months-to-date reference-date installment-index))))
                (range number-installments)))
      [payment-data])))

(s/defn return-payments-by-category :- [model.payment/monthly-payment-schema]
  [payments :- model.payment/payment-list-schema]
  (->> payments
       (group-by :category-id)
       (mapv (fn [[category-id grouped-payments]]
               (let [reference-date (earliest-reference-date grouped-payments)
                     total-amount (reduce + (map :amount grouped-payments))]
                 {:reference-date reference-date
                  :is-installments false
                  :number-installments 1
                  :category-id category-id
                  :is-fixed-expense (every? :is-fixed-expense grouped-payments)
                  :amount total-amount})))
       (sort-by :category-id)))
