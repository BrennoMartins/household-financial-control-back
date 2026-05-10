(ns household-financial-control-back.adapter.owner
  (:require [schema.core :as s]
            [household-financial-control-back.wire.in.create-new-owner :as wire.in.create-new-owner]
            [household-financial-control-back.wire.out.return-all-owners :as wire.out.return-all-owners]
             [household-financial-control-back.model.owner :as model.owner]))


(s/defn wire-create-new-owner->internal-owner :- model.owner/owner-schema
  [payload :- wire.in.create-new-owner/create-new-owner-schema]
  {:name (:name payload)})


(s/defn internal-owners->wire-return-all-owners :- wire.out.return-all-owners/return-all-owners-schema
  [owners :- model.owner/owner-list-schema]
  {:owners (mapv (fn [o] {:id (:id o) :name (:name o)}) owners)})
