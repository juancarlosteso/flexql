(ns flexql.graphql.resolvers.board-game
  (:require [flexql.db.core :as db]
            [honey.sql.helpers :as sqlh]))

(defn index [{:keys [dbconn]} _ _]
  (db/execute! dbconn (-> (sqlh/select :*) (sqlh/from :board-game))))