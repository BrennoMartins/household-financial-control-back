(ns household-financial-control-back.controller.category
  (:require [household-financial-control-back.diplomatic.db.category :as diplomatic.db.category]
            [schema.core :as s]
            [household-financial-control-back.model.category :as model.category]))

(s/defn create-new-category :- model.category/category-schema
  [db category-data :- model.category/category-schema]
  (diplomatic.db.category/create-new-category db category-data))

(s/defn return-all-categories :- model.category/category-list-schema
  [db]
  (diplomatic.db.category/return-all-categories db))
