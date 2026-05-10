(ns household-financial-control-back.controller.owner
  (:require [household-financial-control-back.diplomatic.db.owner :as diplomatic.db.owner]
            [schema.core :as s]
            [household-financial-control-back.model.owner :as model.owner]))

(s/defn create-new-owner :- model.owner/owner-schema
  [db owner-data :- model.owner/owner-schema]
  (diplomatic.db.owner/create-new-owner db owner-data))

(s/defn return-all-owners :- model.owner/owner-list-schema
  [db]
  (diplomatic.db.owner/return-all-owners db))
