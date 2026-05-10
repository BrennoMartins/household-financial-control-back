(ns household-financial-control-back.diplomatic.db.payment
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [schema.core :as s]
            [clojure.string]
            [household-financial-control-back.model.payment :as model.payment]))

(defn- snake-to-kebab [m]
  "Converte snake_case para kebab-case nos nomes das chaves"
  (into {}
        (map (fn [[k v]]
               [(keyword (clojure.string/replace (name k) "_" "-")) v])
             m)))

(s/defn create-new-payment :- model.payment/payment-schema
  [db payment-data :- model.payment/payment-schema]
  (let [datasource (jdbc/get-datasource db)
        insert-sql (-> (h/insert-into :payments)
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
                       (sql/format))]
    (let [result (first (jdbc/execute! datasource insert-sql {:builder-fn rs/as-maps}))]
      (-> result
          snake-to-kebab
          (update :payment-method keyword)))))

(s/defn return-all-payments :- model.payment/payment-list-schema
  [db]
  (let [datasource (jdbc/get-datasource db)
        query-sql (-> (h/select :id :payment_date :reference_date :payment_method :card_id :is_installments
                               :number_installments :description :category_id :is_fixed_expense :amount :owner_id)
                      (h/from :payments)
                      (h/order-by [:id :asc])
                      (sql/format))]
    (mapv (fn [payment]
            (-> payment
                snake-to-kebab
                (update :payment-method keyword)))
          (jdbc/execute! datasource query-sql {:builder-fn rs/as-maps}))))

