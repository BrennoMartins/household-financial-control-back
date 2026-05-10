(ns household-financial-control-back.diplomatic.db.category-test
  (:require [clojure.test :refer :all]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [household-financial-control-back.diplomatic.db.category :as diplomatic.db.category]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(deftest create-new-category-persists-and-returns-row
  (let [db-spec        {:dbtype "postgresql" :dbname "household-financial"}
        category-data  {:name "Alimentação"}
        expected-sql   (-> (h/insert-into :categories)
                           (h/columns :name)
                           (h/values [["Alimentação"]])
                           (h/returning :id :name)
                           (sql/format))
        captured       (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  [{:id 1 :name "Alimentação"}])]
      (is (= {:id 1 :name "Alimentação"}
             (diplomatic.db.category/create-new-category db-spec category-data)))
      (is (= {:ds   :mock-datasource
              :sql  expected-sql
              :opts {:builder-fn rs/as-unqualified-lower-maps}}
             @captured)))))

(deftest return-all-categories-queries-and-returns-categories
  (let [db-spec           {:dbtype "postgresql" :dbname "household-financial"}
        expected-sql      (-> (h/select :id :name)
                              (h/from :categories)
                              (h/order-by [:id :asc])
                              (sql/format))
        expected-categories [{:id 1 :name "Alimentação"}
                              {:id 2 :name "Transporte"}]
        captured          (atom nil)]
    (with-redefs [jdbc/get-datasource (fn [db]
                                        (is (= db-spec db))
                                        :mock-datasource)
                  jdbc/execute! (fn [ds sql-params opts]
                                  (reset! captured {:ds ds :sql sql-params :opts opts})
                                  expected-categories)]
      (is (= expected-categories
             (diplomatic.db.category/return-all-categories db-spec)))
      (is (= {:ds   :mock-datasource
              :sql  expected-sql
              :opts {:builder-fn rs/as-unqualified-lower-maps}}
             @captured)))))

