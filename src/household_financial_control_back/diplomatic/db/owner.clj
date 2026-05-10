(ns household-financial-control-back.diplomatic.db.owner
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [schema.core :as s]
            [household-financial-control-back.model.owner :as model.owner]))

(s/defn create-new-owner :- model.owner/owner-schema
  [db owner-data :- model.owner/owner-schema]
  (let [datasource (jdbc/get-datasource db)
        insert-sql (-> (h/insert-into :owners)
                       (h/columns :name)
                       (h/values [[(:name owner-data)]])
                       (h/returning :id :name)
                       (sql/format))]
    (first (jdbc/execute! datasource insert-sql {:builder-fn rs/as-unqualified-lower-maps}))))

(s/defn return-all-owners :- model.owner/owner-list-schema
  [db]
  (let [datasource (jdbc/get-datasource db)
        query-sql (-> (h/select :id :name)
                      (h/from :owners)
                      (h/order-by [:id :asc])
                      (sql/format))]
    (jdbc/execute! datasource query-sql {:builder-fn rs/as-unqualified-lower-maps})))
