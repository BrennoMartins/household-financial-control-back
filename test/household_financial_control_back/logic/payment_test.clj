(ns household-financial-control-back.logic.payment-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.logic.payment :as logic.payment]))

(deftest generate-instalment-payment-creates-installments-with-sequence
  (let [payment-data {:payment-date (java.time.LocalDate/parse "2026-05-10")
                      :reference-date (java.time.LocalDate/parse "2026-05-01")
                      :payment-method :credit-card
                      :card-id 1
                      :is-installments true
                      :number-installments 3
                      :description "Compra do mes"
                      :category-id 1
                      :is-fixed-expense false
                      :amount 300.00M
                      :owner-id 1}
        installments (logic.payment/generate-instalment-payment payment-data)]
    (is (= 3 (count installments)))
    (is (= [1 2 3] (mapv :quantity-installments installments)))
    (is (= [(java.time.LocalDate/parse "2026-05-01")
            (java.time.LocalDate/parse "2026-06-01")
            (java.time.LocalDate/parse "2026-07-01")]
           (mapv :reference-date installments)))
    (is (= [100.0 100.0 100.0]
           (mapv :amount installments)))))

(deftest generate-instalment-payment-keeps-single-payment-when-not-installments
  (let [payment-data {:payment-date (java.time.LocalDate/parse "2026-05-10")
                      :reference-date (java.time.LocalDate/parse "2026-05-01")
                      :payment-method :debit-card
                      :card-id 1
                      :is-installments false
                      :number-installments 1
                      :description "Conta fixa"
                      :category-id 2
                      :is-fixed-expense true
                      :amount 90.00M
                      :owner-id 1}]
    (is (= [payment-data]
           (logic.payment/generate-instalment-payment payment-data)))))

(deftest return-payments-by-category-aggregates-and-picks-earliest-reference-date
  (let [payments [{:payment-date (java.time.LocalDate/parse "2026-06-05")
                   :reference-date (java.time.LocalDate/parse "2026-06-15")
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
                   :reference-date (java.time.LocalDate/parse "2026-06-01")
                   :payment-method :credit-card
                   :card-id 1
                   :is-installments false
                   :number-installments 1
                   :description "B"
                   :category-id 1
                   :is-fixed-expense false
                   :amount 50.00M
                   :owner-id 1}
                  {:payment-date (java.time.LocalDate/parse "2026-06-12")
                   :reference-date (java.time.LocalDate/parse "2026-06-02")
                   :payment-method :debit-card
                   :card-id 2
                   :is-installments false
                   :number-installments 1
                   :description nil
                   :category-id 2
                   :is-fixed-expense true
                   :amount 75.00M
                   :owner-id 1}]
        expected [{:reference-date (java.time.LocalDate/parse "2026-06-01")
                   :is-installments false
                   :number-installments 1
                   :category-id 1
                   :is-fixed-expense false
                   :amount 150.00M}
                  {:reference-date (java.time.LocalDate/parse "2026-06-02")
                   :is-installments false
                   :number-installments 1
                   :category-id 2
                   :is-fixed-expense true
                   :amount 75.00M}]]
    (is (= expected
           (logic.payment/return-payments-by-category payments)))))

