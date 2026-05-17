(ns household-financial-control-back.diplomatic.http-server
  (:require [compojure.core :refer [GET POST PUT defroutes]]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [household-financial-control-back.wire.in.create-new-card :as wire.in.create-new-card]
            [household-financial-control-back.wire.in.create-new-category :as wire.in.create-new-category]
            [household-financial-control-back.wire.in.create-new-owner :as wire.in.create-new-owner]
            [household-financial-control-back.wire.in.create-new-payment :as wire.in.create-new-payment]
            [household-financial-control-back.diplomatic.db.household-financial-db :as diplomatic.db.household-financial-db]
            [household-financial-control-back.controller.card :as controller.card]
            [household-financial-control-back.controller.category :as controller.category]
            [household-financial-control-back.controller.owner :as controller.owner]
            [household-financial-control-back.controller.payment :as controller.payment]
            [household-financial-control-back.adapter.card :as adapter.card]
            [household-financial-control-back.adapter.category :as adapter.category]
            [household-financial-control-back.adapter.owner :as adapter.owner]
            [household-financial-control-back.adapter.payment :as adapter.payment]
            [ring.middleware.cors :refer [wrap-cors]]
            [schema.core :as s]))

(defn- parse-int-safe [value]
  (try
    (Integer/parseInt (str value))
    (catch Exception _
      nil)))

(defroutes app-routes
           (POST "/card" req
             (let [body (:body req)
                   valid? (s/check wire.in.create-new-card/create-new-card-schema body)]
               (if valid?
                 {:status 400 :body {:erro "Invalid data" :detalhes valid?}}
                 (let [created-card (controller.card/create-new-card diplomatic.db.household-financial-db/db (adapter.card/wire-create-new-card->internal-card body))]
                   {:status 201 :body {:mensagem "Card created successfully"
                                       :card created-card}}))))

           (GET "/card" []
                 (let [cards    (controller.card/return-all-cards diplomatic.db.household-financial-db/db)
                       response (adapter.card/internal-cards->wire-return-all-cards cards)]
                   {:status 200
                    :body   response}))

           (POST "/category" req
             (let [body   (:body req)
                   valid? (s/check wire.in.create-new-category/create-new-category-schema body)]
               (if valid?
                 {:status 400 :body {:erro "Invalid data" :detalhes valid?}}
                 (let [created-category (controller.category/create-new-category
                                          diplomatic.db.household-financial-db/db
                                          (adapter.category/wire-create-new-category->internal-category body))]
                   {:status 201 :body {:mensagem "Category created successfully"
                                       :category created-category}}))))

           (GET "/category" []
             (let [categories (controller.category/return-all-categories diplomatic.db.household-financial-db/db)
                   response   (adapter.category/internal-categories->wire-return-all-categories categories)]
               {:status 200 :body response}))

           (POST "/owner" req
             (let [body   (:body req)
                   valid? (s/check wire.in.create-new-owner/create-new-owner-schema body)]
               (if valid?
                 {:status 400 :body {:erro "Invalid data" :detalhes valid?}}
                 (let [created-owner (controller.owner/create-new-owner
                                       diplomatic.db.household-financial-db/db
                                       (adapter.owner/wire-create-new-owner->internal-owner body))]
                   {:status 201 :body {:mensagem "Owner created successfully"
                                       :owner created-owner}}))))

           (GET "/owner" []
             (let [owners   (controller.owner/return-all-owners diplomatic.db.household-financial-db/db)
                   response (adapter.owner/internal-owners->wire-return-all-owners owners)]
               {:status 200 :body response}))

           (POST "/payment" req
             (let [body   (:body req)
                   valid? (s/check wire.in.create-new-payment/create-new-payment-schema body)]
               (if valid?
                 {:status 400 :body {:erro "Invalid data" :detalhes valid?}}
                 (let [created-payment (controller.payment/create-new-payment
                                         diplomatic.db.household-financial-db/db
                                         (adapter.payment/wire-create-new-payment->internal-payment body))]
                   {:status 201 :body {:mensagem "Payment created successfully"
                                       :payment (adapter.payment/internal-payment->wire-payment created-payment)}}))))

           (GET "/payment" []
             (let [payments (controller.payment/return-all-payments diplomatic.db.household-financial-db/db)
                   response (adapter.payment/internal-payments->wire-return-all-payments payments)]
               {:status 200 :body response}))

           (GET "/payment/monthly-reference" [year month]
             (let [year-int (parse-int-safe year)
                   month-int (parse-int-safe month)]
               (if (or (nil? year-int)
                       (nil? month-int)
                       (< month-int 1)
                       (> month-int 12))
                 {:status 400 :body {:erro "Invalid query params" :detalhes {:year year :month month}}}
                 (let [payments (controller.payment/return-monthly-payments-by-year-month
                                  diplomatic.db.household-financial-db/db
                                  year-int
                                  month-int)
                       response (adapter.payment/internal-monthly-payments->wire-return-monthly-reference-payments payments)]
                   {:status 200 :body response}))))

           (route/not-found {:status 404 :body "Route not found"}))

(def app
  (-> app-routes
      (wrap-cors
        :access-control-allow-origin [#"http://localhost:5173"]
        :access-control-allow-methods [:get :post :put :delete :options]
        :access-control-allow-headers ["Content-Type" "Authorization"])
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)))

(defn -main []
  (jetty/run-jetty app {:port 3000}))
