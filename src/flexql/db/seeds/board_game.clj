(ns flexql.db.seeds.board-game
  (:require [flexql.model.board-game :refer [->board-game]]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :board-game))

(def seeds
  {:zertz    (->board-game "Zertz" "Two player abstract with forced moves and shrinking board" 2 2)
   :dominion (->board-game "Dominion" "Created the deck-building genre; zillions of expansions" 2 nil)
   :tiny     (->board-game "Tiny Epic Galaxies" "Fast dice-based sci-fi space game with a bit of chaos" 1 4)
   :wonders  (->board-game "7 Wonders: Duel" "Tense, quick card game of developing civilizations" 2 2)})

(def feed
  (-> (insert-into :board-game)
      (values (vals seeds))))