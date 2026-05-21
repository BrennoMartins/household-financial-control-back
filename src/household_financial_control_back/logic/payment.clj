(ns household-financial-control-back.logic.payment
    (:require [schema.core :as s]
              [household-financial-control-back.model.category :as model.category]
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

(defn- categories-by-id
  [categories]
  (into {}
        (map (fn [category]
               [(:id category) (:name category)]))
        categories))

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

(s/defn return-monthly-reference-payments :- [model.payment/monthly-reference-payment-schema]
  [payments :- model.payment/payment-list-schema
   categories :- model.category/category-list-schema]
  (let [category-id->name (categories-by-id categories)]
    (mapv (fn [payment]
            {:category-name (get category-id->name (:category-id payment) "Unknown category")
             :quantity-installments (:quantity-installments payment)
             :number-installments (:number-installments payment)
             :amount (:amount payment)})
          payments)))
