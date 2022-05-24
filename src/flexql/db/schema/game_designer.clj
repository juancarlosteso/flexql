(ns flexql.db.schema.game-designer
  (:require [honey.sql.helpers :refer [create-table drop-table with-columns]]))

(def destroy
  (drop-table :if-exists :game-designer))

(def create
  (create-table
   :game-designer
   (with-columns
     [:game-id :uuid [:not nil]]
     [:designer-id :uuid [:not nil]])))