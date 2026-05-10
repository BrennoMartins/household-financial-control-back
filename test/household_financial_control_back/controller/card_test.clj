(ns household-financial-control-back.controller.card-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.controller.card :as controller.card]
            [household-financial-control-back.diplomatic.db.card :as diplomatic.db.card]))

(deftest create-new-card-delegates-to-db-layer
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        card-data {:name "Nubank"}
        captured (atom nil)
        expected {:id 1 :name "Nubank"}]
    (with-redefs [diplomatic.db.card/create-new-card (fn [db payload]
                                                       (reset! captured {:db db :payload payload})
                                                       expected)]
      (is (= {:id 1 :name "Nubank"}
             (controller.card/create-new-card db-spec card-data)))
      (is (= {:db db-spec
              :payload card-data}
             @captured)))))

(deftest return-all-cards-delegates-to-db-layer
  (let [db-spec {:dbtype "postgresql" :dbname "household-financial"}
        expected [{:id 1 :name "Nubank"}]
        captured (atom nil)]
    (with-redefs [diplomatic.db.card/return-all-cards (fn [db]
                                                        (reset! captured db)
                                                        expected)]
      (is (= expected
             (controller.card/return-all-cards db-spec)))
      (is (= db-spec @captured)))))

