(ns household-financial-control-back.diplomatic.http-server
  (:require [compojure.core :refer [GET POST PUT defroutes]]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [household-financial-control-back.wire.in.create-new-card :as wire.in.create-new-card]
            [household-financial-control-back.diplomatic.db.household-financial-db :as diplomatic.db.household-financial-db]
            [household-financial-control-back.controller.card :as controller.card]
            [household-financial-control-back.adapter.card :as adapter.card]
            [ring.middleware.cors :refer [wrap-cors]]
            [schema.core :as s]))

(defroutes app-routes
           (POST "/card" req
             (let [body (:body req)
                   valid? (s/check wire.in.create-new-card/create-new-card-schema body)]
               (if valid?
                 {:status 400 :body {:erro "Invalid data" :detalhes valid?}}
                 (do
                   (controller.card/create-new-card diplomatic.db.household-financial-db/db (adapter.card/wire-create-new-card->internal-card body))
                   {:status 201 :body {:mensagem "Card created successfully"}}))))

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
