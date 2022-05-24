(ns flexql.db.schema.board-game
  (:require [honey.sql.helpers :refer [create-table drop-table with-columns]]))

(def destroy
  (drop-table :if-exists :board-game))

(def create
  (create-table
   :board-game
   (with-columns
     [:id :uuid [:not nil] [:primary-key]]
     [:name :text [:not nil]]
     [:summary :text]
     [:min-players :int]
     [:max-players :int]
     [:created-at :timestamp]
     [:updated-at :timestamp])))