(ns household-financial-control-back.wire.out.return-all-owners
  (:require [schema.core :as s]))

(s/defschema owner-out-schema
  {:id   s/Int
   :name s/Str})

(s/defschema return-all-owners-schema
  {:owners [owner-out-schema]})

