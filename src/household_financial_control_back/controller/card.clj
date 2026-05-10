(ns household-financial-control-back.controller.card
  (:require [household-financial-control-back.diplomatic.db.card :as diplomatic.db.card]
            [schema.core :as s]
            [household-financial-control-back.model.card :as model.card]))

(s/defn create-new-card :- model.card/card-schema
  [db card-data :- model.card/card-schema]
  (diplomatic.db.card/create-new-card db card-data))

(s/defn return-all-cards :- model.card/card-list-schema
  [db]
  (diplomatic.db.card/return-all-cards db))
