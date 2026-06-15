(ns household-financial-control-back.controller.payment
  (:require [household-financial-control-back.diplomatic.db.payment :as diplomatic.db.payment]
            [household-financial-control-back.diplomatic.db.category :as diplomatic.db.category]
            [household-financial-control-back.diplomatic.db.card :as diplomatic.db.card]
            [household-financial-control-back.diplomatic.db.owner :as diplomatic.db.owner]
            [schema.core :as s]
            [household-financial-control-back.model.payment :as model.payment]
            [household-financial-control-back.logic.payment :as logic.payment]))

(defn- map-by-id
  [items]
  (into {} (map (fn [item] [(:id item) item]) items)))

(s/defn create-new-payment :- model.payment/payment-list-schema
  [db payment-data :- model.payment/payment-schema]
  (let [payments-to-create (logic.payment/generate-instalment-payment payment-data)
        created-payments (mapv #(diplomatic.db.payment/create-new-payment db %) payments-to-create)
        cards (diplomatic.db.card/return-all-cards db)
        categories (diplomatic.db.category/return-all-categories db)
        owners (diplomatic.db.owner/return-all-owners db)
        card-map (map-by-id cards)
        category-map (map-by-id categories)
        owner-map (map-by-id owners)]
    (mapv (fn [payment]
            (-> payment
                (assoc :card (get card-map (:card-id payment)))
                (assoc :category (get category-map (:category-id payment)))
                (assoc :owner (get owner-map (:owner-id payment)))))
          created-payments)))

(s/defn return-all-payments :- model.payment/payment-list-schema
  [db]
  (let [payments (diplomatic.db.payment/return-all-payments db)
        cards (diplomatic.db.card/return-all-cards db)
        categories (diplomatic.db.category/return-all-categories db)
        owners (diplomatic.db.owner/return-all-owners db)
        card-map (map-by-id cards)
        category-map (map-by-id categories)
        owner-map (map-by-id owners)]
    (mapv (fn [payment]
            (-> payment
                (assoc :card (get card-map (:card-id payment)))
                (assoc :category (get category-map (:category-id payment)))
                (assoc :owner (get owner-map (:owner-id payment)))))
          payments)))

(s/defn return-monthly-payments-by-year-month :- [model.payment/monthly-reference-payment-schema]
  [db
   year :- s/Int
   month :- s/Int]
  (let [monthly-payments (diplomatic.db.payment/return-payments-by-year-month db year month)
        categories (diplomatic.db.category/return-all-categories db)]
    (logic.payment/return-monthly-reference-payments monthly-payments categories)))

(s/defn update-payment :- model.payment/payment-schema
  [db payment-id :- s/Int payment-data :- model.payment/payment-schema]
  (let [updated-payment (diplomatic.db.payment/update-payment db payment-id payment-data)
        cards (diplomatic.db.card/return-all-cards db)
        categories (diplomatic.db.category/return-all-categories db)
        owners (diplomatic.db.owner/return-all-owners db)
        card-map (map-by-id cards)
        category-map (map-by-id categories)
        owner-map (map-by-id owners)]
    (when updated-payment
      (-> updated-payment
          (assoc :card (get card-map (:card-id updated-payment)))
          (assoc :category (get category-map (:category-id updated-payment)))
          (assoc :owner (get owner-map (:owner-id updated-payment)))))))
