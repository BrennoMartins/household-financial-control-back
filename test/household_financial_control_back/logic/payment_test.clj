(ns household-financial-control-back.logic.payment-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.logic.payment :as logic.payment]))

(deftest return-payments-by-category-handles-sql-date
  (let [payments [{:payment-date (java.time.LocalDate/parse "2026-06-05")
                   :reference-date (java.sql.Date/valueOf "2026-06-01")
                   :payment-method :credit-card
                   :card-id 1
                   :is-installments false
                   :number-installments 1
                   :description "A"
                   :category-id 1
                   :is-fixed-expense true
                   :amount 100.00M
                   :owner-id 1}
                  {:payment-date (java.time.LocalDate/parse "2026-06-10")
                   :reference-date (java.sql.Date/valueOf "2026-06-15")
                   :payment-method :credit-card
                   :card-id 1
                   :is-installments false
                   :number-installments 1
                   :description "B"
                   :category-id 1
                   :is-fixed-expense false
                   :amount 50.00M
                   :owner-id 1}]
        expected [{:reference-date (java.sql.Date/valueOf "2026-06-01")
                   :is-installments false
                   :number-installments 1
                   :category-id 1
                   :is-fixed-expense false
                   :amount 150.00M}]]
    (is (= expected
           (logic.payment/return-payments-by-category payments)))))

