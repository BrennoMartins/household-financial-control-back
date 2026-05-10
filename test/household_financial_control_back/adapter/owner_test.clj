(ns household-financial-control-back.adapter.owner-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.adapter.owner :as adapter.owner]))

(deftest wire-create-new-owner->internal-owner-test
  (testing "converts wire payload to internal owner"
    (is (= {:name "João"}
           (adapter.owner/wire-create-new-owner->internal-owner {:name "João"})))))

(deftest internal-owners->wire-return-all-owners-test
  (testing "converts list of internal owners to wire output"
    (is (= {:owners [{:id 1 :name "João"}
                     {:id 2 :name "Maria"}]}
           (adapter.owner/internal-owners->wire-return-all-owners
             [{:id 1 :name "João"}
              {:id 2 :name "Maria"}]))))

  (testing "returns empty list when no owners"
    (is (= {:owners []}
           (adapter.owner/internal-owners->wire-return-all-owners [])))))

