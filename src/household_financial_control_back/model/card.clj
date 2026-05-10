(ns household-financial-control-back.model.card
  (:require [schema.core :as s]))

(def card-schema
  {(s/optional-key :id) s/Int
   :name s/Str})

(def card-list-schema
  [card-schema])

