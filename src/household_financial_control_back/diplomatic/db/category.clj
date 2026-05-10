(ns household-financial-control-back.diplomatic.db.category
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [schema.core :as s]
            [household-financial-control-back.model.category :as model.category]))

(s/defn create-new-category :- model.category/category-schema
  [db category-data :- model.category/category-schema]
  (let [datasource (jdbc/get-datasource db)
        insert-sql (-> (h/insert-into :categories)
                       (h/columns :name)
                       (h/values [[(:name category-data)]])
                       (h/returning :id :name)
                       (sql/format))]
    (first (jdbc/execute! datasource insert-sql {:builder-fn rs/as-unqualified-lower-maps}))))

(s/defn return-all-categories :- model.category/category-list-schema
  [db]
  (let [datasource (jdbc/get-datasource db)
        query-sql (-> (h/select :id :name)
                      (h/from :categories)
                      (h/order-by [:id :asc])
                      (sql/format))]
    (jdbc/execute! datasource query-sql {:builder-fn rs/as-unqualified-lower-maps})))
