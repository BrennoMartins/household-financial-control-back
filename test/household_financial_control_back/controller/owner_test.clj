(ns household-financial-control-back.controller.owner-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.controller.owner :as controller.owner]
            [household-financial-control-back.diplomatic.db.owner :as diplomatic.db.owner]))

(deftest create-new-owner-delegates-to-db-layer
  (let [db-spec    {:dbtype "postgresql" :dbname "household-financial"}
        owner-data {:name "João"}
        expected   {:id 1 :name "João"}
        captured   (atom nil)]
    (with-redefs [diplomatic.db.owner/create-new-owner (fn [db payload]
                                                         (reset! captured {:db db :payload payload})
                                                         expected)]
      (is (= expected
             (controller.owner/create-new-owner db-spec owner-data)))
      (is (= {:db db-spec :payload owner-data} @captured)))))

(deftest return-all-owners-delegates-to-db-layer
  (let [db-spec  {:dbtype "postgresql" :dbname "household-financial"}
        expected [{:id 1 :name "João"}]
        captured (atom nil)]
    (with-redefs [diplomatic.db.owner/return-all-owners (fn [db]
                                                          (reset! captured db)
                                                          expected)]
      (is (= expected
             (controller.owner/return-all-owners db-spec)))
      (is (= db-spec @captured)))))

