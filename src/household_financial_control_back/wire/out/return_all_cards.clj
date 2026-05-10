(ns household-financial-control-back.wire.out.return-all-cards
  (:require [schema.core :as s]))

(s/defschema card-out-schema
  {:id   s/Int
   :name s/Str})

(s/defschema return-all-cards-schema
  {:cards [card-out-schema]})
