(ns flexql.db.schema.game-rating
  (:require [honey.sql.helpers :refer [create-table drop-table with-columns]]))

(def destroy
  (drop-table :if-exists :game-rating))

(def create
  (create-table
   :game-rating
   (with-columns
     [:game-id :uuid [:not nil]]
     [:member-id :uuid [:not nil]]
     [:rating :int [:not nil]]
     [:created-at :timestamp]
     [:updated-at :timestamp])))