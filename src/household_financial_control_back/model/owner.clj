(ns household-financial-control-back.model.owner
  (:require [schema.core :as s]))

(def owner-schema
  {(s/optional-key :id) s/Int
   :name s/Str})

