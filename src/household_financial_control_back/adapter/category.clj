(ns household-financial-control-back.adapter.category
  (:require [schema.core :as s]
            [household-financial-control-back.wire.in.create-new-category :as wire.in.create-new-category]
            [household-financial-control-back.wire.out.return-all-categories :as wire.out.return-all-categories]
             [household-financial-control-back.model.category :as model.category]))


(s/defn wire-create-new-category->internal-category :- model.category/category-schema
  [payload :- wire.in.create-new-category/create-new-category-schema]
  {:name (:name payload)})


(s/defn internal-categories->wire-return-all-categories :- wire.out.return-all-categories/return-all-categories-schema
  [categories :- model.category/category-list-schema]
  {:categories (mapv (fn [c] {:id (:id c) :name (:name c)}) categories)})
