(ns household-financial-control-back.wire.in.create-new-card
  (:require [schema.core :as s]))

(s/defschema create-new-card-schema
  {:name s/Str})
