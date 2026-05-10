(ns household-financial-control-back.diplomatic.db.payment-test
  (:require [clojure.test :refer :all]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [household-financial-control-back.diplomatic.db.payment :as diplomatic.db.payment]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs])
  (:import (java.time LocalDate)))

(def payment-data
  {:payment-date (LocalDate/parse "2026-05-10")
   :reference-date (LocalDate/parse "2026-05-01")
   :payment-method :credit-card
   :card-id 1
   :is-installments true
   :number-installments 3
   :description "Compra do mes"
   :category-id 1
   :is-fixed-expense false
   :amount 199.90M
   :owner-id 1})

(def payment-data-db-return
  (assoc payment-data :id 1))

(deftest create-new-payment-persists-and-returns-row
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        expected-sql (-> (h/insert-into :payments)
                         (h/columns :payment_date :reference_date :payment_method :card_id :is_installments
                                    :number_installments :description :category_id :is_fixed_expense :amount :owner_id)
                         (h/values [[(:payment-date payment-data)
                                     (:reference-date payment-data)
                                     (name (:payment-method payment-data))
                                     (:card-id payment-data)
                                     (:is-installments payment-data)
                                     (:number-installments payment-data)
                                     (:description payment-data)
                                     (:category-id payment-data)
                                     (:is-fixed-expense payment-data)
                                     (:amount payment-data)
                                     (:owner-id payment-data)]])
                         (h/returning :id :payment_date :reference_date :payment_method :card_id :is_installments
                                      :number_installments :description :category_id :is_fixed_expense :amount :owner_id)
                         (sql/format))
        captured (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  [payment-data-db-return])]
      (is (= payment-data-db-return
             (diplomatic.db.payment/create-new-payment db-spec payment-data)))
      (is (= {:ds :mock-datasource
              :sql expected-sql
              :opts {:builder-fn rs/as-maps}}
             @captured)))))

(deftest return-all-payments-queries-and-returns-payments
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        expected-sql (-> (h/select :id :payment_date :reference_date :payment_method :card_id :is_installments
                                   :number_installments :description :category_id :is_fixed_expense :amount :owner_id)
                         (h/from :payments)
                         (h/order-by [:id :asc])
                         (sql/format))
            payment-2 (-> payment-data
                          (assoc :id 2)
                          (assoc :payment-method :debit-card))
            expected-payments [payment-data-db-return
                               (-> payment-2
                               (assoc :payment-method :debit-card)
                               (assoc :is-installments false)
                               (assoc :number-installments 1)
                               (assoc :description nil)
                               (assoc :amount 75.00M)
                               (assoc :card-id 2)
                               (assoc :category-id 2)
                               (assoc :owner-id 2))]
        captured (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  expected-payments)]
      (is (= expected-payments
             (diplomatic.db.payment/return-all-payments db-spec)))
      (is (= {:ds :mock-datasource
              :sql expected-sql
              :opts {:builder-fn rs/as-maps}}
             @captured)))))

(deftest return-payments-by-year-month-queries-and-returns-payments
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        year 2026
        month 5
        start-date (LocalDate/of year month 1)
        end-date (.plusMonths start-date 1)
        expected-sql (-> (h/select :id :payment_date :reference_date :payment_method :card_id :is_installments
                                   :number_installments :description :category_id :is_fixed_expense :amount :owner_id)
                         (h/from :payments)
                         (h/where [:and [:>= :reference_date start-date]
                                   [:< :reference_date end-date]])
                         (h/order-by [:id :asc])
                         (sql/format))
        expected-payments [payment-data-db-return]
        captured (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  expected-payments)]
      (is (= expected-payments
             (diplomatic.db.payment/return-payments-by-year-month db-spec year month)))
      (is (= {:ds :mock-datasource
              :sql expected-sql
              :opts {:builder-fn rs/as-maps}}
             @captured)))))

