(ns household-financial-control-back.controller.payment-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.controller.payment :as controller.payment]
            [household-financial-control-back.diplomatic.db.card :as diplomatic.db.card]
            [household-financial-control-back.diplomatic.db.category :as diplomatic.db.category]
            [household-financial-control-back.diplomatic.db.owner :as diplomatic.db.owner]
            [household-financial-control-back.diplomatic.db.payment :as diplomatic.db.payment]
            [household-financial-control-back.logic.payment :as logic.payment]))

(def payment-data
  {:payment-date (java.time.LocalDate/parse "2026-05-10")
   :reference-date (java.time.LocalDate/parse "2026-05-01")
   :payment-method :credit-card
   :card-id 1
   :is-installments true
   :number-installments 3
   :quantity-installments 1
   :description "Compra do mês"
   :category-id 1
   :is-fixed-expense false
   :amount 199.90M
   :owner-id 1})

(deftest create-new-payment-delegates-to-db-layer
  (let [db-spec  {:dbtype "postgresql" :dbname "household-financial"}
        installments [payment-data]
        cards [{:id 1 :name "Nubank"}]
        categories [{:id 1 :name "Alimentação"}]
        owners [{:id 1 :name "Brenno"}]
        expected [(assoc payment-data
                    :card (first cards)
                    :category (first categories)
                    :owner (first owners))]
        captured (atom nil)
        calls (atom [])]
    (with-redefs [logic.payment/generate-instalment-payment (fn [_] installments)
                  diplomatic.db.payment/create-new-payment (fn [db payload]
                                                             (reset! captured {:db db :payload payload})
                                                              payload)
                  diplomatic.db.card/return-all-cards (fn [db]
                                                        (swap! calls conj [:card db])
                                                        cards)
                  diplomatic.db.category/return-all-categories (fn [db]
                                                                  (swap! calls conj [:category db])
                                                                  categories)
                  diplomatic.db.owner/return-all-owners (fn [db]
                                                          (swap! calls conj [:owner db])
                                                          owners)]
      (is (= expected
             (controller.payment/create-new-payment db-spec payment-data)))
      (is (= {:db db-spec :payload payment-data}
             @captured))
      (is (= [[:card db-spec] [:category db-spec] [:owner db-spec]]
             @calls)))))

(deftest return-all-payments-delegates-to-db-layer
  (let [db-spec  {:dbtype "postgresql" :dbname "household-financial"}
        db-payments [payment-data]
        cards [{:id 1 :name "Nubank"}]
        categories [{:id 1 :name "Alimentação"}]
        owners [{:id 1 :name "Brenno"}]
        expected [(assoc payment-data
                    :card (first cards)
                    :category (first categories)
                    :owner (first owners))]
        captured (atom nil)]
    (with-redefs [diplomatic.db.payment/return-all-payments (fn [db]
                                                              (reset! captured db)
                                                               db-payments)
                  diplomatic.db.card/return-all-cards (fn [_] cards)
                  diplomatic.db.category/return-all-categories (fn [_] categories)
                  diplomatic.db.owner/return-all-owners (fn [_] owners)]
      (is (= expected
             (controller.payment/return-all-payments db-spec)))
      (is (= db-spec @captured)))))

(deftest return-monthly-payments-by-year-month-builds-monthly-reference-payload
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        year 2026
        month 5
        db-payments [payment-data]
         categories [{:id 1 :name "Alimentação"}]
         expected [{:category-name "Alimentação"
                    :quantity-installments 1
                    :number-installments 3
                   :amount 199.90M}]
        captured-db (atom nil)
         captured-categories-db (atom nil)
         captured-logic (atom nil)]
    (with-redefs [diplomatic.db.payment/return-payments-by-year-month (fn [db y m]
                                                                        (reset! captured-db {:db db :year y :month m})
                                                                        db-payments)
                   diplomatic.db.category/return-all-categories (fn [db]
                                                                  (reset! captured-categories-db db)
                                                                  categories)
                   logic.payment/return-monthly-reference-payments (fn [payments loaded-categories]
                                                                     (reset! captured-logic {:payments payments :categories loaded-categories})
                                                                     expected)]
      (is (= expected
             (controller.payment/return-monthly-payments-by-year-month db-spec year month)))
      (is (= {:db db-spec :year year :month month}
             @captured-db))
       (is (= db-spec
              @captured-categories-db))
       (is (= {:payments db-payments :categories categories}
             @captured-logic)))))

