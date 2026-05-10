(ns household-financial-control-back.diplomatic.db.card-test
  (:require [clojure.test :refer :all]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [household-financial-control-back.diplomatic.db.card :as diplomatic.db.card]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(deftest create-new-card-persists-and-returns-row
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        card-data {:name "Nubank"}
        expected-sql (-> (h/insert-into :cards)
                         (h/columns :name)
                         (h/values [["Nubank"]])
                         (h/returning :id :name)
                         (sql/format))
        captured (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  [{:id 1 :name "Nubank"}])]
      (is (= {:id 1 :name "Nubank"}
             (diplomatic.db.card/create-new-card db-spec card-data)))
      (is (= {:ds :mock-datasource
              :sql expected-sql
              :opts {:builder-fn rs/as-unqualified-lower-maps}}
             @captured)))))

(deftest return-all-cards-queries-and-returns-cards
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        expected-sql (-> (h/select :id :name)
                         (h/from :cards)
                         (h/order-by [:id :asc])
                         (sql/format))
        expected-cards [{:id 1 :name "Nubank"}
                        {:id 2 :name "Itau"}]
        captured (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  expected-cards)]
      (is (= expected-cards
             (diplomatic.db.card/return-all-cards db-spec)))
      (is (= {:ds :mock-datasource
              :sql expected-sql
              :opts {:builder-fn rs/as-unqualified-lower-maps}}
             @captured)))))

