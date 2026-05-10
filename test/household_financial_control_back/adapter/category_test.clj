(ns household-financial-control-back.adapter.category-test
  (:require [clojure.test :refer :all]
            [household-financial-control-back.adapter.category :as adapter.category]))

(deftest wire-create-new-category->internal-category-test
  (testing "converts wire payload to internal category"
    (is (= {:name "Alimentação"}
           (adapter.category/wire-create-new-category->internal-category {:name "Alimentação"})))))

(deftest internal-categories->wire-return-all-categories-test
  (testing "converts list of internal categories to wire output"
    (is (= {:categories [{:id 1 :name "Alimentação"}
                         {:id 2 :name "Transporte"}]}
           (adapter.category/internal-categories->wire-return-all-categories
             [{:id 1 :name "Alimentação"}
              {:id 2 :name "Transporte"}]))))

  (testing "returns empty list when no categories"
    (is (= {:categories []}
           (adapter.category/internal-categories->wire-return-all-categories [])))))

