(ns household-financial-control-back.controller.payment-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.controller.payment :as controller.payment]
            [household-financial-control-back.diplomatic.db.payment :as diplomatic.db.payment]))

(def payment-data
  {:payment-date (java.time.LocalDate/parse "2026-05-10")
   :reference-date (java.time.LocalDate/parse "2026-05-01")
   :payment-method :credit-card
   :card-id 1
   :is-installments true
   :number-installments 3
   :description "Compra do mês"
   :category-id 1
   :is-fixed-expense false
   :amount 199.90M
   :owner-id 1})

(deftest create-new-payment-delegates-to-db-layer
  (let [db-spec  {:dbtype "postgresql" :dbname "household-financial"}
        expected payment-data
        captured (atom nil)]
    (with-redefs [diplomatic.db.payment/create-new-payment (fn [db payload]
                                                             (reset! captured {:db db :payload payload})
                                                             expected)]
      (is (= expected
             (controller.payment/create-new-payment db-spec payment-data)))
      (is (= {:db db-spec :payload payment-data}
             @captured)))))

(deftest return-all-payments-delegates-to-db-layer
  (let [db-spec  {:dbtype "postgresql" :dbname "household-financial"}
        expected [payment-data]
        captured (atom nil)]
    (with-redefs [diplomatic.db.payment/return-all-payments (fn [db]
                                                              (reset! captured db)
                                                              expected)]
      (is (= expected
             (controller.payment/return-all-payments db-spec)))
      (is (= db-spec @captured)))))

