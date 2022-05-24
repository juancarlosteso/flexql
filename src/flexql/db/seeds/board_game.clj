(ns flexql.db.seeds.board-game
  (:require [flexql.db.utils :refer [now]]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :board-game))

(defn- new-game [name summary min-players max-players]
  (let [timestamp (now)]
    {:id (random-uuid)
     :name name
     :summary summary
     :min-players min-players
     :max-players max-players
     :created-at timestamp
     :updated-at timestamp}))

(def seeds
  {:zertz    (new-game "Zertz" "Two player abstract with forced moves and shrinking board" 2 2)
   :dominion (new-game "Dominion" "Created the deck-building genre; zillions of expansions" 2 nil)
   :tiny     (new-game "Tiny Epic Galaxies" "Fast dice-based sci-fi space game with a bit of chaos" 1 4)
   :wonders  (new-game "7 Wonders: Duel" "Tense, quick card game of developing civilizations" 2 2)})

(def feed
  (-> (insert-into :board-game)
      (values (vals seeds))))