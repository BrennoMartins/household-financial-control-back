(ns household-financial-control-back.wire.out.return-all-categories
  (:require [schema.core :as s]))

(s/defschema category-out-schema
  {:id   s/Int
   :name s/Str})

(s/defschema return-all-categories-schema
  {:categories [category-out-schema]})

