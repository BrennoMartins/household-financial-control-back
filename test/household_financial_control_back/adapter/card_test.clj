(ns household-financial-control-back.adapter.card-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.adapter.card :as adapter.card]))

(deftest wire-create-new-card->internal-card-test
  (testing "converts wire payload to internal card"
    (is (= {:name "Nubank"}
           (adapter.card/wire-create-new-card->internal-card {:name "Nubank"})))))

(deftest internal-cards->wire-return-all-cards-test
  (testing "converts list of internal cards to wire output"
    (is (= {:cards [{:id 1 :name "Nubank"}
                    {:id 2 :name "Itau"}]}
           (adapter.card/internal-cards->wire-return-all-cards
             [{:id 1 :name "Nubank"}
              {:id 2 :name "Itau"}]))))

  (testing "returns empty list when no cards"
    (is (= {:cards []}
           (adapter.card/internal-cards->wire-return-all-cards [])))))

