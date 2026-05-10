(ns household-financial-control-back.wire.in.create-new-category
  (:require [schema.core :as s]))

(s/defschema create-new-category-schema
  {:name s/Str})
