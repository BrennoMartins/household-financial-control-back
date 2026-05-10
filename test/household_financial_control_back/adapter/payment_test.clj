(ns household-financial-control-back.adapter.payment-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.adapter.payment :as adapter.payment]))
(deftest wire-create-new-payment->internal-payment-converts-types
  (let [payload {:payment-date "2026-05-10"
                 :reference-date "2026-05-01"
                 :payment-method "credit-card"
                 :card-id 1
                 :is-installments true
                 :number-installments 3
                 :description "Compra do mês"
                 :category-id 1
                 :is-fixed-expense false
                 :amount 199.90
                 :owner-id 1}
        expected {:payment-date (java.time.LocalDate/parse "2026-05-10")
                  :reference-date (java.time.LocalDate/parse "2026-05-01")
                  :payment-method :credit-card
                  :card-id 1
                  :is-installments true
                  :number-installments 3
                  :description "Compra do mês"
                  :category-id 1
                  :is-fixed-expense false
                  :amount 199.90
                  :owner-id 1}]
    (is (= expected
           (adapter.payment/wire-create-new-payment->internal-payment payload)))))
(deftest internal-payment->wire-payment-converts-types
  (let [payment {:id 1
                 :payment-date (java.sql.Date/valueOf "2026-05-10")
                 :reference-date (java.time.LocalDate/parse "2026-05-01")
                 :payment-method :credit-card
                 :card-id 1
                 :is-installments true
                 :number-installments 3
                 :description "Compra do mês"
                 :category-id 1
                 :is-fixed-expense false
                 :amount 199.90M
                 :owner-id 1}
        expected {:id 1
                  :payment-date "2026-05-10"
                  :reference-date "2026-05-01"
                  :payment-method "credit-card"
                  :card-id 1
                  :is-installments true
                  :number-installments 3
                  :description "Compra do mês"
                  :category-id 1
                  :is-fixed-expense false
                  :amount 199.90M
                  :owner-id 1}]
    (is (= expected
           (adapter.payment/internal-payment->wire-payment payment)))))
(deftest internal-payments->wire-return-all-payments-converts-list
  (let [payments [{:id 1
                   :payment-date (java.sql.Date/valueOf "2026-05-10")
                   :reference-date (java.time.LocalDate/parse "2026-05-01")
                   :payment-method :credit-card
                   :card-id 1
                   :is-installments true
                   :number-installments 3
                   :description "Compra do mês"
                   :category-id 1
                   :is-fixed-expense false
                   :amount 199.90M
                   :owner-id 1}
                  {:id 2
                   :payment-date (java.sql.Date/valueOf "2026-06-10")
                   :reference-date (java.time.LocalDate/parse "2026-06-01")
                   :payment-method :debit-card
                   :card-id 2
                   :is-installments false
                   :number-installments 1
                   :description nil
                   :category-id 2
                   :is-fixed-expense true
                   :amount 75.00M
                   :owner-id 2}]
        expected {:payments [{:id 1
                              :payment-date "2026-05-10"
                              :reference-date "2026-05-01"
                              :payment-method "credit-card"
                              :card-id 1
                              :is-installments true
                              :number-installments 3
                              :description "Compra do mês"
                              :category-id 1
                              :is-fixed-expense false
                              :amount 199.90M
                              :owner-id 1}
                             {:id 2
                              :payment-date "2026-06-10"
                              :reference-date "2026-06-01"
                              :payment-method "debit-card"
                              :card-id 2
                              :is-installments false
                              :number-installments 1
                              :description nil
                              :category-id 2
                              :is-fixed-expense true
                              :amount 75.00M
                              :owner-id 2}]}]
    (is (= expected
           (adapter.payment/internal-payments->wire-return-all-payments payments)))))
