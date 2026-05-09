(ns household-financial-control-back.adapter.card
  (:require [schema.core :as s]
            [household-financial-control-back.wire.in.create-new-card :as wire.in.create-new-card]
            [household-financial-control-back.model.card :as model.card]))


(s/defn wire-create-new-card->internal-card :- model.card/card-schema
        [payload :- wire.in.create-new-card/create-new-card-schema]
        {:name (:name payload)})
