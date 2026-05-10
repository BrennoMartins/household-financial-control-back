(ns household-financial-control-back.wire.in.create-new-owner
  (:require [schema.core :as s]))

(s/defschema create-new-owner-schema
  {:name s/Str})
