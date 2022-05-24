(ns flexql.db.seeds.member
  (:require [flexql.model.member :refer [->member]]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :member))

(def seeds
  {:bunny  (->member "curiousattemptbunny")
   :edge   (->member "bleedingedge")
   :missyo (->member "missyo")})

(def feed
  (-> (insert-into :member)
      (values (vals seeds))))
