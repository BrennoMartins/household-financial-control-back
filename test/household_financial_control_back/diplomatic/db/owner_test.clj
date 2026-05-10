(ns household-financial-control-back.diplomatic.db.owner-test
  (:require [clojure.test :refer :all]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [household-financial-control-back.diplomatic.db.owner :as diplomatic.db.owner]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(deftest create-new-owner-persists-and-returns-row
  (let [db-spec    {:dbtype "postgresql" :dbname "household-financial"}
        owner-data {:name "João"}
        expected-sql (-> (h/insert-into :owners)
                         (h/columns :name)
                         (h/values [["João"]])
                         (h/returning :id :name)
                         (sql/format))
        captured   (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  [{:id 1 :name "João"}])]
      (is (= {:id 1 :name "João"}
             (diplomatic.db.owner/create-new-owner db-spec owner-data)))
      (is (= {:ds   :mock-datasource
              :sql  expected-sql
              :opts {:builder-fn rs/as-unqualified-lower-maps}}
             @captured)))))

(deftest return-all-owners-queries-and-returns-owners
  (let [db-spec        {:dbtype "postgresql" :dbname "household-financial"}
        expected-sql   (-> (h/select :id :name)
                           (h/from :owners)
                           (h/order-by [:id :asc])
                           (sql/format))
        expected-owners [{:id 1 :name "João"}
                         {:id 2 :name "Maria"}]
        captured       (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  expected-owners)]
      (is (= expected-owners
             (diplomatic.db.owner/return-all-owners db-spec)))
      (is (= {:ds   :mock-datasource
              :sql  expected-sql
              :opts {:builder-fn rs/as-unqualified-lower-maps}}
             @captured)))))

