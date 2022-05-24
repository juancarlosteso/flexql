(ns flexql.db.seeds.game-designer
  (:require [flexql.db.seeds.board-game :as game]
            [flexql.db.seeds.designer :as designer]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :game-designer))

(defn- designed-by [designer game]
  {:game-id     (-> game/seeds game :id)
   :designer-id (-> designer/seeds designer :id)})

(def seeds
  [(designed-by :burm :zertz)
   (designed-by :bauza :wonders)
   (designed-by :donald :dominion)
   (designed-by :almes :tiny)
   (designed-by :cathala :wonders)])

(def feed
  (-> (insert-into :game-designer)
      (values seeds)))
