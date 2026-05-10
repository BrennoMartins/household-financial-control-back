(ns household-financial-control-back.controller.category-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.controller.category :as controller.category]
            [household-financial-control-back.diplomatic.db.category :as diplomatic.db.category]))

(deftest create-new-category-delegates-to-db-layer
  (let [db-spec        {:dbtype "postgresql" :dbname "household-financial"}
        category-data  {:name "Alimentação"}
        expected       {:id 1 :name "Alimentação"}
        captured       (atom nil)]
    (with-redefs [diplomatic.db.category/create-new-category (fn [db payload]
                                                               (reset! captured {:db db :payload payload})
                                                               expected)]
      (is (= expected
             (controller.category/create-new-category db-spec category-data)))
      (is (= {:db db-spec :payload category-data} @captured)))))

(deftest return-all-categories-delegates-to-db-layer
  (let [db-spec  {:dbtype "postgresql" :dbname "household-financial"}
        expected [{:id 1 :name "Alimentação"}]
        captured (atom nil)]
    (with-redefs [diplomatic.db.category/return-all-categories (fn [db]
                                                                 (reset! captured db)
                                                                 expected)]
      (is (= expected
             (controller.category/return-all-categories db-spec)))
      (is (= db-spec @captured)))))

