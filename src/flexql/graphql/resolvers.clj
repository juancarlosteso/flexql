(ns flexql.graphql.resolvers
  (:require [flexql.config :as cfg]
            [flexql.db.core :as db]
            [honey.sql.helpers :as sqlh]))

(defn- game-index [_ _ _]
  (let [connection (-> cfg/config :db db/connection)]
    (db/execute! connection (-> (sqlh/select :*) (sqlh/from :board-game)))))

(def resolvers-map
  {:games/all game-index})