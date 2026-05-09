(ns household-financial-control-back.controller.card
  (:require [schema.core :as s]))

(defn create-new-card
  [db card-data]
  (println "Creating new card with data:" card-data))
