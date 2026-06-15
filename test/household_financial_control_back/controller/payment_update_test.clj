(ns household-financial-control-back.controller.payment-test
  (:require [clojure.test :refer [deftest is testing]]
            [household-financial-control-back.controller.payment :as controller.payment]
            [household-financial-control-back.model.payment :as model.payment]
            [household-financial-control-back.diplomatic.db.household-financial-db :as db]
            [java.time LocalDate]))

(deftest update-payment-test
  (testing "Should update a payment successfully"
    (let [test-payment {:payment-date (LocalDate/of 2026 6 1)
                        :reference-date (LocalDate/of 2026 6 1)
                        :payment-method :debit-card
                        :card-id 1
                        :is-installments false
                        :number-installments 1
                        :description "Test update"
                        :category-id 1
                        :is-fixed-expense false
                        :amount 100.00
                        :owner-id 1
                        :quantity-installments nil}
          created-payments (controller.payment/create-new-payment db/db test-payment)
          first-payment (first created-payments)
          payment-id (:id first-payment)
          updated-data (assoc test-payment :amount 150.00 :description "Updated payment")
          updated-payment (controller.payment/update-payment db/db payment-id updated-data)]
      (is (some? updated-payment))
      (is (= 150.00 (:amount updated-payment)))
      (is (= "Updated payment" (:description updated-payment))))))

