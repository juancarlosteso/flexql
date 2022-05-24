(ns flexql.db.seeds.game-rating
  (:require [flexql.db.seeds.board-game :as game]
            [flexql.db.seeds.member :as member]
            [flexql.db.utils :refer [now]]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :game-rating))

(defn- rated-by [member game rating]
  (let [timestamp (now)]
    {:game-id   (-> game/seeds game :id)
     :member-id (-> member/seeds member :id)
     :rating rating
     :created-at timestamp
     :updated-at timestamp}))

(def seeds
  [(rated-by :bunny :zertz 3)
   (rated-by :edge :zertz 5)
   (rated-by :edge :tiny 4)
   (rated-by :edge :wonders 4)
   (rated-by :missyo :wonders 4)
   (rated-by :bunny :wonders 5)])

(def feed
  (-> (insert-into :game-rating)
      (values seeds)))
