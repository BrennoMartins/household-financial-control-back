(ns household-financial-control-back.model.category
  (:require [schema.core :as s]))

(def category-schema
  {(s/optional-key :id) s/Int
   :name s/Str})

