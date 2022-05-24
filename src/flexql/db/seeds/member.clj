(ns flexql.db.seeds.member
  (:require [flexql.db.utils :refer [now]]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :member))

(defn- new-member [name]
  (let [timestamp (now)]
    {:id (random-uuid)
     :name name
     :created-at timestamp
     :updated-at timestamp}))

(def seeds
  {:bunny  (new-member "curiousattemptbunny")
   :edge   (new-member "bleedingedge")
   :missyo (new-member "missyo")})

(def feed
  (-> (insert-into :member)
      (values (vals seeds))))
